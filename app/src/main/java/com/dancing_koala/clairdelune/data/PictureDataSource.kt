package com.dancing_koala.clairdelune.data

import com.dancing_koala.clairdelune.core.EntityNotFoundException
import com.dancing_koala.clairdelune.core.Picture
import com.dancing_koala.clairdelune.core.Result
import com.dancing_koala.clairdelune.network.ApiPicture
import com.dancing_koala.clairdelune.network.PictureRepository
import com.dancing_koala.clairdelune.persistence.dao.PictureDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface PictureDataSource {
    suspend fun get(id: String): Result<Picture>
    suspend fun insert(picture: Picture)
    suspend fun update(picture: Picture)
    suspend fun delete(picture: Picture)
}

class LocalPictureDataSource(
    private val pictureDao: PictureDao
) : PictureDataSource {

    override suspend fun get(id: String): Result<Picture> = withContext(Dispatchers.IO) {
        val fetchedItem = pictureDao.findById(id)?.toDataModel()

        if (fetchedItem != null) {
            Result.Success(fetchedItem)
        } else {
            Result.Failure(EntityNotFoundException(id))
        }
    }

    override suspend fun insert(picture: Picture) =
        pictureDao.insert(picture.toEntity())

    override suspend fun update(picture: Picture) =
        pictureDao.update(picture.toEntity())

    override suspend fun delete(picture: Picture) =
        pictureDao.delete(picture.toEntity())
}

class RemotePictureDataSource(private val pictureRepository: PictureRepository) : PictureDataSource {

    override suspend fun get(id: String): Result<Picture> = withContext(Dispatchers.IO) {
        val result = pictureRepository.getPictureById(id)

        if (result is Result.Success<ApiPicture>) {
            Result.Success(result.data.toDataModel())
        } else {
            result as Result.Failure
        }
    }

    override suspend fun insert(picture: Picture) = throw NotImplementedError()

    override suspend fun update(picture: Picture) = throw NotImplementedError()

    override suspend fun delete(picture: Picture) = throw NotImplementedError()

}