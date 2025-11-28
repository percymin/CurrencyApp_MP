package com.cookandroid.currencytest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookandroid.currencytest.data.CurrencyCardRepository
import com.cookandroid.currencytest.data.GeminiRepository
import com.cookandroid.currencytest.model.CurrencyCard
import com.cookandroid.currencytest.model.Message
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // 환율 데이터
    private val _currencyList = MutableLiveData<List<CurrencyCard>>()
    val currencyList: LiveData<List<CurrencyCard>> get() = _currencyList

    // AI 채팅 메시지 목록 (화면 회전해도 대화 유지되게 함)
    private val _chatMessages = MutableLiveData<MutableList<Message>>(
        mutableListOf(Message("ai", "안녕하세요! 현재 환율 데이터를 보고 있습니다. 무엇이든 물어보세요! \uD83D\uDCCA"))
    )
    val chatMessages: LiveData<MutableList<Message>> get() = _chatMessages

    // AI 로딩 상태 (답변 기다리는 중?)
    private val _isAiLoading = MutableLiveData<Boolean>(false)
    val isAiLoading: LiveData<Boolean> get() = _isAiLoading

    // [추가] 계산기 한 줄 요약 결과 저장용
    private val _conversionSummary = MutableLiveData<String>()
    val conversionSummary: LiveData<String> get() = _conversionSummary

    init {
        loadRates()
    }

    fun loadRates() {
        viewModelScope.launch {
            val result = CurrencyCardRepository.fetchRealRates()
            if (result.isNotEmpty()) {
                _currencyList.value = result
            }
        }
    }

    // ★ AI에게 질문하기
    fun askAi(question: String) {
        val currentRates = _currencyList.value
        if (currentRates.isNullOrEmpty()) {
            addMessage(Message("ai", "환율 데이터를 먼저 불러와야 합니다. 잠시만 기다려주세요."))
            return
        }

        // 1. 사용자 질문 화면에 추가
        addMessage(Message("user", question))
        _isAiLoading.value = true

        viewModelScope.launch {
            // 2. Gemini에게 물어보기 (데이터 + 질문)
            val answer = GeminiRepository.getAnalysis(currentRates, question)

            // 3. 답변 화면에 추가
            addMessage(Message("ai", answer))
            _isAiLoading.value = false
        }
    }

    private fun addMessage(msg: Message) {
        val currentList = _chatMessages.value ?: mutableListOf()
        currentList.add(msg)
        _chatMessages.value = currentList // LiveData 갱신 트리거
    }

    fun fetchConversionSummary(base: String, target: String, changePercent: Double) {
        _conversionSummary.value = "AI가 분석 중입니다... 🤖" // 로딩 표시
        viewModelScope.launch {
            val result = GeminiRepository.getSimpleAnalysis(base, target, changePercent)
            _conversionSummary.value = result
        }
    }
}