package com.dancing_koala.clairdelune.core

data class CacheEntry(
    val id: Int,
    val key: String,
    val value: String,
    val createdAt: Long
)

data class Picture(
    val id: String,
    val createdAt: String,
    val width: Int,
    val height: Int,
    val color: String,
    val description: String?,
    val location: Location? = null,
    val urls: Urls,
    val links: Links,
    val user: User
)

data class Location(val city: String?, val country: String?, val position: Position?)

data class Position(val latitude: Double?, val longitude: Double?)

data class Urls(
    val raw: String?,
    val full: String?,
    val regular: String?,
    val small: String?,
    val thumb: String?
)

data class Links(
    val self: String,
    val html: String,
    val download: String,
    val downloadLocation: String
)

data class User(
    val id: String,
    val username: String,
    val name: String,
    val portfolioUrl: String?,
    val links: UserLinks
)

data class UserLinks(val self: String, val html: String)

data class Lock(
    val id: Int = 0,
    val pictureId: String,
    val unlockedAt: Long?,
    val favorite: Boolean
)

data class LockWithPicture(
    val lock: Lock,
    val picture: Picture
)