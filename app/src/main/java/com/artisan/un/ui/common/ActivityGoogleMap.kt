package com.artisan.un.ui.common

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.baseClasses.CommonViewModel
import com.artisan.un.databinding.ActivityGoogleMapBinding
import com.artisan.un.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import java.util.*

class ActivityGoogleMap : BaseActivity<ActivityGoogleMapBinding, CommonViewModel>(R.layout.activity_google_map, CommonViewModel::class), OnMapReadyCallback {
    private lateinit var formatAddress: String
    private lateinit var geocoder: Geocoder
    private lateinit var latLng: LatLng
    private lateinit var address: String
    private var googleMap: GoogleMap? = null

    override fun onCreate() {
        formatAddress = intent.getStringExtra(USER_ADDRESS) ?: ""

        val lat = intent.getDoubleExtra(LATITUDE, 0.0)
        val long = intent.getDoubleExtra(LONGITUDE, 0.0)
        if(lat != 0.0 && long != 0.0) latLng = LatLng(lat, long)

        geocoder = Geocoder(this, Locale.getDefault())

        (supportFragmentManager.findFragmentById(R.id.google_map_container) as? SupportMapFragment)?.getMapAsync(this)

        viewDataBinding.run {
            backBtn.setOnClickListener { onBackPressed() }

            confirmAddressBtn.setOnClickListener {
                if(::address.isInitialized) {
                    val intent = Intent()
                    intent.putExtra("address", address)
                    intent.putExtra("lat", latLng.latitude)
                    intent.putExtra("long", latLng.longitude)

                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    /**
     * Once the map is ready move camera to the [latLng] with animation and display the formatted address on the screen.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap?.setOnCameraIdleListener(CameraMoveListener())

        if(!::latLng.isInitialized) latLng = getLatLongFromAddress(formatAddress)
        viewDataBinding.locationTxt.text = formatAddress

        this.googleMap?.run {
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , 16F))
        }
    }

    /**
     * Get the [LatLng] from formatted [String] address. If LatLng is not found returns an default [LatLng].
     */
    private fun getLatLongFromAddress(formattedAddress: String) = run {
        val addressList = geocoder.getFromLocationName(formattedAddress, 1)

        if(addressList?.isNotEmpty() == true) {
            LatLng(addressList[0].latitude, addressList[0].longitude)
        } else {
            LatLng(DEFAULT_LAT, DEFAULT_LONG)
        }
    }

    /**
     * Get the address from google map api using [latLng]. If address is not found returns an empty [String].
     */
    private fun getAddressFromLatLong(latLng: LatLng) : String = run {
        val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if(addressList?.isNotEmpty() == true) {
            val addressFromGoogleMap = addressList[0]
            addressFromGoogleMap.getAddressLine(0)
        } else {
            ""
        }
    }

    inner class CameraMoveListener : GoogleMap.OnCameraIdleListener {

        /**
         *  Get current position of the camera once camera stops moving and become idle.
         *  Get address of the current location [getAddressFromLatLong] and display to the screen.
         * */
        override fun onCameraIdle() {
            googleMap?.cameraPosition?.target?.let {
                latLng = it
                address = getAddressFromLatLong(it)
                viewDataBinding.locationTxt.text = address
            }
        }
    }
}