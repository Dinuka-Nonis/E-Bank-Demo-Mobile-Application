package com.example.ebank

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

/**
 * Lab 7: biometric login. On start the app checks whether a Class 3 (BIOMETRIC_STRONG)
 * sensor with an enrolled fingerprint/face is available and shows the system BiometricPrompt.
 * If biometrics are unavailable, cancelled or locked out, the password form is revealed instead.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                // Recognised fingerprint/face -> straight into the app.
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    openMain()
                }

                // The dialog is closing for good (negative button, cancel, lockout, ...),
                // so this is where the fallback belongs.
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Tapping "Use password instead" / cancelling is a choice, not a problem,
                    // so only surface the message for real errors such as a lockout.
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                        errorCode != BiometricPrompt.ERROR_USER_CANCELED
                    ) {
                        Toast.makeText(this@LoginActivity, errString, Toast.LENGTH_SHORT).show()
                    }
                    showPasswordForm()
                }

                // One misread attempt (e.g. wrong finger): the dialog stays open, so do nothing
                // except tell the user to retry. No fallback here.
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        this@LoginActivity,
                        "Fingerprint not recognized, try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Sign in to EBank")
            .setSubtitle("Use your fingerprint or face to continue")
            .setNegativeButtonText("Use password instead")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        // The existing "Log in with Face ID" button re-opens the prompt after a fallback.
        findViewById<View>(R.id.btnFaceId).setOnClickListener {
            if (canUseBiometrics()) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Toast.makeText(
                    this, "Biometric login is not available. Use your password.", Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (canUseBiometrics()) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            showPasswordForm()
        }
    }

    private fun canUseBiometrics(): Boolean {
        val biometricManager = BiometricManager.from(this)
        val result = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        )
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun showPasswordForm() {
        findViewById<View>(R.id.passwordFormGroup).visibility = View.VISIBLE
        // btnLogin is a TextView in this layout (not a Button), so look it up as a View.
        findViewById<View>(R.id.btnLogin).setOnClickListener { openMain() }
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}