package com.jarekbank.domain.account.swap

import arrow.core.Either
import com.jarekbank.response.FailureResponse
import reactor.core.publisher.Mono
import java.math.BigDecimal

internal interface SwapService {
    fun calculate(from: String, to: String, amount: BigDecimal): Mono<Either<FailureResponse, SwapResponse>>
    fun swap(pesel: String, from: String, to: String, amount: BigDecimal): Mono<Either<FailureResponse, SwapResponse>>
}