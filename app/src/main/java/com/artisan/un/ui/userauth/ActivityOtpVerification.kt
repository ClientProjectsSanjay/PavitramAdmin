package com.artisan.un.ui.userauth

import androidx.core.content.ContextCompat
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityOtpVerificationBinding
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.home.MainActivity
import com.artisan.un.ui.userauth.viewModel.OTPViewModel
import com.artisan.un.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ActivityOtpVerification : BaseActivity<ActivityOtpVerificationBinding, OTPViewModel>(R.layout.activity_otp_verification, OTPViewModel::class), OtpListener {
    override fun onCreate() {
        mViewModel.mRedirect = intent.extras?.getString(REDIRECT)
        mViewModel.mMobile = intent.extras?.getString(MOBILE)

        viewDataBinding.mobileNumber = mViewModel.mMobile
        viewDataBinding.listener = this
        startTimer()

        observeData()
    }

    private fun observeData() {
        mViewModel.userProfile.observe(this, {
            when (mViewModel.mRedirect) {
                SIGN_UP -> {
                    it?.user?.api_token?.let { token ->
                        mPref.authToken = token
                        ApplicationData.user = it

                        navigateTo(ActivityDocumentsUpload::class.java, arrayListOf(
                            Pair(USER_ROLE, it.user.role_id),
                        ))

                        finishAffinity()
                    }
                }
                FORGOT_PASSWORD -> {
                    navigateTo(ActivityResetPassword::class.java, arrayListOf(
                        Pair(ActivityResetPassword.MOBILE, mViewModel.mMobile),
                        Pair(ActivityResetPassword.OTP, viewDataBinding.pinView.text?.toString()?.toInt())
                    ))

                    finish()
                }
                UPDATE_MOBILE_NO -> {
                    progressBar.setMessage(getString(R.string.updating_mobile_no))
                    mViewModel.requestMobileNoUpdate(mViewModel.mMobile, viewDataBinding.pinView.text.toString().toInt())
                }
            }
        })

        mViewModel.otp.observe(this, {
            showOtp(it.otp)
            startTimer()
        })

        mViewModel.requestStatus.observe(this, {
            ApplicationData.user?.user?.mobile = mViewModel.mMobile
            CustomAlertDialog(
                context = this,
                isCancelable = false,
                message = getString(R.string.mobile_no_updated_successfully),
                primaryKey = getString(R.string.ok),
                primaryKeyAction = {
                    navigateTo(MainActivity::class.java, arrayListOf(Pair(MENU_TYPE, MenuType.PROFILE.name)))
                    finishAffinity()
                }
            ).show()
        })
    }

    override fun onBackClick() = onBackPressed()

    override fun onVerifyClick() {
        val pin = viewDataBinding.pinView.text.toString();

        if (pin.length == 4) {
            progressBar.setMessage(getString(R.string.verifying_otp))
            mViewModel.otpVerify(pin, mViewModel.mRedirect == SIGN_UP)
        } else {
            viewDataBinding.root.showMessage(getString(R.string.enter_a_valid_otp))
        }
    }

    private fun startTimer() {
        GlobalScope.launch(Dispatchers.Main) {
            val totalSeconds = TimeUnit.SECONDS.toSeconds(30)
            val tickSeconds = 1
            viewDataBinding.timerTxt.setOnClickListener {}
            viewDataBinding.timerTxt.setTextColor(ContextCompat.getColor(this@ActivityOtpVerification, R.color.darkGray))

            for (second in totalSeconds downTo tickSeconds) {
                val time = String.format(
                    getString(R.string.remaining_30_sec),
                    second - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(second))
                )
                viewDataBinding.timerTxt.text = time
                delay(1000)
            }

            viewDataBinding.timerTxt.text = getString(R.string.resend_otp)
            viewDataBinding.timerTxt.setTextColor(ContextCompat.getColor(this@ActivityOtpVerification, R.color.orange))
            viewDataBinding.timerTxt.setOnClickListener {
                viewDataBinding.pinView.text = null
                mViewModel.otpResend()
            }
        }
    }

    companion object {
        const val OTP = "OTP"
        const val REDIRECT = "REDIRECT"
        const val SIGN_UP = "SIGN-UP"
        const val MOBILE = "MOBILE"
        const val FORGOT_PASSWORD = "FORGOT-PASSWORD"
        const val UPDATE_MOBILE_NO = "UPDATE-MOBILE-NO"
    }
}