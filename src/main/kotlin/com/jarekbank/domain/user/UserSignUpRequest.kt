package com.jarekbank.domain.user

import java.math.BigDecimal

data class UserSignUpRequest(
    val pesel: String,
    val password: String,
    val name: String,
    val surname: String,
    val deposit: BigDecimal,
)