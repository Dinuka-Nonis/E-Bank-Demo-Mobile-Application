package com.example.ebank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class TransferHistoryAdapter(
    private val items: List<TransferRequest>
) : RecyclerView.Adapter<TransferHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvHistoryName)
        val amount: TextView = view.findViewById(R.id.tvHistoryAmount)
        val remarks: TextView = view.findViewById(R.id.tvHistoryRemarks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transfer_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = "${item.recipientName} (${item.recipientAccount})"
        holder.amount.text = String.format(Locale.US, "Rs %,.2f", item.amount)
        holder.remarks.text = item.remarks.ifEmpty { "No remarks" }
    }

    override fun getItemCount(): Int = items.size
}
