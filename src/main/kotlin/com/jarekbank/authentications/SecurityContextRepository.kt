package com.jarekbank.authentications

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecurityContextRepository(private val authManager: UserAuthManager): ServerSecurityContextRepository {

    private val TOKEN_PREFIX = "Bearer "

    override fun save(swe: ServerWebExchange?, sc: SecurityContext?): Mono<Void> {
        TODO("not implemented")
    }

    override fun load(swe: ServerWebExchange?): Mono<SecurityContext> {
        val request = swe?.request
        val authHeader = request?.headers?.getFirst(HttpHeaders.AUTHORIZATION)

        return if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            val authToken = authHeader.replace(TOKEN_PREFIX, "")
            val auth = UsernamePasswordAuthenticationToken(authToken, authToken)

            authManager.authenticate(auth)
                .map(::SecurityContextImpl)
        } else {
            Mono.empty()
        }
    }

}