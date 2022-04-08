package com.artisan.un.ui.userauth.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.apis.ApiService
import com.artisan.un.utils.apis.OTP
import kotlin.collections.HashMap

class ForgotPasswordViewModel(private val apis: ApiService) : BaseViewModel() {
    private val _otp = MutableLiveData<OTP>()
    val otp: LiveData<OTP> = _otp

    var mobile = MutableLiveData<String>()

    fun forgotPassword() {
        val map = HashMap<String, String>()
        map["mobile"] = mobile.value.toString()

        requestData(apis.forgotPassword(map), { _otp.postValue(it.data) })
    }

    fun requestUpdateMobileOtp() {
        val map = HashMap<String, Any>()
        map["mobile"] = mobile.value.toString()
        map["type"] = "updatemobile"

        requestData(apis.resendOTP(map), { _otp.postValue(it.data) })
    }
}