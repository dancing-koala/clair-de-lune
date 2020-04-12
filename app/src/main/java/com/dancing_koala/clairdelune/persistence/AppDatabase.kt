package com.dancing_koala.clairdelune.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.dancing_koala.clairdelune.R
import com.dancing_koala.clairdelune.data.LockPolicy
import com.dancing_koala.clairdelune.persistence.dao.CacheEntryDao
import com.dancing_koala.clairdelune.persistence.dao.LockDao
import com.dancing_koala.clairdelune.persistence.dao.PictureDao
import java.util.*

@Database(
    entities = [CacheEntryEntity::class, PictureEntity::class, LockEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cacheEntryDao(): CacheEntryDao
    abstract fun pictureDao(): PictureDao
    abstract fun lockDao(): LockDao

    fun populateDatabase(context: Context) {
        val calendar = Calendar.getInstance()
        LockPolicy.setCalendarToUnlockTime(calendar)

        val curatedIds = context.resources.getStringArray(R.array.picture_curated_ids)

        val queryStringBuilder = StringBuilder("INSERT INTO LockEntity (pictureId, unlockedAt, favorite) VALUES ")

        for (id in curatedIds) {
            queryStringBuilder
                .append("(\"$id\", ${calendar.time.time}, 0)")
                .append(",")

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        queryStringBuilder.deleteCharAt(queryStringBuilder.lastIndex)
            .append(";")

        val query = queryStringBuilder.toString()

        openHelper.writableDatabase?.let { db ->
            db.beginTransaction()
            db.execSQL(query)
            db.setTransactionSuccessful()
            db.endTransaction()
        }
    }
}
