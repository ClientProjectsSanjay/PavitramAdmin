package com.artisan.un.ui.userauth

import android.util.Patterns
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityEnterMobileBinding
import com.artisan.un.ui.userauth.viewModel.ForgotPasswordViewModel
import com.artisan.un.utils.*

class ActivityEnterMobile : BaseActivity<ActivityEnterMobileBinding, ForgotPasswordViewModel>(R.layout.activity_enter_mobile, ForgotPasswordViewModel::class), ForgotPasswordListener {
    private var enterMobileAction: String? = null

    override fun onCreate() {
        enterMobileAction = intent.getStringExtra(ENTER_MOBILE_ACTION)

        when(enterMobileAction) {
            EnterMobileAction.FORGOT_PASSWORD.action -> {
                viewDataBinding.title = getString(R.string.forgot_password)
                viewDataBinding.message = getString(R.string.enter_registered_mobile_number)
                viewDataBinding.actionTitle = getString(R.string.reset_now)
            }
            EnterMobileAction.UPDATE_MOBILE.action -> {
                viewDataBinding.title = getString(R.string.change_mobile_no)
                viewDataBinding.message = getString(R.string.enter_new_mobile_no)
                viewDataBinding.actionTitle = getString(R.string.update)
            }
        }

        viewDataBinding.listener = this
        viewDataBinding.model = mViewModel

        observeData()
    }

    override fun onResetNowClick(mobileNo: String?) {
        if(mobileNo.isNullOrEmpty() || !Patterns.PHONE.matcher(mobileNo).matches() || mobileNo.length < 10) {
            viewDataBinding.root.showMessage(getString(R.string.enter_valid_mobile_number))
            return
        }

        progressBar.setMessage(getString(R.string.sending_otp))

        when(enterMobileAction) {
            EnterMobileAction.FORGOT_PASSWORD.action -> mViewModel.forgotPassword()
            EnterMobileAction.UPDATE_MOBILE.action -> mViewModel.requestUpdateMobileOtp()
        }
    }

    private fun observeData() {
        mViewModel.otp.observe(this) {
            showOtp(it.otp)

            when (enterMobileAction) {
                EnterMobileAction.FORGOT_PASSWORD.action -> ActivityOtpVerification.FORGOT_PASSWORD
                EnterMobileAction.UPDATE_MOBILE.action -> ActivityOtpVerification.UPDATE_MOBILE_NO
                else -> null
            }?.let { actionType ->
                navigateTo(
                    ActivityOtpVerification::class.java, arrayListOf(
                        Pair(ActivityOtpVerification.MOBILE, mViewModel.mobile.value),
                        Pair(ActivityOtpVerification.REDIRECT, actionType),
                    )
                )
            }
        }
    }

    override fun onBackClick() = onBackPressed()
}