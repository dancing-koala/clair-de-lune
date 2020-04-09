package com.dancing_koala.lunedejour.persistence

import androidx.room.*

@Dao
interface CacheEntryDao {
    @Query("SELECT * FROM CacheEntryEntity")
    suspend fun findAll(): List<CacheEntryEntity>

    @Query("SELECT * FROM CacheEntryEntity WHERE `key` = :key LIMIT 1")
    suspend fun findByKey(key: String): CacheEntryEntity?

    @Insert
    suspend fun insert(cacheEntryEntity: CacheEntryEntity)

    @Update
    suspend fun update(cacheEntryEntity: CacheEntryEntity)

    @Delete
    suspend fun delete(cacheEntryEntity: CacheEntryEntity)
}