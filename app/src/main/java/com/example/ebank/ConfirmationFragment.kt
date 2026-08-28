package com.example.ebank

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.BundleCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConfirmationFragment : Fragment() {

    private lateinit var transferRequest: TransferRequest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transferRequest = BundleCompat.getSerializable(
            requireArguments(), ARG_TRANSFER_REQUEST, TransferRequest::class.java
        )!!

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.root_home)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        bindTransferRequest(view)

        view.findViewById<View>(R.id.btn_back).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        view.findViewById<View>(R.id.tv_edit_transfer).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        view.findViewById<View>(R.id.btn_confirm_send).setOnClickListener {
            lifecycleScope.launch {
                AppDatabase.getInstance(requireContext()).transferDao().insert(transferRequest)
            }
            startActivity(Intent(requireContext(), SuccessActivity::class.java))
            (requireActivity() as MainActivity).showDashboardFragment()
        }

        view.findViewById<View>(R.id.tv_cancel).setOnClickListener {
            (requireActivity() as MainActivity).showDashboardFragment()
        }
    }

    private fun bindTransferRequest(view: View) {
        val fee = FLAT_FEE
        val total = transferRequest.amount + fee

        view.findViewById<TextView>(R.id.tv_recipient_initials).text = initialsFor(transferRequest.recipientName)
        view.findViewById<TextView>(R.id.tv_recipient_name).text = transferRequest.recipientName
        view.findViewById<TextView>(R.id.tv_account_info).text = maskAccount(transferRequest.recipientAccount)

        view.findViewById<TextView>(R.id.tv_amount_value).text = formatCurrency(transferRequest.amount)
        view.findViewById<TextView>(R.id.tv_fee_value).text = formatCurrency(fee)
        view.findViewById<TextView>(R.id.tv_total_value).text = formatCurrency(total)

        view.findViewById<TextView>(R.id.tv_date_value).text = DATE_FORMAT.format(Date())
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
        String.format(Locale.US, "Rs %,.2f", value)

    companion object {
        const val ARG_TRANSFER_REQUEST = "transfer_request"
        private const val FLAT_FEE = 0.00
        private val DATE_FORMAT = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    }
}
