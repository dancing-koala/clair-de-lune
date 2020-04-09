package com.dancing_koala.lunedejour.network

import com.dancing_koala.lunedejour.core.Result
import com.dancing_koala.lunedejour.data.CacheDataSource
import kotlinx.coroutines.coroutineScope
import retrofit2.Call
import retrofit2.HttpException

@Suppress("MemberVisibilityCanBePrivate")
abstract class CacheBackedRemoteDataSource(protected val cacheDataSource: CacheDataSource) {

    @Suppress("BlockingMethodInNonBlockingContext")
    protected suspend fun <T : Any> executeCall(call: Call<String>, useCache: Boolean = true, processor: (rawData: String) -> T): Result<T> {
        val cacheKey = (call.request().url).toString()

        if (useCache) {
            val cacheEntry = cacheDataSource.get(cacheKey)
            if (cacheEntry != null) {
                val processedData = processor.invoke(cacheEntry.value)
                return Result.Success(processedData)
            }
        }

        return try {
            val response = call.execute()
            response.body()?.let { rawData ->
                coroutineScope {
                    cacheDataSource.put(cacheKey, rawData)
                }
                val processedData = processor.invoke(rawData)
                Result.Success(processedData)
            } ?: Result.Failure(IllegalArgumentException("Response body is empty, it should not."))
        } catch (ex: HttpException) {
            Result.Failure(ex)
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }
}