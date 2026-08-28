package com.example.ebank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.rootHome)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        val openTransfer = { (requireActivity() as MainActivity).showTransferFragment() }

        // Quick action icon under the balance card
        view.findViewById<View>(R.id.btnTransfer).setOnClickListener { openTransfer() }
        // Bottom navigation bar "Transfers" tab
        view.findViewById<View>(R.id.navTransfers).setOnClickListener { openTransfer() }
        // "See all" next to Recent Transactions doubles as the History entry point
        view.findViewById<View>(R.id.tvSeeAll).setOnClickListener {
            (requireActivity() as MainActivity).showHistoryFragment()
        }

        setupRecentTransactions(view)
    }

    private fun setupRecentTransactions(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvRecentTransactions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val allTransfers = AppDatabase.getInstance(requireContext()).transferDao().getAll()
            // Show only the top 3 most recent transfers on the home page
            val recentTransfers = allTransfers.take(3)
            recyclerView.adapter = TransferHistoryAdapter(recentTransfers)
        }
    }
}
