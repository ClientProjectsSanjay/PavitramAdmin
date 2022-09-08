package com.artisan.un

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.artisan.un.helpers.PreferencesHelper
import com.artisan.un.koinModule.appModule
import com.artisan.un.koinModule.myViewModel
import com.artisan.un.koinModule.networkModule
import com.artisan.un.utils.PREF_NAME
import com.artisan.un.utils.locale.LocaleHelper
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp : MultiDexApplication() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.updateResources(base, PreferencesHelper(base.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)).appLanguage))
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            koin.loadModules(listOf(appModule, networkModule, myViewModel))
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isSuccessful) {
                val mPref: PreferencesHelper = get()
                mPref.fcmToken = it.result ?: ""
            }
        }
    }
}