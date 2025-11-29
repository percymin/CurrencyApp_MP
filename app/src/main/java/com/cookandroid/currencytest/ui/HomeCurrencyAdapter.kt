package com.cookandroid.currencytest.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.currencytest.R
import com.cookandroid.currencytest.databinding.ItemCurrencyCardBinding
import com.cookandroid.currencytest.model.CurrencyCard

class HomeCurrencyAdapter(
    private val items: MutableList<CurrencyCard>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<HomeCurrencyAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding = ItemCurrencyCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(items[position], onClick)
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<CurrencyCard>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class CurrencyViewHolder(private val binding: ItemCurrencyCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CurrencyCard, onClick: (String) -> Unit) {
            binding.currencyTitle.text = "${item.name} ${item.code}"
            binding.rateValue.text = String.format("%,.2f", item.rate)
            val icon = if (item.isPositive) "▲" else "▼"
            val color = binding.root.context.getColor(if (item.isPositive) R.color.riseRed else R.color.fallBlue)
            binding.changeIcon.text = icon
            binding.changeIcon.setTextColor(color)
            binding.changeText.text = "$icon ${String.format("%.2f", item.change)} ${String.format("%.2f", item.changePercent)}%"
            binding.changeText.setTextColor(color)
            val sparkData = item.data.map { it.toFloat() }
            binding.sparkline.setData(sparkData, item.isPositive, R.color.riseRed, R.color.fallBlue)
            binding.root.setOnClickListener { onClick(item.code) }
        }
    }
}
