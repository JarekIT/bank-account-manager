package com.jarekbank.domain.account.wallet

import com.jarekbank.response.Response
import java.math.BigDecimal

data class WalletResponse(
    val wallet: Map<String, BigDecimal>
): Response()