package com.dancing_koala.clairdelune.network

import com.dancing_koala.clairdelune.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface UnsplashApiV1Service {

    @Headers(
        "Accept-Version: v1",
        "Authorization: Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}"
    )
    @GET("/photos/{photoId}")
    fun getPhotoById(@Path("photoId") photoId: String): Call<String>
}