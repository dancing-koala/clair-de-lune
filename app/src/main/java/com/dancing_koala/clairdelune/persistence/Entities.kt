package com.dancing_koala.clairdelune.persistence

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CacheEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val key: String,
    val value: String,
    val createdAt: Long
)

@Entity
data class LockEntity(
    @PrimaryKey
    val id: Int = 0,
    val pictureId: String,
    val unlockedAt: Long?,
    val favorite: Boolean
)

@Entity
data class PictureEntity(
    @PrimaryKey val id: String,
    val createdAt: String,
    val width: Int,
    val height: Int,
    val color: String,
    val description: String?,
    @Embedded(prefix = "location_")
    val location: LocationEntity? = null,
    @Embedded(prefix = "urls_")
    val urls: UrlsEntity,
    @Embedded(prefix = "links_")
    val links: LinksEntity,
    @Embedded(prefix = "user_")
    val user: UserEntity
)

data class LocationEntity(
    val city: String,
    val country: String,
    @Embedded(prefix = "position_")
    val position: PositionEntity
)

data class PositionEntity(
    val latitude: Double,
    val longitude: Double
)

data class UrlsEntity(
    val raw: String?,
    val full: String?,
    val regular: String?,
    val small: String?,
    val thumb: String?
)

data class LinksEntity(
    val self: String,
    val html: String,
    val download: String,
    val downloadLocation: String
)

data class UserEntity(
    val id: String,
    val username: String,
    val name: String,
    val portfolioUrl: String?,
    @Embedded(prefix = "links_")
    val links: UserLinksEntity
)

data class UserLinksEntity(
    val self: String,
    val html: String
)