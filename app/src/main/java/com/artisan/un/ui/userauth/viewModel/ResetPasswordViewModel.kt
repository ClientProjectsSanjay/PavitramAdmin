package com.artisan.un.ui.userauth.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.apis.ApiService
import com.artisan.un.utils.apis.BaseResponse
import kotlin.collections.HashMap

class ResetPasswordViewModel(private val apis: ApiService) : BaseViewModel() {
    private val _requestStatus = MutableLiveData<BaseResponse<Any>>()
    val requestStatus: LiveData<BaseResponse<Any>> = _requestStatus

    var mobile = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var confirmPassword = MutableLiveData<String>()

    fun resetPassword(otp: Int) {
        val map = HashMap<String, Any>()
        map["mobile"] = mobile.value.toString()
        map["password"] = password.value.toString()
        map["otp"] = otp
        requestData(apis.changePassword(map), { _requestStatus.postValue(it) }, priority = ApiService.PRIORITY_HIGH)
    }
}