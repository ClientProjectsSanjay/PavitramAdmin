package com.artisan.un.ui.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.apiModel.ArtisanProductsModel
import com.artisan.un.apiModel.ErrorPriority
import com.artisan.un.apiModel.HomeDataModel
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.ApplicationData
import com.artisan.un.utils.apis.ApiService
import com.artisan.un.utils.apis.OTP
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class HomeViewModel(private val apiService: ApiService) : BaseViewModel() {
    private val _homeData = MutableLiveData<HomeDataModel>()
    val homeData: LiveData<HomeDataModel> = _homeData

    private val _artisanProducts = MutableLiveData<ArtisanProductsModel>()
    val artisanProducts: LiveData<ArtisanProductsModel> = _artisanProducts

    private val _requestStatus = MutableLiveData<Boolean>()
    val requestStatus: LiveData<Boolean> = _requestStatus

    private val mQuantityUpdateLiveData = MutableLiveData<String>()
    val mQuantityUpdateObservable: LiveData<String> = mQuantityUpdateLiveData

    private val _otp = MutableLiveData<OTP>()
    val otp: LiveData<OTP> = _otp

    fun requestHomeData(page: Int) {
        val map = HashMap<String, Any>()
        map["page"] = page

        requestData(
            apiService.getHomeData(map),
            { response -> response.data?.let { _homeData.postValue(it) } },
            errorPriority = ErrorPriority.LOW
        )
    }

    fun requestArtisanProducts(page: Int) {
        val map = HashMap<String, Any>()
        map["artisanshgid"] = ApplicationData.user?.user?.id ?: 0
        map["page"] = page

        requestData(
            apiService.getArtisanProducts(map),
            { response -> response.data?.let { _artisanProducts.postValue(it) } },
            errorPriority = ErrorPriority.LOW
        )
    }

    fun requestProfileImageUpload(file: File) {
        val filePart = MultipartBody.Part.createFormData(
            "profileimage",
            file.name,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )
        requestData(
            apiService.uploadProfileImage(filePart),
            { _requestStatus.postValue(it.status) },
            priority = ApiService.PRIORITY_HIGH
        )
    }

    fun requestUpdateUserProfile(name: String?, emailId: String?, title: String?) {
        if (name != null && emailId != null && title != null) {
            val map = HashMap<String, Any>()
            map["name"] = name
            map["title"] = title
            if(!emailId.isNullOrEmpty()) map["email"] = emailId

            requestData(
                apiService.updateUserProfile(map),
                { _requestStatus.postValue(it.status) },
                priority = ApiService.PRIORITY_HIGH
            )
        }
    }

    fun updateProductQuantity(productId: Int, quantity: Int) {
        requestData(
            api = apiService.updateProductQuantity(productId, quantity),
            success = { response -> response.message.let { mQuantityUpdateLiveData.postValue(it) } },
            priority = ApiService.PRIORITY_HIGH,
            errorPriority = ErrorPriority.MEDIUM,
        )
    }
}