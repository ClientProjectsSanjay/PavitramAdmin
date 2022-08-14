package com.artisan.un.ui.home.myOrder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.apiModel.ErrorPriority
import com.artisan.un.apiModel.OrderDetailsModel
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.apis.ApiService

class OrderDetailsViewModel(private val apiService: ApiService) : BaseViewModel() {
    private val mOrderDetailsLiveData = MutableLiveData<OrderDetailsModel>()
    val mOrderDetailsObservable: LiveData<OrderDetailsModel> = mOrderDetailsLiveData

    fun getSellerOrderDetails(orderId: Int) {
        requestData(
            api = apiService.getSellerOrderDetails(orderId),
            success = { response -> response.data?.let { mOrderDetailsLiveData.postValue(it) } },
            priority = ApiService.PRIORITY_HIGH,
            errorPriority = ErrorPriority.LOW,
        )
    }
}