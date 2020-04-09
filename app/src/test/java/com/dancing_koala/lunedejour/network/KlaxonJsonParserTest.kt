package com.dancing_koala.lunedejour.network

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class KlaxonJsonParserTest {

    private val jsonParser = KlaxonJsonParser()
    private val soloObjectFileName = "one-photo-object.json"
    private val dataSetBaseName = "unsplash-moon-%d.json"

    private fun readFileContent(filename: String): String {
        val fileUrl = javaClass.getResource("/$filename") ?: throw FileNotFoundException(filename)
        val file = File(fileUrl.toURI())
       return file.inputStream().readBytes().toString(Charsets.UTF_8)
    }

    @Test
    fun parseApiPhotoInfo() {
        val content = readFileContent(soloObjectFileName)
        val parsedData = jsonParser.parseApiPhotoInfo(content)
        Assert.assertNotNull(parsedData)
    }

    @Test
    fun testParseApiPhotoInfoList() {
        for (i in 1..9) {
            val filename = String.format(Locale.ROOT, dataSetBaseName, i)
            val content = readFileContent(filename)

            val parsedData = jsonParser.parseApiPhotoInfoList(content)
            Assert.assertEquals(30, parsedData.size)
        }
    }
}