package com.jarekbank.domain.user

data class UserLoginRequest(
    val pesel: String,
    val password: String,
)