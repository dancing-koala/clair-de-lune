package com.dancing_koala.lunedejour.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CacheEntryEntity::class, PictureEntity::class, UnlockedPictureEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cacheEntryDao(): CacheEntryDao
    abstract fun pictureDao(): PictureDao
}
