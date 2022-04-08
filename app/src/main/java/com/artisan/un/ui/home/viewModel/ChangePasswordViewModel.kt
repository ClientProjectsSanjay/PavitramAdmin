package com.artisan.un.ui.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.apis.ApiService

class ChangePasswordViewModel(private val apiService: ApiService) : BaseViewModel() {
    private val _changePassword = MutableLiveData<String>()
    val changePassword: LiveData<String> = _changePassword

    fun requestUpdatePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        val requestMap = hashMapOf<String, Any>(
            Pair("old_password", oldPassword),
            Pair("new_password", newPassword),
            Pair("confirm_password", confirmPassword),
        )

        requestData(
            apiService.updatePassword(requestMap),
            { _changePassword.postValue(it.message) },
            priority = ApiService.PRIORITY_HIGH,
        )
    }
}