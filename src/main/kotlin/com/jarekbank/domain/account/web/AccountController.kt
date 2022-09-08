package com.jarekbank.domain.account.web

import arrow.core.getOrHandle
import com.jarekbank.domain.account.AccountFacade
import com.jarekbank.domain.account.swap.SwapRequest
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/account")
class AccountController(
    private val accountFacade: AccountFacade,
) {

    @GetMapping("/wallet")
    fun getWallet(authentication: Authentication) =
        accountFacade.getWallet(authentication.name)
            .map { it.getOrHandle { it } }

    @GetMapping("/calculate")
    fun calculate(authentication: Authentication, @RequestBody request: SwapRequest) =
        accountFacade.calculate(request.from, request.to, request.amount)
            .map { it.getOrHandle { it } }

    @PostMapping("/swap")
    fun swap(authentication: Authentication, @RequestBody request: SwapRequest) =
        accountFacade.swap(authentication.name, request.from, request.to, request.amount)
            .map { it.getOrHandle { it } }
}