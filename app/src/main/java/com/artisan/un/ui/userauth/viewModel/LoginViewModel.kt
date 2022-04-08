package com.artisan.un.ui.userauth.viewModel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.apiModel.LoginModel
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.apis.ApiService
import com.artisan.un.utils.apis.UserResponse

class LoginViewModel(private val apis: ApiService) : BaseViewModel() {
    private val _userResponse = MutableLiveData<UserResponse>()
    val userResponse: LiveData<UserResponse> = _userResponse

    private val _isProfileDeleted = MutableLiveData<Boolean>()
    val isProfileDeleted: LiveData<Boolean> = _isProfileDeleted

    fun login(auth: LoginModel) {
        val map = HashMap<String, String>()

        map["password"] = auth.password.toString()
        if(Patterns.EMAIL_ADDRESS.matcher(auth.phoneOrEmail ?: "").matches()) map["email"] = auth.phoneOrEmail.toString()
        else map["mobile"] = auth.phoneOrEmail.toString()

        requestData(apis.login(map), { _userResponse.postValue(it.data) }, priority = ApiService.PRIORITY_HIGH)
    }

    fun requestUserProfile() {
        requestData(apis.getProfile(), { _userResponse.postValue(it.data) }, priority = ApiService.PRIORITY_LOW)
    }

    fun deleteProfile(reason: String = "") {
        val map = hashMapOf<String, Any>(Pair("reason", reason))
        requestData(apis.deleteProfile(map), { _isProfileDeleted.postValue(it.status) })
    }
}