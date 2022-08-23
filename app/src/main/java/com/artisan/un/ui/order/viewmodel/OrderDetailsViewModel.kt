package com.artisan.un.ui.order.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.apiModel.ErrorPriority
import com.artisan.un.apiModel.MyOrderModel
import com.artisan.un.apiModel.OrderDetailsModel
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.apis.ApiService

class OrderDetailsViewModel(private val apiService: ApiService) : BaseViewModel() {
    private val mOrderDetailsLiveData = MutableLiveData<OrderDetailsModel>()
    val mOrderDetailsObservable: LiveData<OrderDetailsModel> = mOrderDetailsLiveData

    private val mMyOrderListLiveData = MutableLiveData<MyOrderModel>()
    val mMyOrderListObservable: LiveData<MyOrderModel> = mMyOrderListLiveData

    fun getUserOrderDetails(orderId: Int) {
        requestData(
            api = apiService.getUserOrderDetails(orderId),
            success = { response -> response.data?.let { mOrderDetailsLiveData.postValue(it) } },
            priority = ApiService.PRIORITY_HIGH,
            errorPriority = ErrorPriority.LOW,
        )
    }

    fun requestMyOrderList(userId: Int, page: Int) {
        val map = HashMap<String, Any>()
        map["page"] = page
        map["user_id"] = userId

        requestData(
            api = apiService.getOrderList(map),
            success = { response -> response.data?.let { mMyOrderListLiveData.postValue(it) } }
        )
    }
}