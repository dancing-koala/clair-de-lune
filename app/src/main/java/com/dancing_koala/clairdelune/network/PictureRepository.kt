package com.dancing_koala.clairdelune.network

import com.dancing_koala.clairdelune.core.Result
import com.dancing_koala.clairdelune.data.CacheDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface PictureRepository {
    suspend fun getPictureById(photoId: String): Result<ApiPicture>
}

class UnsplashPictureRepository(
    cacheDataSource: CacheDataSource,
    private val unsplashApiV1Service: UnsplashApiV1Service
) : CacheBackedRemoteDataSource(cacheDataSource), PictureRepository {

    private val jsonParser = KlaxonJsonParser()

    override suspend fun getPictureById(photoId: String): Result<ApiPicture> = withContext(Dispatchers.IO) {
        val call = unsplashApiV1Service.getPhotoById(photoId)
        executeCall(call) { rawData -> jsonParser.parseApiPhotoInfo(rawData)!! }
    }
}