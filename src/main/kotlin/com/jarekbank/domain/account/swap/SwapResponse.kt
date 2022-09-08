package com.jarekbank.domain.account.swap

import com.jarekbank.response.Response
import java.math.BigDecimal

data class SwapResponse(
    val receive: BigDecimal
): Response()