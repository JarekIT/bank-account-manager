package com.jarekbank.domain.user

import com.jarekbank.authentications.TokenProvider
import com.jarekbank.domain.user.utils.LoginConfiguration
import com.jarekbank.domain.user.utils.PeselValidator
import com.jarekbank.domain.user.utils.SignUpUtils
import com.jarekbank.response.*
import org.springframework.context.ApplicationContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
internal class UserService(
    private val userRepository: UserRepository,
    private val applicationContext: ApplicationContext,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val peselValidator: PeselValidator,
) {

    fun signUp(request: UserSignUpRequest): Mono<out Response> =
        Mono.just(request)
            .flatMap {
                if (!peselValidator.validate(it.pesel)) return@flatMap UserInvalidPesel().toMono()
                if (!peselValidator.validateAge(it.pesel)) return@flatMap UserUnderAge().toMono()

                findUserByPesel(request.pesel)
                    .map { AccountExistsFailureResponse() as Response }
                    .switchIfEmpty(createUser(request)
                        .map { UserSignupResponse(it, TokenProvider.generateToken(it)) })
            }

    fun login(pesel: String, password: String): Mono<out Response> =
        findUserByPesel(pesel)
            .map { user ->
                LoginConfiguration.getAuthToken(user, pesel, applicationContext)
                    ?.let { token -> UserLoginResponse(token) }
                    ?: InvalidCredentials() as Response
            }
            .onErrorReturn(InvalidCredentials())
            .defaultIfEmpty(AccountNotExistsFailureResponse())

    private fun createUser(signup: UserSignUpRequest): Mono<User> =
        Mono.just(SignUpUtils.singUpAsUser(signup, passwordEncoder.encode(signup.password)))
            .flatMap { userRepository.save(it) }

    private fun findUserByPesel(pesel: String): Mono<User> =
        userRepository.findByPesel(pesel)
}

