package com.dancing_koala.clairdelune.network

import com.beust.klaxon.Json

data class ApiPicture(
    val id: String,
    @Json(name = "created_at")
    val createdAt: String,
    val width: Int,
    val height: Int,
    val color: String,
    val description: String?,
    val location: ApiLocation? = null,
    val urls: ApiUrls,
    val links: ApiLinks,
    val user: ApiUser
)

data class ApiLocation(
    val city: String,
    val country: String,
    val position: ApiPosition
)

data class ApiPosition(
    val latitude: Double,
    val longitude: Double
)

data class ApiUrls(
    val raw: String?,
    val full: String?,
    val regular: String?,
    val small: String?,
    val thumb: String?
)

data class ApiLinks(
    val self: String,
    val html: String,
    val download: String,
    @Json(name = "download_location")
    val downloadLocation: String
)

data class ApiUser(
    val id: String,
    val username: String,
    val name: String,
    @Json(name = "portfolio_url")
    val portfolioUrl: String?,
    val links: ApiUserLinks
)

data class ApiUserLinks(
    val self: String,
    val html: String
)