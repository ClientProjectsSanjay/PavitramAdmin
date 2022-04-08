package com.artisan.un.ui.userauth

import android.util.Patterns
import android.widget.CheckBox
import com.artisan.un.R
import com.artisan.un.apiModel.AuthModel
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityRegisterBinding
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.userauth.viewModel.RegisterViewModel
import com.artisan.un.utils.RegisterListener
import com.artisan.un.utils.navigateTo
import com.artisan.un.utils.showMessage
import com.artisan.un.utils.showOtp

class ActivityRegister : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register, RegisterViewModel::class), RegisterListener {
    private var mobileNumber: String? = null

    override fun onCreate() {
        viewDataBinding.model = mViewModel
        viewDataBinding.listener = this
        viewDataBinding.authModel = AuthModel()
        viewDataBinding.userTypes = resources.getStringArray(R.array.user_types).toCollection(ArrayList())

        observeData()
    }

    private fun observeData() {
        mViewModel.otp.observe(this, {
            showOtp(it.otp)
            navigateTo(ActivityOtpVerification::class.java, arrayListOf(
                Pair(ActivityOtpVerification.MOBILE, mobileNumber),
                Pair(ActivityOtpVerification.REDIRECT, ActivityOtpVerification.SIGN_UP),
            ))
        })

        mViewModel.userAlreadyRegistered.observe(this, {
            CustomAlertDialog(
                context = this,
                title = "User Already Exist",
                message = it.message,
                isCancelable = false,
                primaryKey = getString(R.string.login_now),
                secondaryKey = getString(R.string.cancel),
                primaryKeyAction = { onLoginClick() },
            ).show()
        })
    }

    override fun onSignUpClick(promotionalEmails: CheckBox, termsAcceptance: CheckBox, authModel: AuthModel) {
        authModel.isPromotionalEmailEnabled = promotionalEmails.isChecked

        validateData(authModel)?.let {
            viewDataBinding.root.showMessage(it)
            return
        }

        if (!termsAcceptance.isChecked) {
            viewDataBinding.root.showMessage(getString(R.string.accept_terms_and_conditions))
            return
        }

        mobileNumber = authModel.mobileNo

        progressBar.setMessage(getString(R.string.creating_user))
        mViewModel.registerUser(authModel)
    }

    private fun validateData(authModel: AuthModel): String? = run {
        when {
            authModel.name.isNullOrEmpty() -> getString(R.string.enter_name)
            (!authModel.emailId.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(authModel.emailId ?: "").matches()) -> getString(R.string.invalid_email_id)
            authModel.mobileNo.isNullOrEmpty() -> getString(R.string.enter_mobile_no)
            authModel.mobileNo?.length ?: 0 < 10 -> getString(R.string.invalid_mobile_no)
            !Patterns.PHONE.matcher(authModel.mobileNo ?: "").matches() -> getString(R.string.invalid_mobile_no)
            authModel.password.isNullOrEmpty() -> getString(R.string.enter_password)
            authModel.password?.length ?: 0 < 8 -> getString(R.string.password_length_error)
            !(authModel.confirmPassword?.equals(authModel.password) ?: false) -> getString(R.string.password_doesnt_match)
            !resources.getStringArray(R.array.user_types).contains(authModel.userType) -> getString(
                R.string.select_user_type
            )
            else -> null
        }
    }

    override fun onLoginClick() = onBackPressed()
}