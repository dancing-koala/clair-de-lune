package com.dancing_koala.clairdelune.data

import com.dancing_koala.clairdelune.core.*
import com.dancing_koala.clairdelune.network.*
import com.dancing_koala.clairdelune.persistence.*

fun CacheEntry.toEntity() = CacheEntryEntity(
    id = id,
    key = key,
    value = value,
    createdAt = createdAt
)

fun CacheEntryEntity.toDataModel() = CacheEntry(
    id = id,
    key = key,
    value = value,
    createdAt = createdAt
)

fun Lock.toEntity() = LockEntity(
    id = id,
    pictureId = pictureId,
    unlockedAt = unlockedAt,
    favorite = favorite
)

fun LockEntity.toDataModel() = Lock(
    id = id,
    pictureId = pictureId,
    unlockedAt = unlockedAt,
    favorite = favorite
)

fun Picture.toEntity() = PictureEntity(
    id = id,
    createdAt = createdAt,
    width = width,
    height = height,
    color = color,
    description = description,
    location = location?.toEntity(),
    urls = urls.toEntity(),
    links = links.toEntity(),
    user = user.toEntity()
)

fun Location.toEntity() = LocationEntity(
    city = city,
    country = country,
    position = position?.toEntity()
)

fun Position.toEntity() = PositionEntity(
    latitude = latitude,
    longitude = longitude
)

fun Urls.toEntity() = UrlsEntity(
    raw = raw,
    full = full,
    regular = regular,
    small = small,
    thumb = thumb
)

fun Links.toEntity() = LinksEntity(
    self = self,
    html = html,
    download = download,
    downloadLocation = downloadLocation
)

fun User.toEntity() = UserEntity(
    id = id,
    username = username,
    name = name,
    portfolioUrl = portfolioUrl,
    links = links.toEntity()
)

fun UserLinks.toEntity() = UserLinksEntity(
    self = self,
    html = html
)

fun PictureEntity.toDataModel() = Picture(
    id = id,
    createdAt = createdAt,
    width = width,
    height = height,
    color = color,
    description = description,
    location = location?.toDataModel(),
    urls = urls.toDataModel(),
    links = links.toDataModel(),
    user = user.toDataModel()
)

fun LocationEntity.toDataModel() = Location(
    city = city,
    country = country,
    position = position?.toDataModel()
)

fun PositionEntity.toDataModel() = Position(
    latitude = latitude,
    longitude = longitude
)

fun UrlsEntity.toDataModel() = Urls(
    raw = raw,
    full = full,
    regular = regular,
    small = small,
    thumb = thumb
)

fun LinksEntity.toDataModel() = Links(
    self = self,
    html = html,
    download = download,
    downloadLocation = downloadLocation
)

fun UserEntity.toDataModel() = User(
    id = id,
    username = username,
    name = name,
    portfolioUrl = portfolioUrl,
    links = links.toDataModel()
)

fun UserLinksEntity.toDataModel() = UserLinks(
    self = self,
    html = html
)

fun ApiPicture.toDataModel() = Picture(
    id = id,
    createdAt = createdAt,
    width = width,
    height = height,
    color = color,
    description = description,
    location = location?.toDataModel(),
    urls = urls.toDataModel(),
    links = links.toDataModel(),
    user = user.toDataModel()
)

fun ApiLocation.toDataModel() = Location(
    city = city,
    country = country,
    position = position?.toDataModel()
)

fun ApiPosition.toDataModel() = Position(
    latitude = latitude,
    longitude = longitude
)

fun ApiUrls.toDataModel() = Urls(
    raw = raw,
    full = full,
    regular = regular,
    small = small,
    thumb = thumb
)

fun ApiLinks.toDataModel() = Links(
    self = self,
    html = html,
    download = download,
    downloadLocation = downloadLocation
)

fun ApiUser.toDataModel() = User(
    id = id,
    username = username,
    name = name,
    portfolioUrl = portfolioUrl,
    links = links.toDataModel()
)

fun ApiUserLinks.toDataModel() = UserLinks(
    self = self,
    html = html
)