package com.artisan.un.ui.userauth

import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityResetPasswordBinding
import com.artisan.un.ui.userauth.viewModel.ResetPasswordViewModel
import com.artisan.un.utils.ResetPasswordListener
import com.artisan.un.utils.navigateTo
import com.artisan.un.utils.showMessage

class ActivityResetPassword : BaseActivity<ActivityResetPasswordBinding, ResetPasswordViewModel>(R.layout.activity_reset_password, ResetPasswordViewModel::class), ResetPasswordListener {
    override fun onCreate() {
        viewDataBinding.listener = this
        viewDataBinding.model = mViewModel
        mViewModel.mobile.value = intent.extras?.getString(MOBILE)

        observeData()
    }

    private fun observeData() {
        mViewModel.requestStatus.observe(this) {
            navigateTo(ActivityLogin::class.java)
            finishAffinity()
        }
    }

    override fun onResetClick(password: String?, confirmPassword: String?) {
        val error = when {
            password.isNullOrEmpty() -> getString(R.string.enter_new_password)
            password.length < 8 -> getString(R.string.password_length_error)
            password != confirmPassword -> getString(R.string.password_doesnt_match)
            else -> null
        }

        if (error != null) {
            viewDataBinding.root.showMessage(error)
            return
        }

        progressBar.setMessage(getString(R.string.resetting_passwrod))
        mViewModel.resetPassword(intent.extras?.getInt(OTP) ?: 0)
    }

    companion object {
        const val MOBILE = "MOBILE"
        const val OTP = "OTP"
    }
}