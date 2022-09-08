package com.jarekbank.domain.account.swap

import arrow.core.Either
import com.jarekbank.domain.rates.NbpRatesCachedService
import com.jarekbank.domain.user.User
import com.jarekbank.domain.user.UserFacade
import com.jarekbank.response.InsufficientAmountFailureResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.comparesEqualTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.math.BigDecimal


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SwapWithoutFeeServiceImplTest {

    @MockBean
    private lateinit var ratesFacade: NbpRatesCachedService

    @MockBean
    private lateinit var userFacade: UserFacade

    private lateinit var service: SwapService

    val mainCurrency = "PLN"


    @BeforeAll
    fun prepare() {
        service = SwapWithoutFeeServiceImpl(ratesFacade, userFacade)
    }

    @BeforeEach
    fun beforeEach() {
        `when`(ratesFacade.getRate("USD")).thenReturn(BigDecimal(5))
        `when`(ratesFacade.getRate("EUR")).thenReturn(BigDecimal(4))
    }

    @Nested
    inner class Calculate {

        @Test
        fun `should calculate properly from PLN to USD`() {
            // given
            val to = "USD"
            val amount = BigDecimal(500)
            val expected = BigDecimal(100)

            // when
            val response = service.calculate(
                from = mainCurrency,
                to = to,
                amount = amount
            )

            // then
            StepVerifier.create(response)
                .consumeNextWith {
                    println(it)

                    assertAll({
                        assertTrue(it.isRight())
                        assertTrue(comparesEqualTo(expected).matches((it as Either.Right).value.receive))
                    })
                }
                .verifyComplete()
        }

        @Test
        fun `should calculate properly from USD to PLN`() {
            // given
            val from = "USD"
            val to = mainCurrency
            val amount = BigDecimal(500)
            val expected = BigDecimal(2500)

            // when
            val response = service.calculate(
                from = from,
                to = to,
                amount = amount
            )

            // then
            StepVerifier.create(response)
                .consumeNextWith {
                    println(it)

                    assertAll({
                        assertTrue(it.isRight())
                        assertTrue(comparesEqualTo(expected).matches((it as Either.Right).value.receive))
                    })
                }
                .verifyComplete()
        }
    }

    @Nested
    inner class Swap {

        @Test
        fun `should not swap if not enough amount`() {
            val user = User(
                pesel = "",
                name = "",
                surename = "",
                password = "",
                wallet = mutableMapOf("PLN" to BigDecimal(500)),
            )

            val from = mainCurrency
            val to = "USD"
            val amount = BigDecimal(1000)

            `when`(userFacade.findByPesel(user.pesel))
                .thenReturn(user.toMono())

            `when`(userFacade.save(user))
                .thenReturn(user.toMono())

            val response = service.swap(
                user.pesel,
                from = from,
                to = to,
                amount = amount
            )

            // then
            println(user)
            StepVerifier.create(response)
                .consumeNextWith {
                    println(user)
                    println(it)

                    assertTrue(it.isLeft())
                    assertTrue((it as Either.Left).value is InsufficientAmountFailureResponse )
                }
                .verifyComplete()
        }

        @Test
        fun `after swap wallet should change amounts`() {
            // given
            val user = User(
                pesel = "",
                name = "",
                surename = "",
                password = "",
                wallet = mutableMapOf("PLN" to BigDecimal(500)),
            )

            val from = mainCurrency
            val to = "USD"
            val amount = BigDecimal(500)

            val expectedPln = BigDecimal(0)
            val expectedUsd = BigDecimal(100)

            // when
            `when`(userFacade.findByPesel(user.pesel))
                .thenReturn(user.toMono())

            `when`(userFacade.save(user))
                .thenReturn(user.toMono())

            // when
            val response = service.swap(
                user.pesel,
                from = from,
                to = to,
                amount = amount
            )

            // then
            println(user)
            StepVerifier.create(response)
                .consumeNextWith {
                    println(user)
                    println(it)

                    assertTrue(it.isRight())

                    val responseRight = it.orNull() ?: return@consumeNextWith assertTrue(false)
                    println(user.wallet)

                    assertAll({
                        assertEqualsTwoBigDecimals(expectedUsd, responseRight.receive)
                        assertEqualsTwoBigDecimals(expectedPln, user.wallet[from])
                        assertEqualsTwoBigDecimals(expectedUsd, user.wallet[to])
                    })
                }
                .verifyComplete()
        }

    }


    private fun assertEqualsTwoBigDecimals(expected: BigDecimal, received: BigDecimal?) {
        assertThat(expected, comparesEqualTo(received))
    }
}


