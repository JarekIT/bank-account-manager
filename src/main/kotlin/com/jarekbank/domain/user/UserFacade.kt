package com.jarekbank.domain.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserFacade {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    fun signUp(request: UserSignUpRequest) =
        userService.signUp(request)

    fun login(pesel: String, password: String) =
        userService.login(pesel, password)

    fun findByPesel(pesel: String) =
        userRepository.findByPesel(pesel)

    fun save(user: User) =
        userRepository.save(user)

}
