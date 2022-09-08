package com.jarekbank.domain.account.wallet

import arrow.core.Either
import com.jarekbank.response.FailureResponse
import reactor.core.publisher.Mono

internal interface WalletService {
    fun getWallet(pesel: String): Mono<Either<FailureResponse, WalletResponse>>
}