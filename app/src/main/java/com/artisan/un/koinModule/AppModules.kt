package com.artisan.un.koinModule

import android.content.Context
import com.artisan.un.utils.PREF_NAME
import com.artisan.un.helpers.PreferencesHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single {
        androidContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    single {
        PreferencesHelper(get())
    }
}