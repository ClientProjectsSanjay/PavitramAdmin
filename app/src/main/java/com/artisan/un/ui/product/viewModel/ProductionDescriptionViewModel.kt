package com.artisan.un.ui.product.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.artisan.un.apiModel.ProductDetailsModel
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.apis.ApiService

class ProductionDescriptionViewModel(private val apis: ApiService) : BaseViewModel() {
    private val _productDetails = MutableLiveData<ProductDetailsModel>()
    val productDetails: LiveData<ProductDetailsModel> = _productDetails

    private val _requestStatus = MutableLiveData<Boolean>()
    val requestStatus: LiveData<Boolean> = _requestStatus

    fun requestProductDetails(productId: Int?) {
        productId?.let { id ->
            val map = HashMap<String, Any>()
            map["productId"] = id

            requestData(apis.getProductDetails(map), { _productDetails.postValue(it.data) })
        }
    }

    fun deleteProduct(productId: Int?) {
        productId?.let { id ->
            val map = HashMap<String, Any>()
            map["productId"] = id

            requestData(apis.deleteProduct(map), { _requestStatus.postValue(it.status) })
        }
    }
}