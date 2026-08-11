package com.example.ebank

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val root = findViewById<View>(R.id.root_home)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        // Share the receipt via the system share sheet.
        findViewById<View>(R.id.btn_share).setOnClickListener {
            val receiptText = getString(
                R.string.success_subtitle
            ) + "\n" + getString(R.string.success_reference_label) +
                    ": " + getString(R.string.success_reference_value)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, receiptText)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.success_button_share)))
        }

        // Done: the transfer flow is complete, so clear Transfer/Confirm/Success
        // off the stack and return to the existing HomeActivity instance.
        findViewById<View>(R.id.btn_done).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}