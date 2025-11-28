package com.cookandroid.currencytest.data
import com.cookandroid.currencytest.BuildConfig
import android.util.Log
import com.cookandroid.currencytest.model.CurrencyCard
import com.cookandroid.currencytest.network.RetrofitClient
import kotlin.random.Random

object CurrencyCardRepository {
    // ★ 여기에 아까 발급받은 API 키를 꼭 넣어주세요!
    private val API_KEY = BuildConfig.EXCHANGE_API_KEY
    suspend fun fetchRealRates(): List<CurrencyCard> {
        return try {
            val response = RetrofitClient.api.getRates(API_KEY, "USD")

            if (response.isSuccessful && response.body() != null) {
                val rates = response.body()!!.rates

                // 1. 기준이 되는 원화(KRW) 환율 가져오기 (예: 1350.0)
                val usdToKrw = rates["KRW"] ?: 1350.0

                // 원하는 통화 목록
                val targetCodes = listOf("USD", "JPY", "EUR", "CNY", "GBP", "AUD")
                val countryNames = listOf("미국", "일본", "유럽연합", "중국", "영국", "호주")

                val list = mutableListOf<CurrencyCard>()

                targetCodes.forEachIndexed { index, code ->
                    // 2. 해당 통화의 달러 대비 환율 (예: JPY = 150.0)
                    val usdToTarget = rates[code] ?: 1.0

                    // 3. 원화 기준 환율 계산: (USD->KRW) ÷ (USD->Target)
                    // 예: 1350 / 150 = 9.0 (1엔당 9원)
                    var krewBaseRate = usdToKrw / usdToTarget

                    var displayCode = code

                    // ★ 엔화는 '100엔' 단위로 보여주는 것이 관례
                    if (code == "JPY") {
                        krewBaseRate *= 100
                        displayCode = "JPY 100"
                    }

                    // 그래프용 가짜 과거 데이터 생성 (현재 환율 기준)
                    val fakeHistory = generateFakeHistory(krewBaseRate)
                    val yesterdayRate = fakeHistory[fakeHistory.size - 2]
                    val change = krewBaseRate - yesterdayRate
                    val changePercent = (change / yesterdayRate) * 100

                    list.add(
                        CurrencyCard(
                            name = countryNames[index],
                            code = displayCode,
                            rate = krewBaseRate,
                            change = change,
                            changePercent = changePercent,
                            data = fakeHistory,
                            isPositive = change >= 0
                        )
                    )
                }
                list
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun generateFakeHistory(currentRate: Double): List<Double> {
        val history = mutableListOf<Double>()
        var tempRate = currentRate
        repeat(7) {
            history.add(tempRate)
            // 0.3% 범위 내 랜덤 변동
            val randomPercent = Random.nextDouble(-0.003, 0.003)
            tempRate = tempRate * (1.0 - randomPercent)
        }
        return history.reversed()
    }

    // 더 이상 안 쓰지만 호환성을 위해 남겨둠
    val cards = emptyList<CurrencyCard>()
}