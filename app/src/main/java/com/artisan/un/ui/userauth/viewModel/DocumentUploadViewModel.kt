package com.artisan.un.ui.userauth.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.DEFAULT_COUNTRY_CODE
import com.artisan.un.utils.DEFAULT_STATE_CODE
import com.artisan.un.utils.apis.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class DocumentUploadViewModel(private val apis: ApiService) : BaseViewModel() {
    private val _cityRegister = MutableLiveData<ArrayList<CityData>>()
    private val _cityOffice = MutableLiveData<ArrayList<CityData>>()
    private val _stateRegister = MutableLiveData<ArrayList<StateData>>()
    private val _stateOffice = MutableLiveData<ArrayList<StateData>>()
    private val _countryData = MutableLiveData<ArrayList<CountryData>>()
    private val _tehsilData = MutableLiveData<ArrayList<TehsilData>>()

    private val _requestStatus = MutableLiveData<Boolean>()
    val requestStatus: LiveData<Boolean> = _requestStatus

    val cityArrayRegister: LiveData<ArrayList<String>?> = Transformations.map(_cityRegister) {
        ArrayList(it.map { city -> city.name ?: "" }.toList())
    }

    val tehsilArray: LiveData<ArrayList<String>?> = Transformations.map(_tehsilData) {
        ArrayList(it.map { tehsil -> tehsil.name ?: "" }.toList())
    }

    fun cityRegister(): LiveData<ArrayList<CityData>> = _cityRegister
    fun stateRegister(): LiveData<ArrayList<StateData>> = _stateRegister
    fun countryData(): LiveData<ArrayList<CountryData>> = _countryData
    fun tehsilData(): LiveData<ArrayList<TehsilData>> = _tehsilData

    val aadharImageFrontName = MutableLiveData<String>()
    val aadharImageBackName = MutableLiveData<String>()
    val panImageName = MutableLiveData<String>()
    val brnImageName = MutableLiveData<String>()

    /*-------- AADHAR CARD FORM FILED  -----------*/
    val aadharNumber = MutableLiveData<String>()
    val aadharName = MutableLiveData<String>()
    val aadharDOB = MutableLiveData<String>()
    val aadharImageFront = MutableLiveData<String>()
    val aadharImageBack = MutableLiveData<String>()

    /*-------- Bank CARD FORM FILED  -----------*/
    val bankName = MutableLiveData<String>()
    val bankNumber = MutableLiveData<String>()
    val bankGst = MutableLiveData<String>()
    val bankISFC = MutableLiveData<String>()
    val bankFCC = MutableLiveData<String>()

    /*-------- PAN CARD FORM FILED  -----------*/
    val panNumber = MutableLiveData<String>()
    val panName = MutableLiveData<String>()
    val panDOB = MutableLiveData<String>()
    val panImage = MutableLiveData<String>()

    /*-------- BRN CARD FORM FILED  -----------*/
    val brnNumber = MutableLiveData<String>()
    val brnName = MutableLiveData<String>()
    val brnImage = MutableLiveData<String>()

    /*-------- REGISTERED ADDRESS FORM FIELDS-------*/
    val addressRegisterLine1 = MutableLiveData<String>()
    val addressRegisterCountry = MutableLiveData<String>()
    val addressRegisterState = MutableLiveData<String>()
    val addressRegisterCity = MutableLiveData<String>()
    val addressRegisterTehsil = MutableLiveData<String>()
    val addressRegisterPin = MutableLiveData<String>()

    init {
        getCountries()
        getState()
        getCity()
    }

    private fun getCountries() {
        requestData(
            apis.getCountries(), {
                _countryData.postValue(it.data?.country)

                it.data?.country?.find { country -> country.id == DEFAULT_COUNTRY_CODE }?.let { country ->
                    addressRegisterCountry.postValue(country.name)
                }
            },
        )
    }

    private fun getState() {
        val map = HashMap<String, Any>()
        map["country_id"] = DEFAULT_COUNTRY_CODE

        requestData(apis.getState(map), {
            _stateRegister.postValue(it.data?.states)
            _stateOffice.postValue(it.data?.states)

            it.data?.states?.find { state -> state.id == DEFAULT_STATE_CODE }?.let { state ->
                addressRegisterState.postValue(state.name)
            }
        })
    }

    private fun getCity() {
        val map = HashMap<String, Any>()
        map["state_id"] = DEFAULT_STATE_CODE

        requestData(apis.getCity(map), {
            _cityRegister.postValue(it.data?.district)
            _cityOffice.postValue(it.data?.district)
        })
    }

    fun getTehsil(city_id: Int) {
        val map = HashMap<String, Any>()
        map["city_id"] = city_id

        requestData(apis.getTehsil(map), { _tehsilData.postValue(it.data?.tehsil) })
    }

    fun uploadDocuments(fields: HashMap<String, RequestBody>, files: ArrayList<MultipartBody.Part>) {
        requestData(apis.addDocumentAndAddress(fields, files), { _requestStatus.postValue(it.status) }, priority = ApiService.PRIORITY_HIGH)
    }

    fun reUploadDocument(fields: HashMap<String, RequestBody>, files: ArrayList<MultipartBody.Part>) {
        requestData(apis.reuploadDocument(fields, files), { _requestStatus.postValue(it.status) }, priority = ApiService.PRIORITY_HIGH)
    }
}