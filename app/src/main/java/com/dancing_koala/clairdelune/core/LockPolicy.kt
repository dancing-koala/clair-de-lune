package com.dancing_koala.clairdelune.core

import java.util.*

object LockPolicy {
    private const val unlockHour = 4

    fun setCalendarToUnlockTime(calendar: Calendar) {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    fun isTooEarly(calendar: Calendar): Boolean =
        calendar.get(Calendar.HOUR_OF_DAY) < unlockHour
}