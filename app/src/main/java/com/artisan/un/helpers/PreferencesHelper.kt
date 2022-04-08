package com.artisan.un.helpers

import android.content.SharedPreferences
import com.artisan.un.utils.AppLanguage

class PreferencesHelper internal constructor(private val mPrefs: SharedPreferences) {
    var authToken: String
        get() = mPrefs.getString("USER-AUTH-TOKEN", "") ?: ""
        set(value) = mPrefs.edit().putString("USER-AUTH-TOKEN", value).apply()

    var fcmToken: String
        get() = mPrefs.getString("FCM-TOKEN", " ").toString()
        set(value) = mPrefs.edit().putString("FCM-TOKEN", value).apply()

    var appLanguage: String
        get() = mPrefs.getString("appLanguage", AppLanguage.ENGLISH.code).toString()
        set(value) = mPrefs.edit().putString("appLanguage", value).apply()

    fun clear() {
        val fc = fcmToken
        mPrefs.edit().clear().apply()
        fcmToken = fc
    }
}