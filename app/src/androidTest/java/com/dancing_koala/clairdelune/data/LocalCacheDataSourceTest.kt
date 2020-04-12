package com.dancing_koala.clairdelune.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dancing_koala.clairdelune.core.CacheEntry
import com.dancing_koala.clairdelune.persistence.AppDatabase
import com.dancing_koala.clairdelune.persistence.dao.CacheEntryDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class LocalCacheDataSourceTest {
    private companion object {
        const val KEY = "coucou"
        const val VALUE = "hibou"
    }

    private lateinit var cacheEntryDao: CacheEntryDao
    private lateinit var db: AppDatabase
    private lateinit var dataSource: LocalCacheDataSource

    private val defaultEntry = CacheEntry(1, KEY, VALUE, Date().time)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        cacheEntryDao = db.cacheEntryDao()
        dataSource = LocalCacheDataSource(cacheEntryDao)

        runBlocking {
            cacheEntryDao.insert(defaultEntry.toEntity())
        }
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    fun get_EntryNotFound() = runBlocking {
        val currentEntries = cacheEntryDao.findAll()
        assertTrue(currentEntries.isNotEmpty())

        val fetchedEntry = dataSource.get("popo")
        assertNull(fetchedEntry)
    }

    @Test
    fun get_EntryFound() = runBlocking {
        val currentEntries = cacheEntryDao.findAll()
        assertTrue(currentEntries.isNotEmpty())

        val fetchedEntry = dataSource.get(defaultEntry.key)
        assertNotNull(fetchedEntry)
        assertEquals(defaultEntry, fetchedEntry)
    }

    @Test
    fun put() = runBlocking {
        val newEntry = CacheEntry(111, "popo", "lolo", Date().time)
        val entries = cacheEntryDao.findAll()
        assertEquals(1, entries.size)

        dataSource.put(newEntry)

        val entriesAfterPut = cacheEntryDao.findAll()
        assertEquals(2, entriesAfterPut.size)

        val fetchedEntry = dataSource.get(newEntry.key)
        assertNotNull(fetchedEntry)
        assertEquals(newEntry, fetchedEntry)
    }


    @Test
    fun delete() = runBlocking {
        val entries = cacheEntryDao.findAll()
        assertEquals(1, entries.size)

        dataSource.delete(defaultEntry)

        val entriesAfterDelete = cacheEntryDao.findAll()
        assertTrue(entriesAfterDelete.isEmpty())
    }
}