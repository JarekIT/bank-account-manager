package com.jarekbank.domain.user

import com.jarekbank.response.Response

data class UserLoginResponse(val token: String) : Response()