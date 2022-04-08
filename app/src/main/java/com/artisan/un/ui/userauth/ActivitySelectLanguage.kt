package com.artisan.un.ui.userauth

import android.content.Intent
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.baseClasses.CommonViewModel
import com.artisan.un.databinding.ActivitySelectLanguageBinding
import com.artisan.un.utils.AppLanguage
import com.artisan.un.utils.LanguageSelectionListener

class ActivitySelectLanguage : BaseActivity<ActivitySelectLanguageBinding,CommonViewModel>(R.layout.activity_select_language,CommonViewModel::class), LanguageSelectionListener {

    override fun onCreate() {
        viewDataBinding.listener = this
        viewDataBinding.selectedLanguage = when (mPref.appLanguage) {
            AppLanguage.ENGLISH.code -> AppLanguage.ENGLISH
            AppLanguage.KANNADA.code -> AppLanguage.KANNADA
            else -> AppLanguage.ENGLISH
        }
    }

    override fun onProceedClick() {
        startActivity(Intent(this, ActivityLogin::class.java))
    }

    override fun selectLanguage(language: AppLanguage) {
        mPref.appLanguage = language.code
        viewDataBinding.selectedLanguage = language

        startActivity(intent)
        finish()
        overridePendingTransition(0,0)
    }
}