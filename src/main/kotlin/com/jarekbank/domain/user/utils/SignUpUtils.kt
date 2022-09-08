package com.jarekbank.domain.user.utils

import com.jarekbank.domain.user.User
import com.jarekbank.domain.user.UserRole
import com.jarekbank.domain.user.UserSignUpRequest

internal object SignUpUtils {

    fun singUpAsUser(signup: UserSignUpRequest, encryptedPassword: String) =
        User(
            pesel = signup.pesel,
            name = signup.name,
            surename = signup.surname,
            password = encryptedPassword,
            role = UserRole.USER,
        )
}