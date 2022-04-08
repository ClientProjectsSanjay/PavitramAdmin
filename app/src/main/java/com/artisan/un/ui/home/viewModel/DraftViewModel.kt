package com.artisan.un.ui.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.apiModel.ErrorPriority
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.apis.ApiService
import com.artisan.un.utils.apis.DraftInfo

class DraftViewModel(val apis: ApiService) : BaseViewModel() {
    private val _product = MutableLiveData<DraftInfo>()
    val product: LiveData<DraftInfo> = _product

    fun getDraftProduct() {
        requestData(apis.getDraftProduct(), {
            _product.value = it.data?.draftproduct
        }, errorPriority = ErrorPriority.LOW)
    }

    fun removeDraftProduct(id: Int?) {
        val map = HashMap<String, Any?>()
        map["productId"] = id ?: 0
        requestData(apis.removeDraftProduct(map), { _product.value = null }, priority = ApiService.PRIORITY_HIGH)
    }
}