package com.example.ebank

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import com.example.ebank.databinding.ActivityTransferBinding

class TransferActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransferBinding

    // Falls back to DEFAULT_AVAILABLE_BALANCE if HomeActivity doesn't pass the real
    // balance in via EXTRA_AVAILABLE_BALANCE. See the companion object below.
    private var availableBalance: Double = DEFAULT_AVAILABLE_BALANCE

    private data class QuickRecipient(val account: String, val name: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer)

        availableBalance = intent.getDoubleExtra(EXTRA_AVAILABLE_BALANCE, DEFAULT_AVAILABLE_BALANCE)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootHome) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        setupAmountFormatting()

        // Kasun pre-selected by default, matching the original screen.
        selectRecipient(
            QuickRecipient(KASUN_ACCOUNT, getString(R.string.transfer_recipient_kasun_name)),
            binding.recipientKasun
        )

        binding.recipientKasun.setOnClickListener {
            selectRecipient(QuickRecipient(KASUN_ACCOUNT, getString(R.string.transfer_recipient_kasun_name)), it)
        }
        binding.recipientPriya.setOnClickListener {
            selectRecipient(QuickRecipient(PRIYA_ACCOUNT, getString(R.string.transfer_recipient_priya_name)), it)
        }
        binding.recipientRavindu.setOnClickListener {
            selectRecipient(QuickRecipient(RAVINDU_ACCOUNT, getString(R.string.transfer_recipient_ravindu_name)), it)
        }
        binding.btnAddRecipient.setOnClickListener {
            binding.etRecipientAccount.setText("")
            binding.etRecipientName.setText("")
            clearAvatarSelection()
            binding.etRecipientAccount.requestFocus()
        }

        // Back button doubles as Cancel.
        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            onSubmitTransfer()
        }
    }

    /**
     * Live-formats the amount field with thousands separators (e.g. 1,500.00) as the user
     * types, keeping the cursor in a natural spot. Also blocks a second decimal point and
     * caps the fractional part at 2 digits.
     */
    private fun setupAmountFormatting() {
        // Reformat whatever the field starts with, so a preset value can never contain
        // stray characters that would fail to parse later.
        binding.etAmount.setText(formatAmount(cleanAmountInput(binding.etAmount.text.toString())))

        binding.etAmount.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (isFormatting || editable == null) return
                isFormatting = true

                // Track cursor position relative to the END of the string. Grouping commas
                // get inserted/removed to the left of the cursor as digit count changes, but
                // the distance from the end stays stable, so this keeps typing/deleting feeling
                // natural instead of jumping the cursor to the start or end.
                val cursorFromEnd = editable.length - binding.etAmount.selectionStart
                val cleaned = cleanAmountInput(editable.toString())
                val formatted = formatAmount(cleaned)

                editable.replace(0, editable.length, formatted)

                val newCursor = (formatted.length - cursorFromEnd).coerceIn(0, formatted.length)
                binding.etAmount.setSelection(newCursor)

                isFormatting = false
            }
        })
    }

    /** Strips everything except digits and a single decimal point, max 2 decimal digits. */
    private fun cleanAmountInput(input: String): String {
        val sb = StringBuilder()
        var decimalUsed = false
        for (c in input) {
            when {
                c.isDigit() -> sb.append(c)
                c == '.' && !decimalUsed -> {
                    sb.append(c)
                    decimalUsed = true
                }
            }
        }
        var cleaned = sb.toString()
        val dotIndex = cleaned.indexOf('.')
        if (dotIndex != -1 && cleaned.length - dotIndex - 1 > 2) {
            cleaned = cleaned.substring(0, dotIndex + 3)
        }
        return cleaned
    }

    /** Adds thousands separators to the integer part, e.g. "1500000.5" -> "1,500,000.5" */
    private fun formatAmount(cleaned: String): String {
        if (cleaned.isEmpty()) return cleaned

        val dotIndex = cleaned.indexOf('.')
        val integerPart = if (dotIndex == -1) cleaned else cleaned.substring(0, dotIndex)
        val fractionPart = if (dotIndex == -1) null else cleaned.substring(dotIndex + 1)

        val trimmedInteger = integerPart.trimStart('0').ifEmpty { "0" }
        val groupedInteger = trimmedInteger.reversed().chunked(3).joinToString(",").reversed()

        return if (fractionPart == null) groupedInteger else "$groupedInteger.$fractionPart"
    }

    private fun selectRecipient(recipient: QuickRecipient, tappedAvatar: View) {
        binding.etRecipientAccount.setText(recipient.account)
        binding.etRecipientName.setText(recipient.name)
        clearAvatarSelection()
        tappedAvatar.background = ContextCompat.getDrawable(this, R.drawable.bg_avatar_selected)
    }

    private fun clearAvatarSelection() {
        val default = ContextCompat.getDrawable(this, R.drawable.bg_avatar_default)
        binding.recipientKasun.background = default
        binding.recipientPriya.background = default
        binding.recipientRavindu.background = default
    }

    private fun onSubmitTransfer() {
        val account = binding.etRecipientAccount.text.toString().trim()
        val name = binding.etRecipientName.text.toString().trim()
        val amountText = binding.etAmount.text.toString().trim()
        val remarks = binding.etRemarks.text.toString().trim()

        if (account.isEmpty()) {
            binding.etRecipientAccount.error = "Enter a recipient account number"
            return
        }
        if (name.isEmpty()) {
            binding.etRecipientName.error = "Enter a recipient name"
            return
        }

        // Strip the thousands-separator commas the formatter added before parsing to a Double.
        val amount = amountText.replace(",", "").toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            binding.etAmount.error = "Enter a valid amount greater than 0"
            return
        }
        if (amount > availableBalance) {
            binding.etAmount.error = getString(R.string.transfer_error_insufficient_balance)
            return
        }

        val request = TransferRequest(account, name, amount, remarks)

        val intent = Intent(this, ConfirmTransferActivity::class.java)
        intent.putExtra(EXTRA_TRANSFER_REQUEST, request)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_TRANSFER_REQUEST = "extra_transfer_request"
        const val EXTRA_AVAILABLE_BALANCE = "extra_available_balance"

        // Fallback only, used if TransferActivity is ever launched without a real balance.
        // Have HomeActivity pass the account's actual balance via EXTRA_AVAILABLE_BALANCE.
        private const val DEFAULT_AVAILABLE_BALANCE = 125000.00

        private const val KASUN_ACCOUNT = "8001234567"
        private const val PRIYA_ACCOUNT = "8009876543"
        private const val RAVINDU_ACCOUNT = "8005551212"
    }
}