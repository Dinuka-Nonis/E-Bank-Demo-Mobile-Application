package com.example.ebank

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val root = findViewById<View>(R.id.rootHome)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        val openTransfer = {
            startActivity(Intent(this, TransferActivity::class.java))
        }

        // Quick action icon under the balance card
        findViewById<View>(R.id.btnTransfer).setOnClickListener { openTransfer() }

        // Bottom navigation bar "Transfers" tab
        findViewById<View>(R.id.navTransfers).setOnClickListener { openTransfer() }
    }
}