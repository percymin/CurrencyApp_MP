package com.cookandroid.currencytest.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.currencytest.databinding.ItemMessageAiBinding
import com.cookandroid.currencytest.databinding.ItemMessageUserBinding
import com.cookandroid.currencytest.model.Message

class MessageAdapter(
    private val items: MutableList<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (items[position].role == "user") TYPE_USER else TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_USER) {
            val binding = ItemMessageUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            UserViewHolder(binding)
        } else {
            val binding = ItemMessageAiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AiViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is UserViewHolder -> holder.bind(item)
            is AiViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun addMessage(message: Message) {
        items.add(message)
        notifyItemInserted(items.lastIndex)
    }

    inner class UserViewHolder(private val binding: ItemMessageUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textMessage.text = message.content
        }
    }

    inner class AiViewHolder(private val binding: ItemMessageAiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textMessage.text = message.content
        }
    }

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_AI = 1
    }
}
