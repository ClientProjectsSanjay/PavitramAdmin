package com.artisan.un.baseClasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.artisan.un.apiModel.ErrorData
import com.artisan.un.apiModel.ErrorPriority
import com.artisan.un.utils.apis.ApiService
import com.artisan.un.utils.apis.CallbackWrapper
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

abstract class BaseViewModel : ViewModel() {
    private val _progress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> = _progress

    private val _errorMessage = MutableLiveData<ErrorData>()
    val errorMessage: LiveData<ErrorData> = _errorMessage

    private val _noInternet = MutableLiveData<ErrorData>()
    val noInternet: LiveData<ErrorData> = _noInternet

    private val _userBlocked = MutableLiveData<ErrorData>()
    val userBlocked: LiveData<ErrorData> = _userBlocked

    private val _userAlreadyRegistered = MutableLiveData<ErrorData>()
    val userAlreadyRegistered: LiveData<ErrorData> = _userAlreadyRegistered

    /**
     * This is generic type function which performs as an intermediary for an network request to the server. This handles the response from
     * the server and return either [success] or [failed] depending on the status of the response.
     *
     * @param api is a generic [Observable] of type [T]. This is a network request to the server which returns a [Observable<T>].
     * @param success is a function which accepts [T]. This function called when the [api] network request is successful and return a "true" status.
     * @param failed is a function which accepts ([String] type message, [Int] type statusCode, [ErrorPriority] type errorPriority).
     * This is called when the [api] network request is successful but returns "false" status.
     * @param priority is the priority of the request. Depending on the priority level different actions are performed during the network request.
     * In case of [ApiService.PRIORITY_HIGH] a progress bar is displayed on the screen. Default is [ApiService.PRIORITY_LOW].
     * @param errorPriority is the of type [ErrorPriority]. Which is used to indicate the action to be performed in case an error occurs.
     * If [errorPriority] is [ErrorPriority.MEDIUM] a message is displayed on the screen and in case of [ErrorPriority.LOW] error gets ignored.
     * Default is [ErrorPriority.MEDIUM]
     */
    fun <T> requestData(
        api: Observable<T>,
        success: (m: T) -> Unit,
        failed: (message: String?, statusCode: Int, priority: ErrorPriority) -> Unit = onFailed,
        priority: Int = ApiService.PRIORITY_LOW,
        errorPriority: ErrorPriority = ErrorPriority.MEDIUM
    ) {
        if (priority == ApiService.PRIORITY_HIGH) _progress.postValue(true)

        api.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe(object : CallbackWrapper<T>() {
                override fun onSuccess(t: T) {
                    if (priority == ApiService.PRIORITY_HIGH) _progress.postValue(false)
                    success(t)
                }

                override fun onError(message: String?, statusCode: Int) {
                    if (priority == ApiService.PRIORITY_HIGH) _progress.postValue(false)
                    failed(message, statusCode, errorPriority)
                }
            })
    }

    private val onFailed: (message: String?, code: Int, priority: ErrorPriority) -> Unit =
        { msg, cd, priority ->
            when (cd) {
                -200 -> _noInternet.postValue(ErrorData(msg, cd, priority))
                409 -> _userAlreadyRegistered.postValue(ErrorData(msg, cd, priority))
                403 -> _userBlocked.postValue(ErrorData(msg, cd, priority))
                else -> _errorMessage.postValue(ErrorData(msg, cd, priority))
            }
        }
}