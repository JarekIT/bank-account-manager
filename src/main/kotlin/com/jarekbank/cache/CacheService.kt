package com.jarekbank.cache

interface CacheService {
    fun load()
    fun cacheName(): String
}