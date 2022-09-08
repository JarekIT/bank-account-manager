package com.jarekbank.domain.account.wallet

import com.jarekbank.domain.user.User
import com.jarekbank.domain.user.UserFacade
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.math.BigDecimal

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class WalletServiceTest {

    @MockBean
    lateinit var userFacade: UserFacade

    lateinit var service: WalletService

    @BeforeAll
    fun prepare() {
        service = WalletServiceImpl(userFacade)
    }


    @Nested
    inner class Wallet {

        @Test
        fun `new user should have empty wallet with one currency PLN`() {
            val user = User(
                pesel = "",
                name = "",
                surename = "",
                password = "",
            )

            // when
            Mockito.`when`(userFacade.findByPesel(user.pesel))
                .thenReturn(user.toMono())

            val response = service.getWallet(user.pesel)

            StepVerifier.create(response)
                .consumeNextWith {
                    println(it)

                    Assertions.assertTrue(it.isRight())

                    val responseRight = it.orNull() ?: return@consumeNextWith Assertions.assertTrue(it.orNull() is WalletResponse)
                    val responseWallet = responseRight.wallet

                    Assertions.assertAll({
                        Assertions.assertEquals(1, responseWallet.size)
                        assertEqualsTwoBigDecimals(BigDecimal.ZERO, responseWallet["PLN"])
                    })
                }
                .verifyComplete()
        }


        @Test
        fun `get wallet should return wallet with currencies and amounts `() {

            val user = User(
                pesel = "",
                name = "",
                surename = "",
                password = "",
                wallet = mutableMapOf(
                    "PLN" to BigDecimal(500),
                    "USD" to BigDecimal(100),
                    "EUR" to BigDecimal(0),
                ),
            )


            // when
            Mockito.`when`(userFacade.findByPesel(user.pesel))
                .thenReturn(user.toMono())

            val response = service.getWallet(user.pesel)

            StepVerifier.create(response)
                .consumeNextWith {
                    println(it)

                    Assertions.assertTrue(it.isRight())

                    val responseRight = it.orNull() ?: return@consumeNextWith Assertions.assertTrue(it.orNull() is WalletResponse)
                    val responseWallet = responseRight.wallet

                    Assertions.assertAll({
                        Assertions.assertEquals(3, responseWallet.size)
                        Assertions.assertEquals(user.wallet.size, responseWallet.size)
                        assertEqualsTwoBigDecimals(user.wallet["PLN"], responseWallet["PLN"])
                        assertEqualsTwoBigDecimals(user.wallet["USD"], responseWallet["USD"])
                        assertEqualsTwoBigDecimals(user.wallet["EUR"], responseWallet["EUR"])
                    })
                }
                .verifyComplete()
        }

    }


    private fun assertEqualsTwoBigDecimals(expected: BigDecimal?, received: BigDecimal?) {
        if (expected == null) return Assertions.assertNotNull(expected)
        MatcherAssert.assertThat(expected, Matchers.comparesEqualTo(received))
    }
}