package com.cookandroid.currencytest.data

import com.cookandroid.currencytest.BuildConfig
import com.cookandroid.currencytest.model.CurrencyCard
import com.google.ai.client.generativeai.GenerativeModel

object GeminiRepository {
    // ★ 여기에 Gemini API Key 입력
    private val API_KEY = BuildConfig.GEMINI_API_KEY
    private val generativeModel = GenerativeModel(
        "gemini-2.5-flash",
        API_KEY
    )

    suspend fun getAnalysis(rates: List<CurrencyCard>, userQuestion: String): String {
        return try {
            // 1. AI에게 줄 환율 데이터 요약 문자열 만들기
            val dataSummary = rates.joinToString("\n") {
                "${it.name}(${it.code}): ${String.format("%,.2f", it.rate)}원 (${if(it.isPositive) "상승" else "하락"} ${String.format("%.2f", it.changePercent)}%)"
            }

            // 2. 프롬프트(명령어) 구성
            val prompt = """
                당신은 외환 투자 전문가입니다. 아래는 실시간 환율 데이터입니다.
                (기준: 대한민국 원화 KRW)
                
                [현재 환율 데이터]
                $dataSummary
                
                [사용자 질문]
                "$userQuestion"
                
                위 데이터를 바탕으로 사용자의 질문에 대해 전문적이고 구체적인 조언을 한국어로 해주세요.
                너무 길지 않게, 핵심을 3줄 이내로 요약해서 답변하세요.
                상승/하락 추세에 맞춰 매수/매도 타이밍을 조언해주면 더 좋습니다.
            """.trimIndent()

            // 3. Gemini에게 질문 전송
            val response = generativeModel.generateContent(prompt)
            response.text ?: "죄송합니다. 답변을 생성할 수 없습니다."
        } catch (e: Exception) {
            e.printStackTrace()
            "AI 연결 중 오류가 발생했습니다: ${e.localizedMessage}"
        }
    }
    suspend fun getSimpleAnalysis(base: String, target: String, changePercent: Double): String {
        return try {
            val trend = if (changePercent > 0) "상승" else "하락"

            // AI에게 보내는 명령 (짧고 강렬하게)
            val prompt = """
                현재 $base 대비 $target 환율이 $trend 추세입니다 (변동률: $changePercent%).
                이 상황에서 환전(매수)을 고민하는 사용자에게 투자 전문가로서 
                '지금 사라', '기다려라', '분할 매수해라' 등의 조언을
                반드시 15자 이내의 한 문장으로 강력하게 요약해서 말해줘.
                (예시: 지금이 기회입니다! 즉시 환전하세요.)
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            response.text?.trim() ?: "AI 분석 정보를 불러오지 못했습니다."
        } catch (e: Exception) {
            "잠시 후 다시 시도해주세요."
        }
    }
}
