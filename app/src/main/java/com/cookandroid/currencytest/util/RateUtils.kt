package com.cookandroid.currencytest.util

import com.cookandroid.currencytest.model.CurrencyCard

object RateUtils {
    fun computeRate(list: List<CurrencyCard>, base: String, target: String): Double {
        if (list.isEmpty()) return 0.0
        val baseCard = list.find { it.code.startsWith(base) }
        val targetCard = list.find { it.code.startsWith(target) }
        val baseToKrw = baseCard?.rate?.let { adjustPerUnit(baseCard.code, it) } ?: return 0.0
        if (target == "KRW") return baseToKrw
        val targetToKrw = targetCard?.rate?.let { adjustPerUnit(targetCard.code, it) } ?: return 0.0
        return baseToKrw / targetToKrw
    }

    private fun adjustPerUnit(code: String, rate: Double): Double {
        return if (code.contains("100")) rate / 100.0 else rate
    }
}
