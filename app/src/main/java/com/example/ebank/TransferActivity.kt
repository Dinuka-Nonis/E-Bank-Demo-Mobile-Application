// TransferActivity.kt
package com.example.ebank

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class TransferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val root = findViewById<View>(R.id.root_home)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        // Back button: return to HomeActivity.
        // TransferActivity is always launched from HomeActivity, so it's
        // already on the back stack — finish() just pops back to it.
        val btnBack = findViewById<View>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }
    }
}