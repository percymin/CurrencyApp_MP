package com.cookandroid.currencytest.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.currencytest.databinding.ItemAlertBinding
import com.cookandroid.currencytest.model.Alert
import java.text.SimpleDateFormat
import java.util.Locale

class AlertAdapter(
    private val items: MutableList<Alert>,
    private val onDelete: (Alert) -> Unit
) : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy. MM. dd", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class AlertViewHolder(private val binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alert: Alert) {
            binding.alertPair.text = "${alert.baseCurrency} → ${alert.targetCurrency}"
            val conditionText = if (alert.condition == "below") {
                "${formatRate(alert.targetRate)} 이하일 때 알림"
            } else {
                "${formatRate(alert.targetRate)} 이상일 때 알림"
            }
            binding.alertCondition.text = conditionText
            binding.alertCreated.text = "설정일: ${dateFormat.format(alert.createdAt)}"
            binding.btnDelete.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDelete(alert)
                }
            }
        }

        private fun formatRate(value: Double): String = String.format("%,.0f", value)
    }

    fun remove(alert: Alert) {
        val index = items.indexOfFirst { it.id == alert.id }
        if (index >= 0) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun add(alert: Alert) {
        items.add(alert)
        notifyDataSetChanged()
    }
}
