package com.example.ebank

import android.content.Intent
import android.os.Bundle
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

    private data class QuickRecipient(val account: String, val name: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootHome) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

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
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            binding.etAmount.error = "Enter a valid amount greater than 0"
            return
        }

        val request = TransferRequest(account, name, amount, remarks)

        val intent = Intent(this, ConfirmTransferActivity::class.java)
        intent.putExtra(EXTRA_TRANSFER_REQUEST, request)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_TRANSFER_REQUEST = "extra_transfer_request"
        private const val KASUN_ACCOUNT = "8001234567"
        private const val PRIYA_ACCOUNT = "8009876543"
        private const val RAVINDU_ACCOUNT = "8005551212"
    }
}