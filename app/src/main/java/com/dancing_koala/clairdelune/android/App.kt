package com.dancing_koala.clairdelune.android

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.dancing_koala.clairdelune.data.*
import com.dancing_koala.clairdelune.network.UnsplashApiV1Service
import com.dancing_koala.clairdelune.network.UnsplashPictureRepository
import com.dancing_koala.clairdelune.persistence.AppDatabase
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class App : Application() {

    val kodein by Kodein.lazy {
        val appDB = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "ClairDeLune.db")
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val unsplashApiV1Service = retrofit.create(UnsplashApiV1Service::class.java)

        bind<AppDatabase>() with provider { appDB }
        bind<CacheDataSource>() with provider { LocalCacheDataSource(appDB.cacheEntryDao()) }
        bind<LocalLockDataSource>() with provider { LocalLockDataSource(appDB.lockDao()) }
        bind<PictureDataSource>(tag = "local") with provider { LocalPictureDataSource(appDB.pictureDao()) }
        bind<PictureDataSource>(tag = "unsplash") with provider {
            RemotePictureDataSource(UnsplashPictureRepository(instance(), unsplashApiV1Service))
        }
        bind<LockWithPictureDataSource>() with provider {
            LockWithPictureDataSourceImpl(
                instance(),
                instance(tag = "local"),
                instance(tag = "unsplash")
            )
        }
        bind<PreferencesManager>() with singleton {
            PreferencesManager(getSharedPreferences("app_default", Context.MODE_PRIVATE))
        }
    }

    override fun onCreate() {
        super.onCreate()

        val preferencesManager = kodein.direct.instance<PreferencesManager>()

        if (preferencesManager.isFirstLaunch) {
            val appDB = kodein.direct.instance<AppDatabase>()
            appDB.populateDatabase(this)
            preferencesManager.isFirstLaunch = false
        }
    }
}