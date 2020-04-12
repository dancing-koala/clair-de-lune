package com.dancing_koala.clairdelune.persistence.dao

import androidx.room.*
import com.dancing_koala.clairdelune.persistence.CacheEntryEntity

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