package com.artisan.un.utils.locale

import android.content.Context
import android.content.res.Configuration
import com.artisan.un.utils.AppLanguage
import java.util.*

object LocaleHelper {
    fun updateResources(
        context: Context?,
        language: String?
    ): Context? {
        context?.let {
            val locale = Locale(language ?: AppLanguage.ENGLISH.code)
            Locale.setDefault(locale)
            val config = Configuration(it.resources?.configuration)
            config.setLocale(locale)
            return it.createConfigurationContext(config)
        }
        return context
    }
}