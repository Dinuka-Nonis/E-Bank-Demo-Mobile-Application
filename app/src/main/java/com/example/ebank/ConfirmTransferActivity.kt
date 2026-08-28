package com.example.ebank

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import java.text.SimpleDateFormat
import java.util.Locale

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

        // IntentCompat.getSerializableExtra is the non-deprecated way to pull a
        // Serializable back out on all API levels (plain getSerializableExtra(String)
        // is deprecated as of API 33). Falls back to a synthetic empty request only
        // if this screen is somehow reached without the extra, instead of crashing.
        val request = IntentCompat.getSerializableExtra(
            intent, TransferActivity.EXTRA_TRANSFER_REQUEST, TransferRequest::class.java
        )

        if (request == null) {
            // Nothing to confirm — bail back rather than show a blank/garbage screen.
            finish()
            return
        }

        bindTransferRequest(request)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<View>(R.id.tv_edit_transfer).setOnClickListener { finish() }

        findViewById<View>(R.id.btn_confirm_send).setOnClickListener {
            startActivity(Intent(this, SuccessActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.tv_cancel).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun bindTransferRequest(request: TransferRequest) {
        val fee = FLAT_FEE
        val total = request.amount + fee

        findViewById<TextView>(R.id.tv_recipient_initials).text = initialsFor(request.recipientName)
        findViewById<TextView>(R.id.tv_recipient_name).text = request.recipientName
        findViewById<TextView>(R.id.tv_account_info).text = maskAccount(request.recipientAccount)

        findViewById<TextView>(R.id.tv_amount_value).text = formatCurrency(request.amount)
        findViewById<TextView>(R.id.tv_fee_value).text = formatCurrency(fee)
        findViewById<TextView>(R.id.tv_total_value).text = formatCurrency(total)

        findViewById<TextView>(R.id.tv_date_value).text = DATE_FORMAT.format(java.util.Date())
    }

    /** "Kasun Perera" -> "KP". Falls back to first two chars if there's no space. */
    private fun initialsFor(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            parts.size == 1 && parts[0].length >= 2 -> parts[0].take(2).uppercase()
            parts.size == 1 -> parts[0].uppercase()
            else -> "?"
        }
    }

    /** "8001234567" -> "Account ending in 4567" */
    private fun maskAccount(account: String): String {
        val last4 = if (account.length >= 4) account.takeLast(4) else account
        return "Account ending in $last4"
    }

    private fun formatCurrency(value: Double): String =
        String.format(Locale.US, "$%,.2f", value)

    companion object {
        private const val FLAT_FEE = 0.00
        private val DATE_FORMAT = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    }
}