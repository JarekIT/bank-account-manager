package com.jarekbank.cache

import com.jarekbank.utils.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CacheReloadScheduler(private val cachedServices: List<CacheService>) : Scheduler {

    private companion object : Logger

    @Scheduled(cron = "0 */15 * * * ?")
    override fun execute() {
        cachedServices.forEach { cache -> cache.load() }
    }
}