package com.cookandroid.currencytest

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.currencytest.databinding.FragmentAiBinding
import com.cookandroid.currencytest.ui.MessageAdapter

class AiFragment : Fragment() {

    private var binding: FragmentAiBinding? = null
    // Activity와 데이터를 공유하는 ViewModel
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val b = FragmentAiBinding.inflate(inflater, container, false)
        binding = b

        // 1. 어댑터 초기화 (ViewModel에 있는 대화 목록으로 시작)
        adapter = MessageAdapter(viewModel.chatMessages.value ?: mutableListOf())
        b.messageRecycler.layoutManager = LinearLayoutManager(requireContext()).apply { stackFromEnd = true }
        b.messageRecycler.adapter = adapter

        // 2. 대화 목록 관찰 (새 메시지 오면 갱신)
        viewModel.chatMessages.observe(viewLifecycleOwner) { messages ->
            // 리스트 전체를 갱신하는 것보다 효율적인 방법이 있지만, 간단히 구현
            // 실제로는 어댑터 내부 리스트를 교체하거나 DiffUtil 사용 권장
            adapter = MessageAdapter(messages)
            b.messageRecycler.adapter = adapter
            scrollToBottom()
        }

        // 3. 로딩 상태 관찰 (AI 생각 중일 때 버튼 비활성화 등)
        viewModel.isAiLoading.observe(viewLifecycleOwner) { isLoading ->
            b.btnSend.isEnabled = !isLoading
            b.inputMessage.isEnabled = !isLoading
            if (isLoading) {
                b.inputMessage.hint = "AI가 분석 중입니다..."
            } else {
                b.inputMessage.hint = "환율에 대해 물어보세요..."
                b.inputMessage.requestFocus()
            }
        }

        // 전송 버튼
        b.btnSend.setOnClickListener { handleSend() }

        // 엔터키 입력
        b.inputMessage.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                handleSend()
                true
            } else false
        }

        // 추천 질문 버튼
        b.btnSuggestion1.setOnClickListener { sendMessage("오늘 달러 환율은 어때?") }
        b.btnSuggestion2.setOnClickListener { sendMessage("지금 엔화 사는 게 좋을까?") }
        b.btnSuggestion3.setOnClickListener { sendMessage("환율 변동성이 큰 통화는 뭐야?") }

        return b.root
    }

    private fun handleSend() {
        val text = binding?.inputMessage?.text?.toString()?.trim().orEmpty()
        if (text.isEmpty()) return
        sendMessage(text)
    }

    private fun sendMessage(text: String) {
        viewModel.askAi(text) // ViewModel에게 처리 위임
        binding?.inputMessage?.setText("")
    }

    private fun scrollToBottom() {
        binding?.messageRecycler?.post {
            adapter.itemCount.takeIf { it > 0 }?.let {
                binding?.messageRecycler?.smoothScrollToPosition(it - 1)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}