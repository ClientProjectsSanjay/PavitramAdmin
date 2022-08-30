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

    private val mHandoverResponseLiveData = MutableLiveData<String>()
    val mHandoverResponseObservable: LiveData<String> = mHandoverResponseLiveData

    private val mShippedResponseLiveData = MutableLiveData<String>()
    val mShippedResponseObservable: LiveData<String> = mShippedResponseLiveData

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

    fun markPackageHandover(userId: Int, orderId: Int) {
        val map = HashMap<String, Any>()
        map["user_id"] = userId
        map["order_id"] = orderId

        requestData(
            api = apiService.markPackageHandover(map),
            success = { response -> mHandoverResponseLiveData.postValue(response.message) },
            priority = ApiService.PRIORITY_HIGH,
            errorPriority = ErrorPriority.MEDIUM,
        )
    }

    fun markPackageShipped(orderId: Int, length: Float, breadth: Float, height: Float, weight: Float) {
        val map = HashMap<String, Any>()
        map["order_id"] = orderId
        map["package_length"] = length
        map["package_breadth"] = breadth
        map["package_height"] = height
        map["package_weight"] = weight

        requestData(
            api = apiService.markPackageShipped(map),
            success = { response -> mShippedResponseLiveData.postValue(response.message) },
            priority = ApiService.PRIORITY_HIGH,
            errorPriority = ErrorPriority.MEDIUM,
        )
    }
}