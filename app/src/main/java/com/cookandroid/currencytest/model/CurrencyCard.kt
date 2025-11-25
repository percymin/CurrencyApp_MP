package com.cookandroid.currencytest.model

data class CurrencyCard(
    val name: String,
    val code: String,
    val rate: Double,
    val change: Double,
    val changePercent: Double,
    val data: List<Double>,
    val isPositive: Boolean
)
