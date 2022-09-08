package com.jarekbank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JarekBankApplication

fun main(args: Array<String>) {
    runApplication<JarekBankApplication>(*args)
}
