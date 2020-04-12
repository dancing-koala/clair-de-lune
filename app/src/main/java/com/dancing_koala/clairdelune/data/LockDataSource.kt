package com.dancing_koala.clairdelune.data

import com.dancing_koala.clairdelune.core.LockPolicy
import com.dancing_koala.clairdelune.core.Lock
import com.dancing_koala.clairdelune.persistence.dao.LockDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

interface LockDataSource {
    suspend fun getLockForToday(): Lock
    suspend fun getLocksUpToToday(): List<Lock>
    suspend fun updateLock(lock: Lock)
}

class LocalLockDataSource(
    private val lockDao: LockDao
) : LockDataSource {

    override suspend fun getLockForToday(): Lock = withContext(Dispatchers.IO) {
        val todayTime = getTodayTime()
        val lockEntity = lockDao.findUnlockedAt(todayTime)
        lockEntity!!.toDataModel()
    }

    override suspend fun getLocksUpToToday(): List<Lock> = withContext(Dispatchers.IO) {
        val todayTime = getTodayTime()
        val lockEntities = lockDao.findAllUnlockedUpTo(todayTime)
        lockEntities.map { it.toDataModel() }
    }

    override suspend fun updateLock(lock: Lock) = withContext(Dispatchers.IO) {
        lockDao.update(lock.toEntity())
    }

    private fun getTodayTime(): Long =
        Calendar.getInstance().let {
            if (LockPolicy.isTooEarly(it)) {
                it.add(Calendar.DAY_OF_MONTH, -1)
            }

            LockPolicy.setCalendarToUnlockTime(it)
            it.time.time
        }
}