package com.artisan.un.ui.home.myOrder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.apiModel.ErrorPriority
import com.artisan.un.apiModel.MyOrderModel
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.apis.ApiService

class OrderListViewModel(private val apiService: ApiService) : BaseViewModel() {
    private val mOrderListLiveData = MutableLiveData<MyOrderModel>()
    val mOrderListObservable: LiveData<MyOrderModel> = mOrderListLiveData

    fun getSellerOrderList(userId: Int) {
        requestData(
            api = apiService.getSellerOrderList(userId),
            success = { response -> response.data?.let { mOrderListLiveData.postValue(it) } },
            priority = ApiService.PRIORITY_HIGH,
            errorPriority = ErrorPriority.LOW,
        )
    }
}