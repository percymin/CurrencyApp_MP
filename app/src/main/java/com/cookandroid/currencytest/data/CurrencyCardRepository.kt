package com.cookandroid.currencytest.data

import com.cookandroid.currencytest.model.CurrencyCard

object CurrencyCardRepository {
    val cards: List<CurrencyCard> = listOf(
        CurrencyCard(
            name = "미국",
            code = "USD",
            rate = 1477.30,
            change = 5.30,
            changePercent = 0.36,
            data = listOf(1468.0, 1470.5, 1472.1, 1473.2, 1474.8, 1476.0, 1477.3),
            isPositive = true
        ),
        CurrencyCard(
            name = "일본",
            code = "JPY 100",
            rate = 942.07,
            change = 0.86,
            changePercent = 0.09,
            data = listOf(940.1, 940.4, 940.9, 941.2, 941.5, 941.8, 942.07),
            isPositive = true
        ),
        CurrencyCard(
            name = "유럽연합",
            code = "EUR",
            rate = 1705.54,
            change = 10.16,
            changePercent = 0.60,
            data = listOf(1694.0, 1696.2, 1699.0, 1700.5, 1702.9, 1704.2, 1705.5),
            isPositive = true
        ),
        CurrencyCard(
            name = "중국",
            code = "CNY",
            rate = 207.94,
            change = 0.75,
            changePercent = 0.36,
            data = listOf(206.8, 207.0, 207.1, 207.3, 207.5, 207.7, 207.94),
            isPositive = true
        ),
        CurrencyCard(
            name = "영국",
            code = "GBP",
            rate = 1936.81,
            change = 7.75,
            changePercent = 0.40,
            data = listOf(1927.0, 1928.5, 1929.7, 1931.2, 1933.8, 1935.2, 1936.8),
            isPositive = true
        ),
        CurrencyCard(
            name = "호주",
            code = "AUD",
            rate = 952.93,
            change = 2.39,
            changePercent = 0.25,
            data = listOf(949.0, 949.8, 950.5, 951.1, 951.8, 952.3, 952.93),
            isPositive = true
        )
    )
}
