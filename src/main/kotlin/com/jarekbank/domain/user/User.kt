package com.jarekbank.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

@Document
data class User(
    @Id var pesel: String,
    var name: String,
    var surename: String,
    @JsonIgnore
    var password: String,
    val wallet: MutableMap<String, BigDecimal> = mutableMapOf("PLN" to BigDecimal.ZERO),
    var role: UserRole = UserRole.USER,
    val permissions: List<Permission> = listOf(),
    val created: Instant = Instant.now(),
) {

    fun swap(fromCurrency: String, amountToSend: BigDecimal, toCurrency: String, amountToReceive: BigDecimal) =
        this.apply {
            wallet[fromCurrency] = wallet.getOrDefault(fromCurrency, BigDecimal.ZERO).minus(amountToSend)
            wallet[toCurrency] = wallet.getOrDefault(toCurrency, BigDecimal.ZERO).plus(amountToReceive)
        }
}

data class Permission(
    val name: String,
    val description: String?
)

enum class UserRole {
    ADMIN, USER,
}
