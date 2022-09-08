package com.jarekbank.authentications

import com.jarekbank.domain.user.Permission
import com.jarekbank.domain.user.User
import com.jarekbank.domain.user.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.Authentication
import java.util.*

object TokenProvider {
    private const val ACCESS_TOKEN_VALIDITY_SECONDS = 60L * 60  // 1h

    private const val SIGNING_KEY: String = "lubieplacki-lubieplacki-lubieplacki"
    private const val PERMISSIONS_KEY = "permissions"
    private const val ROLES_KEY = "roles"

    private val key = Keys.hmacShaKeyFor(SIGNING_KEY.toByteArray())

    fun generateToken(user: User, customTime: Long? = null): String = Jwts.builder()
        .setSubject(user.pesel)
        .setIssuedAt(Date())
        .setExpiration(Date(System.currentTimeMillis() + (customTime ?: ACCESS_TOKEN_VALIDITY_SECONDS) * 1000L))
        .claim(ROLES_KEY, listOf(user.role.name))
        .claim(PERMISSIONS_KEY, user.permissions.map(Permission::name))
        .signWith(key)
        .compact()

    fun getUsernameFromToken(token: String): String =
        getClaimFromToken(token) { claims: Claims -> claims.subject }

    fun getExpirationDateFromToken(token: String): Date =
        getClaimFromToken(token) { claims: Claims -> claims.expiration }

    fun getRolesKeyFromToken(token: String): List<String> =
        @Suppress("UNCHECKED_CAST")
        getClaimFromToken(token) { claims: Claims -> claims[ROLES_KEY] as? List<String> ?: listOf() }

    fun isTokenExpired(token: String): Boolean =
        getExpirationDateFromToken(token).before(Date())

    private fun <T> getClaimFromToken(token: String, claimsResolver: (Claims) -> T): T =
        claimsResolver.invoke(getAllClaimsFromToken(token))

    @Deprecated("deprecated methods")
    private fun getAllClaimsFromTokenDeprecated(token: String): Claims =
        Jwts.parser()
            .setSigningKey(SIGNING_KEY)
            .parseClaimsJws(token)
            .body

    private fun getAllClaimsFromToken(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

    fun checkRoleFromToken(authentication: Authentication, role: UserRole): Boolean =
        authentication.authorities.stream()
            .map { it.authority }
            .anyMatch { it == role.toString() }

    fun getRoleFromToken(authentication: Authentication): String {
        return authentication.authorities.stream()
            .map { it.authority }
            .limit(1)
            .toString()
            .ifEmpty { UserRole.USER.toString() }
    }
}
