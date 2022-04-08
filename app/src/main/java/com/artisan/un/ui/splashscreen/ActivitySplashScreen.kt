package com.artisan.un.ui.splashscreen

import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivitySplashScreenBinding
import com.artisan.un.ui.home.MainActivity
import com.artisan.un.ui.userauth.ActivityDocumentsUpload
import com.artisan.un.ui.userauth.ActivityLogin
import com.artisan.un.ui.userauth.ActivitySelectLanguage
import com.artisan.un.ui.userauth.viewModel.LoginViewModel
import com.artisan.un.utils.ApplicationData
import com.artisan.un.utils.USER_ROLE
import com.artisan.un.utils.navigateTo

class ActivitySplashScreen : BaseActivity<ActivitySplashScreenBinding, LoginViewModel>(R.layout.activity_splash_screen, LoginViewModel::class) {
    override fun onCreate() {
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val apiToken = mPref.authToken

        if(apiToken.isEmpty()) {
            Handler(Looper.getMainLooper()).postDelayed({
                navigateTo(ActivitySelectLanguage::class.java)
                finish()
            }, 1000)
        } else {
            observeData()
            mViewModel.requestUserProfile()
        }
    }

    private fun observeData() {
        mViewModel.userResponse.observe(this) {
            it?.let {
                ApplicationData.user = it

                if (it.user?.is_document_added == 0) {
                    mPref.clear()
                    navigateTo(ActivityLogin::class.java)
                } else {
                    navigateTo(MainActivity::class.java)
                }

                finishAffinity()
            }
        }

        mViewModel.errorMessage.observe(this, {
            mPref.clear()
            navigateTo(ActivitySelectLanguage::class.java)
            finish()
        })
    }
}