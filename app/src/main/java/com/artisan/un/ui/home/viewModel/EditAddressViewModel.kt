package com.artisan.un.ui.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.DEFAULT_STATE_CODE
import com.artisan.un.utils.apis.*

class EditAddressViewModel(private val apis: ApiService) : BaseViewModel() {
    private val _requestStatus = MutableLiveData<Boolean>()
    val requestStatus: LiveData<Boolean> = _requestStatus

    private val _cityList = MutableLiveData<ArrayList<CityData>>()
    val cityList: LiveData<ArrayList<CityData>> = _cityList
    val cityNameList: LiveData<ArrayList<String>> = Transformations.map(_cityList) { list ->
        ArrayList(list.map { it.name ?: "" }.toList())
    }

    private val _tehsilList = MutableLiveData<ArrayList<TehsilData>>()
    val tehsilList: LiveData<ArrayList<TehsilData>> = _tehsilList
    val tehsilNameList: LiveData<ArrayList<String>> = Transformations.map(_tehsilList) { list ->
        ArrayList(list.map { it.name ?: "" }.toList())
    }

    init {
        getCity()
    }

    private fun getCity() {
        val map = HashMap<String, Any>()
        map["state_id"] = DEFAULT_STATE_CODE

        requestData(apis.getCity(map), { _cityList.postValue(it.data?.district) })
    }

    fun getTehsil(city_id: Int) {
        val map = HashMap<String, Any>()
        map["city_id"] = city_id

        requestData(apis.getTehsil(map), { _tehsilList.postValue(it.data?.tehsil) })
    }

    fun requestUpdateAddress(address: UserAddress) {
        val map = HashMap<String, Any>()
        map["id"] = address.id ?: 0
        map["address_line_one"] = address.address_line_one ?: ""
        map["address_line_two"] = address.address_line_two ?: ""
        map["pincode"] = address.pincode ?: ""
        map["district"] = address.districtId ?: 0
        map["country"] = address.countryId ?: 0
        map["state"] = address.stateId ?: 0
        map["tehsil_id"] = address.tehsil_id ?: 0
        map["address_type"] = "registered"
        map["lat"] = address.lat
        map["log"] = address.log

        requestData(apis.updateAddress(map), { _requestStatus.postValue(it.status) }, priority = ApiService.PRIORITY_HIGH)
    }
}