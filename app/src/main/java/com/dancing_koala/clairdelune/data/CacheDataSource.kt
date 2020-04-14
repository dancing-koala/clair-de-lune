package com.dancing_koala.clairdelune.data

import com.dancing_koala.clairdelune.core.CacheEntry
import com.dancing_koala.clairdelune.persistence.CacheEntryEntity
import com.dancing_koala.clairdelune.persistence.dao.CacheEntryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

interface CacheDataSource {
    suspend fun get(key: String): CacheEntry?
    suspend fun put(key: String, value: String)
    suspend fun put(cacheEntry: CacheEntry)
    suspend fun delete(cacheEntry: CacheEntry)
}

class LocalCacheDataSource(
    private val cacheEntryDao: CacheEntryDao
) : CacheDataSource {
    override suspend fun get(key: String): CacheEntry? = withContext(Dispatchers.IO) {
        cacheEntryDao.findByKey(key)?.toDataModel()
    }

    override suspend fun put(key: String, value: String) = withContext(Dispatchers.IO) {
        val timestamp = Calendar.getInstance().timeInMillis
        cacheEntryDao.insert(CacheEntryEntity(0, key, value, timestamp))
    }

    override suspend fun put(cacheEntry: CacheEntry) = withContext(Dispatchers.IO) {
        val entity = if (cacheEntry.createdAt == 0L) {
            val timestamp = Calendar.getInstance().timeInMillis
            cacheEntry.copy(createdAt = timestamp)
        } else {
            cacheEntry
        }.toEntity()

        cacheEntryDao.insert(entity)
    }

    override suspend fun delete(cacheEntry: CacheEntry) = withContext(Dispatchers.IO) {
        cacheEntryDao.delete(cacheEntry.toEntity())
    }
}