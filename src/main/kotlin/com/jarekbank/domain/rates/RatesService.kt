package com.jarekbank.domain.rates

import java.math.BigDecimal

internal interface RatesService {
    fun getRate(currency: String): BigDecimal?
}

