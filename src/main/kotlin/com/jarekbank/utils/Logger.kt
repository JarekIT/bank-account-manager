package com.jarekbank.utils

import org.slf4j.LoggerFactory

interface Logger {

    fun info(string: String?) {
        LoggerFactory.getLogger(this.javaClass).info(string)
    }

    fun error(string: String?) {
        LoggerFactory.getLogger(this.javaClass).error(string)
    }
}
