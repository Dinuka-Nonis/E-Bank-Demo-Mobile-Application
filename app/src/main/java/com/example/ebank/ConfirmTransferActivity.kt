package com.example.ebank

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class ConfirmTransferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_transfer)

        val root = findViewById<View>(R.id.root_home)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        // Back button and "Edit transfer": both just return to TransferActivity,
        // which is already underneath on the back stack, so the entered
        // amount/note are still there to edit.
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<View>(R.id.tv_edit_transfer).setOnClickListener { finish() }

        // Confirm & send -> Success screen. finish() removes this screen from
        // the back stack so pressing back from Success won't return here.
        findViewById<View>(R.id.btn_confirm_send).setOnClickListener {
            startActivity(Intent(this, SuccessActivity::class.java))
            finish()
        }

        // Cancel abandons the whole transfer flow and returns to the
        // existing HomeActivity instance, clearing Transfer/Confirm off the stack.
        findViewById<View>(R.id.tv_cancel).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}