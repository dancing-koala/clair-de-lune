package com.dancing_koala.clairdelune.data

import com.dancing_koala.clairdelune.core.LockWithPicture
import com.dancing_koala.clairdelune.core.Picture
import com.dancing_koala.clairdelune.core.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface LockWithPictureDataSource {
    suspend fun getLockWithPictureForToday(): Result<LockWithPicture>
    suspend fun getLockWithPictureListUpToToday(): Result<List<LockWithPicture>>
}

class LockWithPictureDataSourceImpl(
    private val lockDataSource: LocalLockDataSource,
    private val localPictureDataSource: PictureDataSource,
    private val remotePictureDataSource: PictureDataSource
) : LockWithPictureDataSource {

    override suspend fun getLockWithPictureForToday(): Result<LockWithPicture> = withContext(Dispatchers.IO) {
        val lock = lockDataSource.getLockForToday()
        val pictureResult = getPictureByIdFromLocalOrRemote(lock.pictureId)

        if (pictureResult is Result.Success<Picture>) {
            Result.Success(LockWithPicture(lock, pictureResult.data))
        } else {
            pictureResult as Result.Failure
        }
    }

    override suspend fun getLockWithPictureListUpToToday(): Result<List<LockWithPicture>> = withContext(Dispatchers.IO) {
        val locks = lockDataSource.getLocksUpToToday()
        val lockWithPictureList = mutableListOf<LockWithPicture>()

        for (lock in locks) {
            val pictureResult = getPictureByIdFromLocalOrRemote(lock.pictureId)
            if (pictureResult is Result.Success<Picture>) {
                lockWithPictureList.add(LockWithPicture(lock, pictureResult.data))
            } else {
                return@withContext pictureResult as Result.Failure
            }
        }

        Result.Success(lockWithPictureList)
    }

    private suspend fun getPictureByIdFromLocalOrRemote(id: String): Result<Picture> {
        val localPictureResult = localPictureDataSource.get(id)

        return if (localPictureResult is Result.Success<Picture>) {
            localPictureResult
        } else {
            remotePictureDataSource.get(id).also {
                if (it is Result.Success<Picture>) {
                    localPictureDataSource.insert(it.data)
                }
            }
        }
    }
}