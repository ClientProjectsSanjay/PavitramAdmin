package com.artisan.un.ui.userauth.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.apiModel.DocumentsFormDataModel
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.DEFAULT_COUNTRY_CODE
import com.artisan.un.utils.apis.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class DocumentUploadViewModel(private val apis: ApiService) : BaseViewModel() {
    init {
        getCountries()
        getState()
    }

    val mDocumentsFormModel = MutableLiveData(DocumentsFormDataModel())

    private val mRequestStatusLiveData = MutableLiveData<Boolean>()
    val mRequestStatusObservable: LiveData<Boolean> = mRequestStatusLiveData

    private val mCountryLiveData = MutableLiveData<ArrayList<CountryData>>()
    val mCountryDataObservable: LiveData<ArrayList<CountryData>> = mCountryLiveData

    private val mStateLiveData = MutableLiveData<ArrayList<StateData>>()
    val mStateDataObservable: LiveData<ArrayList<StateData>> = mStateLiveData

    private val mCityLiveData = MutableLiveData<ArrayList<CityData>>()
    val mCityDataObservable: LiveData<ArrayList<CityData>> = mCityLiveData

    private val mUserResponse = MutableLiveData<UserResponse>()
    val mUserResponseObservable: LiveData<UserResponse> = mUserResponse

    private fun getCountries() {
        requestData(
            apis.getCountries(), { response ->
                response.data?.country?.let { mCountryLiveData.postValue(it) }

                response.data?.country?.find { country -> country.id == DEFAULT_COUNTRY_CODE }?.let { country ->
                    mDocumentsFormModel.postValue(mDocumentsFormModel.value?.apply { registeredAddressCountry = country.name })
                }
            },
        )
    }

    private fun getState() {
        val map = HashMap<String, Any>()
        map["country_id"] = DEFAULT_COUNTRY_CODE

        requestData(apis.getState(map), { response ->
            response.data?.states?.let { mStateLiveData.postValue(it) }
        })
    }

    fun getCity(stateId: Int) {
        val map = HashMap<String, Any>()
        map["state_id"] = stateId

        requestData(apis.getCity(map), { response ->
            response.data?.district?.let { mCityLiveData.postValue(it) }
        })
    }

    fun requestUserProfile() {
        requestData(apis.getProfile(), { response -> response.data?.let { mUserResponse.postValue(it) } }, priority = ApiService.PRIORITY_HIGH)
    }

    fun uploadDocuments() {
        val fields: HashMap<String, RequestBody> = mDocumentsFormModel.value?.getRequestFields(
            mCountryLiveData,
            mStateLiveData,
            mCityLiveData
        ) ?: HashMap()
        val files: ArrayList<MultipartBody.Part> = mDocumentsFormModel.value?.getRequestFiles() ?: ArrayList()

        requestData(apis.addDocumentAndAddress(fields, files), { mRequestStatusLiveData.postValue(it.status) }, priority = ApiService.PRIORITY_HIGH)
    }

    fun reUploadDocument() {
        val fields: HashMap<String, RequestBody> = mDocumentsFormModel.value?.getRequestFields(
            mCountryLiveData,
            mStateLiveData,
            mCityLiveData
        ) ?: HashMap()
        val files: ArrayList<MultipartBody.Part> = mDocumentsFormModel.value?.getRequestFiles() ?: ArrayList()

        requestData(apis.reuploadDocument(fields, files), { mRequestStatusLiveData.postValue(it.status) }, priority = ApiService.PRIORITY_HIGH)
    }
}