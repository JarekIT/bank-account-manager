package com.jarekbank.domain.rates

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jarekbank.cache.CacheService
import com.jarekbank.client.UnirestClient
import com.jarekbank.utils.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import javax.annotation.PostConstruct

@Service
internal class NbpRatesCachedService(
    private val unirestClient: UnirestClient,
) : CacheService, RatesService {

    private companion object : Logger

    @Value("\${api.rates.nbp.url}")
    private lateinit var api: String

    @Value("#{'\${filter.currencies.fiat}'.split(',')}")
    private lateinit var acceptedRates: List<String>

    private val cache: MutableMap<String, BigDecimal> = mutableMapOf()

    override fun cacheName() = "Cache Fiat Rates"

    @PostConstruct
    override fun load() {
        cacheCurrencyFiatRates()
    }

    override fun getRate(currency: String): BigDecimal? {
        return cache[currency]
    }

    /**
     * @return Map { EUR=4.4805, PLN=1, GBP=5.2044, USD=3.6724 }
     */
    private fun cacheCurrencyFiatRates() {
        cache["PLN"] = BigDecimal.ONE

        fetchRates()
            .stream()
            .filter { acceptedRates.contains(it.code) }
            .forEach { cache[it.code] = BigDecimal.valueOf(it.mid) }

        info("CACHE: ${cacheName()}: ${this.cache}")
    }

    private fun fetchRates(): List<Rate> {
        val response = unirestClient.getClient()
            .get(api)

        val responseNode = response.asJson()

        val currenciesList: Array<Rates> = jacksonObjectMapper().readValue(responseNode.body.toString())

        info("Status: ${responseNode.status} - Url: ${response.url} - ParsingProblem: ${if (responseNode.parsingError.isPresent) responseNode.parsingError.get() else ""} - Size: ${currenciesList.firstOrNull()?.rates?.size}  - Body: $currenciesList")

        return currenciesList.first().rates
    }

    private data class Rates(
        val table: String,
        val no: String,
        val effectiveDate: String,
        val rates: List<Rate>
    )

    private data class Rate(
        val currency: String,
        val code: String,
        val mid: Double
    )
}