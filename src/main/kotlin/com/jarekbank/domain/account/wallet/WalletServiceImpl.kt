package com.jarekbank.domain.account.wallet

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.jarekbank.domain.user.UserFacade
import com.jarekbank.response.AccountNotExistsFailureResponse
import com.jarekbank.response.FailureResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
internal class WalletServiceImpl(
    private val userFacade: UserFacade,
): WalletService {
    override fun getWallet(pesel: String): Mono<Either<FailureResponse, WalletResponse>> =
        userFacade.findByPesel(pesel)
            .map { WalletResponse(it.wallet).right() as Either<FailureResponse, WalletResponse> }
            .defaultIfEmpty(AccountNotExistsFailureResponse().left())
}

