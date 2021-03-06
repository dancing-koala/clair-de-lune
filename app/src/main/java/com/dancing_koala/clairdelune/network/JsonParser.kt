package com.dancing_koala.clairdelune.network

import com.beust.klaxon.Klaxon

interface JsonParser {
    fun parseApiPhotoInfo(rawJson: String): ApiPicture?
    fun parseApiPhotoInfoList(rawJson: String): List<ApiPicture>
}

class KlaxonJsonParser : JsonParser {
    private val klaxon = Klaxon()

    override fun parseApiPhotoInfo(rawJson: String): ApiPicture? =
        klaxon.parse<ApiPicture>(rawJson)

    override fun parseApiPhotoInfoList(rawJson: String): List<ApiPicture> =
        klaxon.parseArray(rawJson) ?: listOf()
}