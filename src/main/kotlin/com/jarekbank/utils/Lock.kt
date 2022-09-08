package com.jarekbank.utils

import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

@Component
class Lock {

    private companion object : Logger

    private val lock = ReentrantLock(true)

    internal fun lock() {
        lock.lock()
    }
    internal fun unlock() {
        lock.unlock()
    }
    internal fun lockTime(seconds: Long) {
        lock.tryLock(seconds, TimeUnit.SECONDS)
    }
}