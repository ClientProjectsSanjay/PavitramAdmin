package com.artisan.un.baseClasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.apiModel.ErrorPriority
import com.artisan.un.apiModel.NotificationModel
import com.artisan.un.utils.apis.ApiService

class CommonViewModel(private val apiService: ApiService) : BaseViewModel() {
    private val _notification = MutableLiveData<NotificationModel>()
    val notification: LiveData<NotificationModel> = _notification

    fun requestNotifications(page: Int) {
        val map = HashMap<String, Any>()
        map["page"] = page

        requestData(
            apiService.getNotification(map),
            { _notification.postValue(it.data) },
            errorPriority = ErrorPriority.LOW
        )
    }
}