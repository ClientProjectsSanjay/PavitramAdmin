package com.artisan.un.ui.userauth

import android.util.Patterns
import com.artisan.un.R
import com.artisan.un.apiModel.LoginModel
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityLoginBinding
import com.artisan.un.ui.home.MainActivity
import com.artisan.un.ui.userauth.viewModel.LoginViewModel
import com.artisan.un.utils.*
import com.artisan.un.utils.apis.UserResponse

class ActivityLogin : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login, LoginViewModel::class), LoginListener {
    override fun onCreate() {
        viewDataBinding.listener = this
        viewDataBinding.loginModel = LoginModel()

        observeData()
    }

    private fun observeData() {
        mViewModel.userResponse.observe(this) {
            if (it?.user == null && it?.is_otp_verified == 0) {
                showOtp(it.otp)

                navigateTo(
                    ActivityOtpVerification::class.java, arrayListOf(
                        Pair(ActivityOtpVerification.MOBILE, it.mobile),
                        Pair(ActivityOtpVerification.REDIRECT, ActivityOtpVerification.SIGN_UP),
                    )
                )
            } else if (it?.user?.is_document_added == 0) {
                saveUserData(it)

                navigateTo(
                    ActivityDocumentsUpload::class.java, arrayListOf(
                        Pair(USER_ROLE, it.user.role_id),
                    )
                )

                finishAffinity()
            } else {
                saveUserData(it)
                navigateTo(MainActivity::class.java)
                finishAffinity()
            }
        }
    }

    private fun saveUserData(user: UserResponse) {
        ApplicationData.user = user
        ApplicationData.user?.user?.api_token?.let { token -> mPref.authToken = token }
    }

    override fun registerNowClick() {
        navigateTo(ActivityRegister::class.java)
    }

    override fun forgotPasswordClick() {
        navigateTo(ActivityEnterMobile::class.java, arrayListOf(
            Pair(ENTER_MOBILE_ACTION, EnterMobileAction.FORGOT_PASSWORD.action)
        ))
    }

    override fun login(loginModel: LoginModel) {
        val error = validateData(loginModel)
        if(error == null) {
            progressBar.setMessage(getString(R.string.signing_in))
            mViewModel.login(loginModel)
        }
        else viewDataBinding.root.showMessage(error)
    }

    private fun validateData(loginModel: LoginModel): String? = run {
        when {
            loginModel.phoneOrEmail.isNullOrEmpty() -> getString(R.string.enter_mobile_or_email)
            !Patterns.PHONE.matcher(loginModel.phoneOrEmail ?: "").matches() && !Patterns.EMAIL_ADDRESS.matcher(loginModel.phoneOrEmail ?: "").matches() -> getString(R.string.invalid_mobile_or_email)
            loginModel.password.isNullOrEmpty() -> getString(R.string.enter_password)
            loginModel.password?.length ?: 0 < 5 -> getString(R.string.password_length_error)
            else -> null
        }
    }
}