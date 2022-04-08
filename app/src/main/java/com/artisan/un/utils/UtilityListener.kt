package com.artisan.un.utils

import android.widget.CheckBox
import com.artisan.un.apiModel.AuthModel
import com.artisan.un.apiModel.LoginModel
import com.artisan.un.utils.apis.UserAddress
import com.artisan.un.utils.apis.UserInfo

interface DocumentVerificationListener {
    fun onBackClick()
    fun onVerifyClick()
    fun doPickAADHARDOB()
    fun doPickAADHARFront()
    fun doPickAADHARBack()
    fun doPickPAN()
    fun doPickPANBOB()
    fun doPickBRN()
}

interface ForgotPasswordListener {
    fun onResetNowClick(mobileNo: String?)
    fun onBackClick()
}

interface LanguageSelectionListener {
    fun selectLanguage(language: AppLanguage)
    fun onProceedClick()
    fun onCancelClick() {}
}

interface LoginListener {
    fun forgotPasswordClick()
    fun registerNowClick()
    fun login(loginModel: LoginModel)
}

interface OtpListener {
    fun onBackClick()
    fun onVerifyClick()
}

interface RegisterListener {
    fun onSignUpClick(promotionalEmails: CheckBox, termsAcceptance: CheckBox, authModel: AuthModel)
    fun onLoginClick()
}

interface ResetPasswordListener {
    fun onResetClick(password: String?, confirmPassword: String?)
}

interface ProductDescriptionListener {
    fun onEditClick()
    fun onDeleteClick()
}

interface ProductListener {
    fun onContinueClick()
    fun onSaveAsDraftClick() {}
}

interface AlertDialogListener {
    fun onPrimaryKeyClick()
    fun onSecondaryKeyClick()
}

interface AddMediaListener {
    fun onGalleryClick()
    fun onCameraClick()
    fun onSaveClick()
}

interface AppBarListener {
    fun onBackClick() {}
    fun onMenuDeleteClick() {}
    fun onEditClick() {}
    fun onNotificationClick() {}
}

interface ProfileListener {
    fun onEditProfileClick()
}

interface EditProfileListener {
    fun onSaveChangesClick(userInfo: UserInfo)
    fun onEditAddressClick()
    fun onChangePasswordClick()
    fun onEditMobileNoClick(mobileNo: String?)
}

interface EditAddressListener {
    fun onSaveChangesClick(address: UserAddress)
}

interface DrawerListener {
    fun onMenuItemClick(type: MenuType)
    fun onLogoutClick()
    fun onMenuIconClick()
}

interface HomePageListener {
    fun onSearchClick()
}