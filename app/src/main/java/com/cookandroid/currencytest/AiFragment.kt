package com.cookandroid.currencytest

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.currencytest.databinding.FragmentAiBinding
import com.cookandroid.currencytest.model.Message
import com.cookandroid.currencytest.ui.MessageAdapter

class AiFragment : Fragment() {

    private var binding: FragmentAiBinding? = null
    private val messages = mutableListOf(
        Message("ai", "안녕하세요! 환율에 대해 궁금한 점이 있으시면 무엇이든 물어보세요. \uD83D\uDCCA")
    )
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val b = FragmentAiBinding.inflate(inflater, container, false)
        binding = b
        adapter = MessageAdapter(messages)
        b.messageRecycler.layoutManager = LinearLayoutManager(requireContext()).apply { stackFromEnd = true }
        b.messageRecycler.adapter = adapter

        b.btnSend.setOnClickListener { handleSend() }
        b.inputMessage.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                handleSend()
                true
            } else false
        }

        b.btnSuggestion1.setOnClickListener { b.inputMessage.setText("오늘 달러 환율은?") }
        b.btnSuggestion2.setOnClickListener { b.inputMessage.setText("엔화 전망은?") }
        b.btnSuggestion3.setOnClickListener { b.inputMessage.setText("환전하기 좋은 시점은?") }

        return b.root
    }

    private fun handleSend() {
        val text = binding?.inputMessage?.text?.toString()?.trim().orEmpty()
        if (text.isEmpty()) {
            Toast.makeText(requireContext(), "질문을 입력하세요", Toast.LENGTH_SHORT).show()
            return
        }
        addMessage(Message("user", text))
        val aiReply =
            "현재 환율 시장은 안정적인 추세를 보이고 있습니다. USD/KRW의 경우 최근 7일간 변동폭이 28원으로 낮은 편이며, 단기적으로는 1,320~1,350원 범위에서 움직일 것으로 예상됩니다. 추가로 궁금한 점이 있으시면 언제든 물어보세요! \uD83D\uDCA1"
        addMessage(Message("ai", aiReply))
        binding?.inputMessage?.setText("")
        scrollToBottom()
    }

    private fun addMessage(message: Message) {
        adapter.addMessage(message)
    }

    private fun scrollToBottom() {
        binding?.messageRecycler?.post {
            adapter.itemCount.takeIf { it > 0 }?.let {
                binding?.messageRecycler?.scrollToPosition(it - 1)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
