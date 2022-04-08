package com.artisan.un.koinModule

import android.util.Log
import com.artisan.un.BuildConfig
import com.artisan.un.helpers.PreferencesHelper
import com.artisan.un.utils.apis.ApiService
import com.artisan.un.utils.isOnline
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

val networkModule = module {

    /**
     * Build and returns an singleton object of [OkHttpClient].
     */
    single {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder()
            .cache(null)
            .addInterceptor(logging)
            .readTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .addInterceptor { chain ->
                if (!isOnline(androidContext().applicationContext)) {
                    throw NoConnectivityException()
                } else {
                    val mPref = PreferencesHelper(get())
                    Log.d("networkModule","token "+mPref.fcmToken)
                    val request = chain.request().newBuilder()
                        .addHeader("app-key", "laravelUNDP")
                        .addHeader("app-type", "Admin")
                        .addHeader("platform", "android")
                        .addHeader("version_code", BuildConfig.VERSION_CODE.toString())
                        .addHeader("version_name", BuildConfig.VERSION_NAME)
                        .addHeader("language", mPref.appLanguage)
                        .addHeader("apitoken", mPref.authToken)
                        .addHeader("devicetoken", mPref.fcmToken)
                        .build()
                    chain.proceed(request)
                }
            }.build()
    }

    /**
     * Build and returns an singleton object of [Retrofit].
     */
    single {
        val fac = GsonConverterFactory.create(GsonBuilder().setLenient().create())
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .client(get())
            .addConverterFactory(fac)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
    }

    /**
     * Builds and returns an implementation of [ApiService] interface using [Retrofit].
     */
    single {
        val ret: Retrofit = get()
        ret.create(ApiService::class.java)
    }
}

class NoConnectivityException : IOException() {
    override val message: String
        get() = "No network available, please check your connection"
}