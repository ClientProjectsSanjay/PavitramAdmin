package com.artisan.un.ui.home

import android.util.Patterns
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityEditProfileBinding
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.home.viewModel.HomeViewModel
import com.artisan.un.ui.userauth.ActivityChangePassword
import com.artisan.un.ui.userauth.ActivityEnterMobile
import com.artisan.un.utils.*
import com.artisan.un.utils.apis.UserInfo

class EditProfileActivity : BaseActivity<ActivityEditProfileBinding, HomeViewModel>(R.layout.activity_edit_profile, HomeViewModel::class), EditProfileListener, AppBarListener {
    private var newName: String? = null
    private var newTitle: String? = null
    private var newEmail: String? = null

    override fun onCreate() = observeData()

    override fun onBackClick() = onBackPressed()

    override fun onStart() {
        super.onStart()
        val user = ApplicationData.user?.user?.copy()

        viewDataBinding.user = user
        viewDataBinding.listener = this
        viewDataBinding.appBarListener = this
        viewDataBinding.address = formatAddress(ApplicationData.user?.address?.registered)
    }

    private fun observeData() {
        mViewModel.requestStatus.observe(this) {
            ApplicationData.user?.user?.name = newName
            ApplicationData.user?.user?.title = newTitle
            ApplicationData.user?.user?.email = newEmail

            CustomAlertDialog(
                context = this,
                message = getString(R.string.profile_update_sucessfully),
                primaryKey = getString(R.string.ok),
                primaryKeyAction = {
                    finish()
                }
            ).show()
        }
    }

    override fun onSaveChangesClick(userInfo: UserInfo) {
        val error = if(userInfo.name.isNullOrEmpty()) getString(R.string.enter_name)
        else if(userInfo.title.isNullOrEmpty()) getString(R.string.enter_title)
        else if(!userInfo.email.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(userInfo.email ?: "").matches()) getString(R.string.invalid_email_id)
        else null

        if(error == null) {
            progressBar.setMessage(getString(R.string.updating_profile))

            newName = userInfo.name
            newEmail = userInfo.email
            newTitle = userInfo.title
            mViewModel.requestUpdateUserProfile(newName, newEmail, newTitle)
        }
        else viewDataBinding.root.showMessage(error)
    }

    override fun onEditMobileNoClick(mobileNo: String?) {
        navigateTo(ActivityEnterMobile::class.java, arrayListOf(
            Pair(ENTER_MOBILE_ACTION, EnterMobileAction.UPDATE_MOBILE.action)
        ))
    }

    override fun onEditAddressClick() {
        navigateTo(EditAddressActivity::class.java)
    }

    override fun onChangePasswordClick() {
        navigateTo(ActivityChangePassword::class.java)
    }
}