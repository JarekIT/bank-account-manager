package com.jarekbank.domain.user

data class UserSignUpRequest(
    val pesel: String,
    val password: String,
    val name: String,
    val surname: String,
)