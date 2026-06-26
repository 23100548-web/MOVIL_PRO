package com.exchangepro.moviles.data.repository

import com.exchangepro.moviles.domain.model.CurrencyCode
import com.exchangepro.moviles.domain.model.ExchangeRate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import kotlin.math.round

class ExchangeRateRepository {
    private val currencies = listOf(CurrencyCode.USD, CurrencyCode.EUR, CurrencyCode.GBP, CurrencyCode.JPY)

    suspend fun getRates(): List<ExchangeRate> = withContext(Dispatchers.IO) {
        runCatching {
            val today = LocalDate.now()
            val todayRates = fetchRates(today)
            val yesterdayRates = fetchRates(today.minusDays(1))
            val spread = 0.03

            currencies.mapNotNull { currency ->
                val todayValue = todayRates[currency.name]?.takeIf { it > 0.0 } ?: return@mapNotNull null
                val yesterdayValue = yesterdayRates[currency.name]?.takeIf { it > 0.0 } ?: todayValue
                val mid = round4(1 / todayValue)
                val previousMid = round4(1 / yesterdayValue)
                val direction = when {
                    mid > previousMid -> "sube"
                    mid < previousMid -> "baja"
                    else -> "estable"
                }

                ExchangeRate(
                    code = currency,
                    mid = mid,
                    buy = round3(mid - spread),
                    sell = round3(mid + spread),
                    direction = direction
                )
            }
        }.getOrElse { fallbackRates() }
    }

    private fun fetchRates(date: LocalDate): Map<String, Double> {
        val symbols = currencies.joinToString(",") { it.name }
        val connection = URL("https://api.frankfurter.app/$date?from=PEN&to=$symbols")
            .openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 8_000
        connection.readTimeout = 8_000

        return connection.inputStream.bufferedReader().use { reader ->
            val rates = JSONObject(reader.readText()).getJSONObject("rates")
            currencies.associate { currency -> currency.name to rates.getDouble(currency.name) }
        }
    }

    private fun fallbackRates(): List<ExchangeRate> = listOf(
        ExchangeRate(CurrencyCode.USD, mid = 3.762, buy = 3.732, sell = 3.792, direction = "estable"),
        ExchangeRate(CurrencyCode.EUR, mid = 4.090, buy = 4.060, sell = 4.120, direction = "estable"),
        ExchangeRate(CurrencyCode.GBP, mid = 4.782, buy = 4.752, sell = 4.812, direction = "estable"),
        ExchangeRate(CurrencyCode.JPY, mid = 0.024, buy = 0.0, sell = 0.054, direction = "estable")
    )

    private fun round3(value: Double): Double = round(value * 1000) / 1000

    private fun round4(value: Double): Double = round(value * 10000) / 10000
}
