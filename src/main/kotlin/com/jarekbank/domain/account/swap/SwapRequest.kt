package com.jarekbank.domain.account.swap

import com.jarekbank.response.Response
import java.math.BigDecimal

data class SwapRequest(
    val from: String,
    val to: String,
    val amount: BigDecimal,
): Response()