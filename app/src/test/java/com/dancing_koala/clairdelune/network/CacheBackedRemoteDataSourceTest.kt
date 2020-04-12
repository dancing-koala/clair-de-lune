package com.dancing_koala.clairdelune.network

import com.dancing_koala.clairdelune.InMemoryCacheDataSource
import com.dancing_koala.clairdelune.core.Result
import com.dancing_koala.clairdelune.data.CacheDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Call
import retrofit2.Response

class CacheBackedRemoteDataSourceTest {
    companion object {
        const val DEFAULT_CALL_RETURN_VALUE = "{}"
        const val DEFAULT_CALL_URL = "https://fake.api.fr"
    }

    private val cacheDataSource = InMemoryCacheDataSource()
    private lateinit var cacheBackedRepository: TestCacheBackedRemoteDataSource

    @Before
    fun setUp() {
        cacheBackedRepository = TestCacheBackedRemoteDataSource(cacheDataSource)
    }

    @After
    fun tearDown() = cacheDataSource.deleteAll()

    @Test
    fun testCaching_requestNotCached() {
        val call: Call<String> = makeCall()
        assertEquals(0, cacheDataSource.countEntries())

        val result = cacheBackedRepository.execute(call) { it }
        assertTrue(result is Result.Success<String>)

        val success = result as Result.Success<String>
        assertEquals(DEFAULT_CALL_RETURN_VALUE, success.data)
        assertEquals(1, cacheDataSource.countEntries())
        verify(call).execute()
    }

    @Test
    fun testCaching_requestCached() {
        val url = "https://request.in.cache/"
        val returnValue = """{"code": 200}"""
        val call: Call<String> = makeCall(url, returnValue)

        // We put data in cache before calling
        runBlocking {
            cacheDataSource.put(url, returnValue)
        }

        val result = cacheBackedRepository.execute(call) { it }
        assertTrue(result is Result.Success<String>)

        val success = result as Result.Success<String>
        assertEquals(returnValue, success.data)
        assertEquals(1, cacheDataSource.countEntries())
        verify(call, times(0)).execute()
    }

    private fun makeCall(url: String = DEFAULT_CALL_URL, returnValue: String = DEFAULT_CALL_RETURN_VALUE): Call<String> {
        val request = Request.Builder()
            .url(url)
            .build()

        val response = Response.success(returnValue)

        val call = mock(StringCall::class.java)
        `when`(call.execute()).thenReturn(response)
        `when`(call.request()).thenReturn(request)

        return call
    }

    private interface StringCall : Call<String>

    private class TestCacheBackedRemoteDataSource(cacheDataSource: CacheDataSource)
        : CacheBackedRemoteDataSource(cacheDataSource) {

        fun <T : Any> execute(call: Call<String>, useCache: Boolean = true, processor: (rawData: String) -> T): Result<T> =
            runBlocking {
                executeCall(call, useCache, processor)
            }
    }
}