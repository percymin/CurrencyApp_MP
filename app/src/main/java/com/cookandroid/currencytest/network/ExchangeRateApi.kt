package com.cookandroid.currencytest.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {
    // 예: https://v6.exchangerate-api.com/v6/YOUR-API-KEY/latest/USD
    @GET("v6/{apiKey}/latest/{base}")
    suspend fun getRates(
        @Path("apiKey") apiKey: String,
        @Path("base") base: String
    ): Response<ExchangeRateResponse>
}