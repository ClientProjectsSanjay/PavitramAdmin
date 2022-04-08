package com.artisan.un.utils.apis

import android.util.Log
import com.artisan.un.koinModule.NoConnectivityException
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import rx.Subscriber
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

abstract class CallbackWrapper<T> : Subscriber<T>() {
    abstract fun onSuccess(t: T)
    abstract fun onError(message: String?, statusCode: Int)
    override fun onCompleted() {}
    override fun onError(throwable: Throwable) {
        throwable.printStackTrace()
        if(throwable is NoConnectivityException) {
            onError("No Internet Connection", -200)
        } else if (throwable is HttpException || throwable is SocketException || throwable is SocketTimeoutException || throwable is IOException) {
            onError(throwable.message, 502)
        } else {
            onError(throwable.message, 0)
        }
        Log.e("error", throwable.message!!)
    }

    override fun onNext(t: T) {
        val errorCheckModel: ErrorCheckModel = checkError(t)
        Log.e("HTTP : API RESULT", Gson().toJson(t))
        if (errorCheckModel.status) {
            onSuccess(t)
        } else {
            onError(errorCheckModel.message, errorCheckModel.statusCode)
        }
    }

    private fun checkError(data: Any?): ErrorCheckModel {
        val errorCheckModel = ErrorCheckModel()
        data?.let {
            try {
                val `object` = JSONObject(Gson().toJson(it))
                errorCheckModel.message = `object`.getString("message")
                errorCheckModel.status = `object`.getBoolean("status")
                errorCheckModel.statusCode = `object`.getInt("statusCode")
            } catch (e: JSONException) {

            }
        }
        return errorCheckModel
    }

    inner class ErrorCheckModel {
        var status = false
        var statusCode = 201
        var message = ""
    }
}