package com.jarekbank.domain.user.web

import com.jarekbank.domain.user.UserFacade
import com.jarekbank.domain.user.UserLoginRequest
import com.jarekbank.domain.user.UserSignUpRequest
import com.jarekbank.response.Response
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/user")
class UserController(
    private val facade: UserFacade
) {

    @PostMapping("/signup")
    fun signUp(@RequestBody request: UserSignUpRequest): Mono<out Response> =
        facade.signUp(request)

    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest): Mono<out Response> =
        facade.login(request.pesel, request.password)
}