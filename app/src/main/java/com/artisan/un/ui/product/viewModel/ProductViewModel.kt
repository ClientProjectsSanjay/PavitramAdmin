package com.artisan.un.ui.product.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.apiModel.ErrorPriority
import com.artisan.un.apiModel.SearchProductModel
import com.artisan.un.apiModel.SubCategoryWiseProductListModel
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.ApplicationData
import com.artisan.un.utils.apis.ApiService

class ProductViewModel(private val apiService: ApiService) : BaseViewModel() {
    private val _products = MutableLiveData<SubCategoryWiseProductListModel>()
    val products: LiveData<SubCategoryWiseProductListModel> = _products

    private val _searchProduct = MutableLiveData<SearchProductModel>()
    val searchProduct: LiveData<SearchProductModel> = _searchProduct

    private val mQuantityUpdateLiveData = MutableLiveData<String>()
    val mQuantityUpdateObservable: LiveData<String> = mQuantityUpdateLiveData

    fun requestSubCategoryProductList(subCategoryId: Int?, page: Int) {
        val map = HashMap<String, Any>()
        map["artisanshgid"] = ApplicationData.user?.user?.id ?: ""
        map["subcategoryId"] = subCategoryId ?: 0
        map["page"] = page

        requestData(
            apiService.getSubCategoryProducts(map),
            { _products.postValue(it.data!!) },
            errorPriority = ErrorPriority.LOW,
        )
    }

    fun searchProduct(key: String, page: Int) {
        val map = HashMap<String, Any>()
        map["keyword"] = key
        map["page"] = page

        requestData(apiService.searchProduct(map), { _searchProduct.postValue(it.data!!) })
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