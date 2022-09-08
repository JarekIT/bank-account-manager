package com.jarekbank.domain.user.utils

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * @author https://github.com/mnahajowski/My_pesel_validator/blob/master/app/src/main/java/com/example/my_pesel_validator/PeselValidator.kt
 */
@Component
internal class PeselValidator {


    fun validate(pesel: String) =
        peselLengthCorrect(pesel)
            .and(checkChecksum(pesel))

    fun validateAge(pesel: String) = isLegalAge(pesel)

    private fun peselLengthCorrect(pesel: String): Boolean {
        return pesel.length == 11 && pesel.toLongOrNull() != null
    }

    private fun getBirthdayDate(pesel: String): LocalDate? {
        val year = pesel.subSequence(0, 2).toString()
        val day = pesel.subSequence(4, 6).toString()
        val month = pesel.subSequence(2, 4).toString().toInt() % 20
        val century = getCentury(pesel.subSequence(2, 4).toString().toInt())

        val date = "${century}${year}-${month.toString().padStart(2, '0')}-${day}"

        return try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: DateTimeParseException) {
            return null
        }
    }

    private val legalAge = 18L

    private fun isLegalAge(pesel: String) =
        getBirthdayDate(pesel)?.isBefore(LocalDate.now().minusYears(legalAge)) ?: false

    private fun getCentury(month: Int): String {
        return when {
            month < 20 -> "19"
            month < 40 -> "20"
            month < 60 -> "21"
            month < 80 -> "22"
            else -> "18"
        }
    }

    private fun checkChecksum(pesel: String): Boolean {
        var correctChecksum = 0
        val positionWeights = arrayOf(1, 3, 7, 9, 1, 3, 7, 9, 1, 3)

        for (i in 0 until pesel.length - 1)
            correctChecksum += (pesel[i].toString().toInt() * positionWeights[i]) % 10
        correctChecksum = 10 - (correctChecksum % 10)

        return correctChecksum == pesel.last().toString().toInt()
    }
}