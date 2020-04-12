package com.dancing_koala.clairdelune.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import com.dancing_koala.clairdelune.persistence.LockEntity

@Dao
interface LockDao {
    @Query("SELECT * FROM LockEntity WHERE unlockedAt = :unlockedAt LIMIT 1")
    suspend fun findUnlockedAt(unlockedAt: Long): LockEntity?

    @Query("SELECT * FROM LockEntity WHERE unlockedAt <= :unlockedAt")
    suspend fun findAllUnlockedUpTo(unlockedAt: Long): List<LockEntity>

    @Update
    suspend fun update(lockEntity: LockEntity)

    @Delete
    suspend fun delete(lockEntity: LockEntity)
}