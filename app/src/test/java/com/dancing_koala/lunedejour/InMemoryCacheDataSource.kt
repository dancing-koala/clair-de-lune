package com.dancing_koala.lunedejour

import com.dancing_koala.lunedejour.data.CacheDataSource
import com.dancing_koala.lunedejour.data.CacheEntry
import java.util.*
import kotlin.collections.HashMap

class InMemoryCacheDataSource : CacheDataSource {

    private var nextId: Int = 1000
        get() = field++

    private val cacheEntries = HashMap<String, CacheEntry>()

    override suspend fun get(key: String): CacheEntry? = cacheEntries[key]

    override suspend fun put(key: String, value: String) {
        cacheEntries[key] = CacheEntry(nextId, key, value, Date().time)
    }

    override suspend fun put(cacheEntry: CacheEntry) {
        cacheEntries[cacheEntry.key] = cacheEntry
    }

    override suspend fun delete(cacheEntry: CacheEntry) {
        cacheEntries.remove(cacheEntry.key)
    }

    fun deleteAll() = cacheEntries.clear()

    fun countEntries() = cacheEntries.size

    fun printEntries() = println(cacheEntries)
}