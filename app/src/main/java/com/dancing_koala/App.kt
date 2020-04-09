package com.dancing_koala

import android.app.Application
import androidx.room.Room
import com.dancing_koala.lunedejour.data.CacheDataSource
import com.dancing_koala.lunedejour.data.LocalCacheDataSource
import com.dancing_koala.lunedejour.network.UnsplashApiV1Service
import com.dancing_koala.lunedejour.persistence.AppDatabase
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import retrofit2.Retrofit

class App : Application() {

    val kodein by Kodein.lazy {
        val appDB = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "ClairDeLune.db")
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .build()

        val unsplashApiV1Service = retrofit.create(UnsplashApiV1Service::class.java)

        bind<CacheDataSource>() with provider { LocalCacheDataSource(appDB.cacheEntryDao()) }
    }

    override fun onCreate() {
        super.onCreate()
    }
}