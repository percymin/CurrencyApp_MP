package com.cookandroid.currencytest.network

import com.google.gson.annotations.SerializedName

// API 응답 형태: {"result":"success", "base_code":"USD", "conversion_rates": {"KRW":1300...}}
data class ExchangeRateResponse(
    @SerializedName("result") val result: String,
    @SerializedName("base_code") val baseCode: String,
    @SerializedName("conversion_rates") val rates: Map<String, Double>
)