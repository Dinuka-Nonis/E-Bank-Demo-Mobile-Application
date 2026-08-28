package com.example.ebank

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.ebank.databinding.FragmentTransferBinding

class TransferFragment : Fragment() {

    private var _binding: FragmentTransferBinding? = null
    private val binding get() = _binding!!

    // TODO: wire this to the signed-in account's real balance; HomeActivity never actually
    // passed EXTRA_AVAILABLE_BALANCE before, so this default is exactly what was in effect.
    private val availableBalance: Double = DEFAULT_AVAILABLE_BALANCE

    private data class QuickRecipient(val account: String, val name: String)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootHome) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        setupAmountFormatting()

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

        // Back button doubles as Cancel -> returns to Dashboard via the back stack.
        binding.btnCancel.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
            onSubmitTransfer()
        }
    }

    private fun setupAmountFormatting() {
        binding.etAmount.setText(formatAmount(cleanAmountInput(binding.etAmount.text.toString())))

        binding.etAmount.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (isFormatting || editable == null) return
                isFormatting = true

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
        tappedAvatar.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_avatar_selected)
    }

    private fun clearAvatarSelection() {
        val default = ContextCompat.getDrawable(requireContext(), R.drawable.bg_avatar_default)
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
        (requireActivity() as MainActivity).showConfirmationFragment(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DEFAULT_AVAILABLE_BALANCE = 125000.00
        private const val KASUN_ACCOUNT = "8001234567"
        private const val PRIYA_ACCOUNT = "8009876543"
        private const val RAVINDU_ACCOUNT = "8005551212"
    }
}
