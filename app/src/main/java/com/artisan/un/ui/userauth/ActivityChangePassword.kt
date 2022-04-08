package com.artisan.un.ui.userauth

import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityChangePasswordBinding
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.home.MainActivity
import com.artisan.un.ui.home.viewModel.ChangePasswordViewModel
import com.artisan.un.utils.*

class ActivityChangePassword : BaseActivity<ActivityChangePasswordBinding, ChangePasswordViewModel>(R.layout.activity_change_password, ChangePasswordViewModel::class), AppBarListener {

    override fun onCreate() {
        viewDataBinding.run {
            appBarListener = this@ActivityChangePassword

            changePassword.setOnClickListener {
                val oldPassword = inputOldPassword.text.toString()
                val newPassword = inputNewPassword.text.toString()
                val confirmPassword = inputConfirmPassword.text.toString()

                validateData(oldPassword, newPassword, confirmPassword)?.let {
                    viewDataBinding.root.showMessage(it)
                } ?: run {
                    mViewModel.requestUpdatePassword(oldPassword, newPassword, confirmPassword)
                }
            }
        }

        observeData()
    }

    override fun onBackClick() {
        onBackPressed()
    }

    private fun observeData() {
        mViewModel.changePassword.observe(this, {
            CustomAlertDialog(
                context = this,
                isCancelable = false,
                message = it,
                primaryKey = getString(R.string.ok),
                primaryKeyAction = {
                    navigateTo(MainActivity::class.java, arrayListOf(Pair(MENU_TYPE, MenuType.PROFILE.name)))
                    finishAffinity()
                }
            ).show()
        })
    }

    private fun validateData(oldPassword: String, newPassword: String, confirmPassword: String) : String? = run {
        if(oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) getString(R.string.fill_all_the_fields)
        else if(oldPassword.length < 8) getString(R.string.enter_valid_current_password)
        else if(newPassword.length < 8) getString(R.string.new_password_length_error)
        else if(newPassword != confirmPassword) getString(R.string.confirm_password_doesnt_match)
        else null
    }
}