package com.artisan.un.ui.userauth.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.apis.ApiService
import com.artisan.un.utils.apis.OTP
import com.artisan.un.utils.apis.UserResponse
import kotlin.collections.HashMap

class OTPViewModel(private val apis: ApiService) : BaseViewModel() {
    private val _userProfile = MutableLiveData<UserResponse>()
    val userProfile: LiveData<UserResponse> = _userProfile

    private val _requestStatus = MutableLiveData<Boolean>()
    val requestStatus: LiveData<Boolean> = _requestStatus

    private val _otp = MutableLiveData<OTP>()
    val otp: LiveData<OTP> = _otp

    var mRedirect: String? = ""
    var mMobile: String? = ""

    fun otpVerify(otp: String, isSignUp: Boolean) {
        val map = HashMap<String, String>()
        map["mobile"] = mMobile.toString()
        map["otp"] = otp
        if(isSignUp) map["type"] = "signup"
        requestData(apis.verifyOTP(map), { _userProfile.postValue(it.data) }, priority = ApiService.PRIORITY_HIGH)
    }

    fun otpResend() {
        val map = HashMap<String, Any>()
        map["mobile"] = mMobile.toString()
        requestData(apis.resendOTP(map), { _otp.postValue(it.data) })
    }

    fun requestMobileNoUpdate(mobileNo: String?, otp: Int?) {
        mobileNo?.let {
            val map = HashMap<String, Any>()
            map["mobile"] = mobileNo.toString()
            map["otp"] = otp ?: 0

            requestData(apis.updateMobile(map), { _requestStatus.postValue(it.status) }, priority = ApiService.PRIORITY_HIGH)
        }
    }
}