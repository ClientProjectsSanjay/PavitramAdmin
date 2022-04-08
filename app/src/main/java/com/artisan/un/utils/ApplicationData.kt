package com.artisan.un.utils

import com.artisan.un.apiModel.ProductBundle
import com.artisan.un.utils.apis.UserResponse
import com.google.android.gms.maps.model.LatLng

object ApplicationData {
    var user: UserResponse? = null
    var productDeleted: Boolean = false
    var productBundle: ProductBundle? = null
}