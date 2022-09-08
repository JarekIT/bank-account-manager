package com.jarekbank.domain.account.swap

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.jarekbank.domain.rates.NbpRatesCachedService
import com.jarekbank.domain.user.User
import com.jarekbank.domain.user.UserFacade
import com.jarekbank.response.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal
import java.math.RoundingMode

@Service
internal class SwapWithoutFeeServiceImpl(
    private val nbpRatesCache: NbpRatesCachedService,
    private val userFacade: UserFacade,
) : SwapService {

    private val mainCurrency = "PLN"

    /**
     * case 1 PLN -> USD
     * case 2 USD -> PLN
     */
    override fun calculate(from: String, to: String, amount: BigDecimal): Mono<Either<FailureResponse, SwapResponse>> =
        calculateRate(from, to, amount)
            ?.let { calculateReceive -> SwapResponse(calculateReceive).right().toMono() }
            ?: CalculateProblemFailureResponse().left().toMono()

    override fun swap(
        pesel: String,
        from: String,
        to: String,
        amount: BigDecimal
    ): Mono<Either<FailureResponse, SwapResponse>> =
        findUser(pesel).flatMap {
            if (isInsufficientAmount(it.wallet, from, amount))
                return@flatMap (InsufficientAmountFailureResponse().left() as Either<FailureResponse, SwapResponse>).toMono()

            val amountToReceive = calculateRate(from, to, amount)
                ?: return@flatMap (CalculateProblemFailureResponse().left() as Either<FailureResponse, SwapResponse>).toMono()

            it.swap(
                fromCurrency = from,
                amountToSend = amount,
                toCurrency = to,
                amountToReceive = amountToReceive
            )

            saveUser(it)
                .map { Either.Right(SwapResponse(amountToReceive)) as Either<FailureResponse, SwapResponse> }
                .onErrorReturn(SwapProblemFailureResponse().left())
        }
            .defaultIfEmpty(AccountNotExistsFailureResponse().left())

    private fun isInsufficientAmount(
        wallet: MutableMap<String, BigDecimal>,
        from: String,
        amount: BigDecimal
    ): Boolean = (wallet.getOrDefault(from, BigDecimal.ZERO) < amount)

    private fun calculateRate(from: String, to: String, amount: BigDecimal): BigDecimal? = when {
        from == to -> amount
        from == mainCurrency -> changeMainCurrencyToForeign(to, amount)
        to == mainCurrency -> changeForeignToMainCurrency(from, amount)
        else -> changeForeignToForeign(from, to, amount)
    }

    /**
     * PLN -> USD
     */
    private fun changeMainCurrencyToForeign(to: String, amount: BigDecimal) =
        getRate(to)?.let { amount.divide(it, 2, RoundingMode.DOWN) }

    /**
     * USD -> PLN
     */
    private fun changeForeignToMainCurrency(from: String, amount: BigDecimal) =
        getRate(from)?.multiply(amount)?.setScale(2, RoundingMode.DOWN)

    /**
     * @sample { 1 USD = 5 PLN, 1 EUR = 2,5 PLN }
     * @sample 1 USD = 2 EUR // 1 EUR = 0.5 USD // 1 USD = (5/2.5) EUR // 1 EUR = (2.5/5) USD // 1 USD = rU/rE EUR
     */
    private fun changeForeignToForeign(from: String, to: String, amount: BigDecimal): BigDecimal? {
        val rateFrom = getRate(from) ?: return null
        val rateTo = getRate(to) ?: return null
        return amount.multiply(rateFrom).divide(rateTo, 2, RoundingMode.DOWN)
    }

    private fun getRate(currency: String): BigDecimal? =
        nbpRatesCache.getRate(currency)

    private fun findUser(pesel: String): Mono<User> =
        userFacade.findByPesel(pesel)

    private fun saveUser(user: User): Mono<User> =
        userFacade.save(user)
}