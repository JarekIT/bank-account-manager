package com.jarekbank.response

sealed class FailureResponse(val code: Int, val error: String): Response("fail")

class UserInvalidPesel: FailureResponse(1001, "user-invalid-pesel")
class UserUnderAge: FailureResponse(1002, "user-under-age")
class AccountExistsFailureResponse: FailureResponse(1003, "account-exists")
class AccountNotExistsFailureResponse: FailureResponse(1004, "account-not-exists")
class NoAuthorization: FailureResponse(1005, "no-authorization")
class InvalidCredentials: FailureResponse(1006, "invalid-credentials")

class WalletProblemFailureResponse: FailureResponse(2001, "wallet-problem")

class CalculateProblemFailureResponse: FailureResponse(3001, "calculate-problem")
class SwapProblemFailureResponse: FailureResponse(3002, "swap-problem")
class InsufficientAmountFailureResponse: FailureResponse(3003, "insufficient-amount")

data class LockProblem(val message: String?): FailureResponse(9001, "lock-problem")
