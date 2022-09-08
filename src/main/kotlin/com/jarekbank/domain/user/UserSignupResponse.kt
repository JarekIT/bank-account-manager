package com.jarekbank.domain.user

import com.jarekbank.response.Response

data class UserSignupResponse(val user: User, val token: String): Response()


