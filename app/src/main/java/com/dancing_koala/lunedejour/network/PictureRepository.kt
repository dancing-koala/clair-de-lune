package com.dancing_koala.lunedejour.network

import com.dancing_koala.lunedejour.core.Result
import com.dancing_koala.lunedejour.data.CacheDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface PictureRepository {
    suspend fun getPictureById(photoId: String): Result<ApiPicture>
}

class UnsplashPictureRemoteDataSource(
    cacheDataSource: CacheDataSource,
    private val unsplashApiV1Service: UnsplashApiV1Service
) : CacheBackedRemoteDataSource(cacheDataSource), PictureRepository {

    private val jsonParser = KlaxonJsonParser()

    override suspend fun getPictureById(photoId: String): Result<ApiPicture> = withContext(Dispatchers.IO) {
        val call = unsplashApiV1Service.getPhotoById(photoId)
        executeCall(call) { rawData -> jsonParser.parseApiPhotoInfo(rawData)!! }
    }
}