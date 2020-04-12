package com.dancing_koala.clairdelune.android

import android.content.SharedPreferences

class PreferencesManager(private val preferences: SharedPreferences) {

    companion object {
        const val KEY_IS_FIRST_LAUNCH = "com.dancing_koala.clairdelune.IS_FIRST_LAUNCH"
    }

    var isFirstLaunch: Boolean
        get() = preferences.getBoolean(KEY_IS_FIRST_LAUNCH, true)
        set(value) {
            preferences.edit()
                .putBoolean(KEY_IS_FIRST_LAUNCH, value)
                .apply()
        }
}