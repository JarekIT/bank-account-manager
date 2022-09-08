package com.jarekbank.domain.user.utils

import com.jarekbank.authentications.TokenProvider
import com.jarekbank.domain.user.User
import org.springframework.context.ApplicationContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

internal object LoginConfiguration {

    fun getAuthToken(user: User, password: String, ctx: ApplicationContext): String? {
        val passwordEncoder = ctx.getBean("passwordEncoder", BCryptPasswordEncoder::class.java)
        return if (passwordEncoder.matches(password, user.password)) TokenProvider.generateToken(user) else null
    }
}