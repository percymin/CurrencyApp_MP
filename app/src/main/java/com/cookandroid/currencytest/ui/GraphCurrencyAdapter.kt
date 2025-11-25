package com.cookandroid.currencytest.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.currencytest.R
import com.cookandroid.currencytest.databinding.ItemCurrencyCardBinding
import com.cookandroid.currencytest.model.CurrencyCard

class GraphCurrencyAdapter(
    private val items: List<CurrencyCard>
) : RecyclerView.Adapter<GraphCurrencyAdapter.GraphViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphViewHolder {
        val binding = ItemCurrencyCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GraphViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GraphViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class GraphViewHolder(private val binding: ItemCurrencyCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CurrencyCard) {
            binding.currencyTitle.text = "${item.name} ${item.code}"
            binding.rateValue.text = String.format("%,.2f", item.rate)
            val isRise = item.isPositive
            val icon = if (isRise) "▲" else "▼"
            val color = binding.root.context.getColor(if (isRise) R.color.riseGreen else R.color.fallBlue)
            binding.changeIcon.text = icon
            binding.changeIcon.setTextColor(color)
            binding.changeText.text = "$icon ${String.format("%.2f", item.change)} ${String.format("%.2f", item.changePercent)}%"
            binding.changeText.setTextColor(color)
            binding.sparkline.setData(item.data.map { it.toFloat() }, isRise, R.color.riseGreen, R.color.fallBlue)
            binding.root.isClickable = false
        }
    }
}
