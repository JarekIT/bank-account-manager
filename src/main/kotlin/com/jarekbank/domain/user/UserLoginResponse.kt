package com.jarekbank.domain.user

import com.jarekbank.response.Response

data class UserLoginResponse(val user: User, val token: String) : Response()