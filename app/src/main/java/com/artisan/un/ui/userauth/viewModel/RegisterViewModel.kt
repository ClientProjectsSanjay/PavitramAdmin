package com.artisan.un.ui.userauth.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.apiModel.AuthModel
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.Roles
import com.artisan.un.utils.apis.ApiService
import com.artisan.un.utils.apis.OTP

class RegisterViewModel(private val apis: ApiService) : BaseViewModel() {
    private val _otp = MutableLiveData<OTP>()
    val otp: LiveData<OTP> = _otp

    fun registerUser(authModel: AuthModel) {
        val map = HashMap<String, String>()
        map["name"] = authModel.name.toString()
        map["password"] = authModel.password.toString()
        map["mobile"] = authModel.mobileNo.toString()
        map["is_promotional_mail"] = (authModel.isPromotionalEmailEnabled).toString()
        map["role_id"] = (if (authModel.userType == Roles.Artisan.NAME) Roles.Artisan.ID else Roles.SHG.ID).toString()
        if(!authModel.emailId.isNullOrEmpty()) map["email"] = authModel.emailId.toString()

        requestData(apis.registration(map), { _otp.postValue(it.data) }, priority = ApiService.PRIORITY_HIGH)
    }
}