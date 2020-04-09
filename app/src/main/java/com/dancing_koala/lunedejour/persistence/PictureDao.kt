package com.dancing_koala.lunedejour.persistence

import androidx.room.*

@Dao
interface PictureDao {
    @Query("SELECT * FROM PictureEntity")
    suspend fun findAll(): List<PictureEntity>

    @Query("SELECT * FROM PictureEntity WHERE `id` = :id LIMIT 1")
    suspend fun findById(id: String): PictureEntity?

    @Insert
    suspend fun insert(pictureEntity: PictureEntity)

    @Update
    suspend fun update(pictureEntity: PictureEntity)

    @Delete
    suspend fun delete(pictureEntity: PictureEntity)
}