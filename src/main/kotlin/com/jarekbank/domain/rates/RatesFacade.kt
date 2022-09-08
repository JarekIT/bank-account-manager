package com.jarekbank.domain.rates

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class RatesFacade : RatesService {

    @Autowired
    private lateinit var rates: NbpRatesCachedService

    override fun getRate(currency: String): BigDecimal? =
        rates.getRate(currency)

}