package com.artisan.un.ui.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.DEFAULT_COUNTRY_CODE
import com.artisan.un.utils.DEFAULT_STATE_CODE
import com.artisan.un.utils.apis.*

class EditAddressViewModel(private val apis: ApiService) : BaseViewModel() {
    private val _requestStatus = MutableLiveData<Boolean>()
    val requestStatus: LiveData<Boolean> = _requestStatus

    private val _stateList = MutableLiveData<ArrayList<StateData>>()
    val stateList: LiveData<ArrayList<StateData>> = _stateList

    private val _cityList = MutableLiveData<ArrayList<CityData>>()
    val cityList: LiveData<ArrayList<CityData>> = _cityList

    val cityNameList: LiveData<ArrayList<String>> = Transformations.map(_cityList) { list ->
        ArrayList(list.map { it.name ?: "" }.toList())
    }

    val stateNameList: LiveData<ArrayList<String>> = Transformations.map(_stateList) { list ->
        ArrayList(list.map { it.name ?: "" }.toList())
    }

    init {
        getState()
    }

    private fun getState() {
        val map = HashMap<String, Any>()
        map["country_id"] = DEFAULT_COUNTRY_CODE

        requestData(apis.getState(map), { it.data?.states?.let { states -> _stateList.postValue(states) } })
    }

    fun getCity(stateId: Int?) {
        val map = HashMap<String, Any>()
        map["state_id"] = stateId ?: 0

        requestData(apis.getCity(map), { it.data?.district?.let { districts -> _cityList.postValue(districts) } })
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
        map["tehsil_id"] = 0
        map["address_type"] = "registered"
        map["lat"] = address.lat
        map["log"] = address.log

        requestData(apis.updateAddress(map), { _requestStatus.postValue(it.status) }, priority = ApiService.PRIORITY_HIGH)
    }
}