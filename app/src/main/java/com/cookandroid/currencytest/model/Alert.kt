package com.cookandroid.currencytest.model

import java.util.Date

data class Alert(
    val id: String,
    val baseCurrency: String,
    val targetCurrency: String,
    val targetRate: Double,
    val condition: String,
    val createdAt: Date
)
