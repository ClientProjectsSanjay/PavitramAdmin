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

    private val mPendingOrderListLiveData = MutableLiveData<MyOrderModel>()
    val mPendingOrderListObservable: LiveData<MyOrderModel> = mPendingOrderListLiveData

    private val mDeliveredOrderListLiveData = MutableLiveData<MyOrderModel>()
    val mDeliveredOrderListObservable: LiveData<MyOrderModel> = mDeliveredOrderListLiveData

    private val mShippedOrderListLiveData = MutableLiveData<MyOrderModel>()
    val mShippedOrderListObservable: LiveData<MyOrderModel> = mShippedOrderListLiveData

    private val mPickedOrderListLiveData = MutableLiveData<MyOrderModel>()
    val mPickedOrderListObservable: LiveData<MyOrderModel> = mPickedOrderListLiveData

    fun getUserOrderDetails(orderId: Int) {
        requestData(
            api = apiService.getUserOrderDetails(orderId),
            success = { response -> response.data?.let { mOrderDetailsLiveData.postValue(it) } },
            priority = ApiService.PRIORITY_HIGH,
            errorPriority = ErrorPriority.LOW,
        )
    }

    fun requestPendingOrderList(userId: Int, page: Int) {
        val map = HashMap<String, Any>()
        map["page"] = page
        map["user_id"] = userId

        requestData(
            api = apiService.getPendingOrderList(map),
            success = { response -> response.data?.let { mPendingOrderListLiveData.postValue(it) } }
        )
    }

    fun requestDeliveredOrderList(userId: Int, page: Int) {
        val map = HashMap<String, Any>()
        map["page"] = page
        map["user_id"] = userId

        requestData(
            api = apiService.getDeliveredOrderList(map),
            success = { response -> response.data?.let { mDeliveredOrderListLiveData.postValue(it) } }
        )
    }

    fun requestShippedOrderList(userId: Int, page: Int) {
        val map = HashMap<String, Any>()
        map["page"] = page
        map["user_id"] = userId

        requestData(
            api = apiService.getShippedOrderList(map),
            success = { response -> response.data?.let { mShippedOrderListLiveData.postValue(it) } }
        )
    }

    fun requestPickedOrderList(userId: Int, page: Int) {
        val map = HashMap<String, Any>()
        map["page"] = page
        map["user_id"] = userId

        requestData(
            api = apiService.getPickedOrderList(map),
            success = { response -> response.data?.let { mPickedOrderListLiveData.postValue(it) } }
        )
    }
}