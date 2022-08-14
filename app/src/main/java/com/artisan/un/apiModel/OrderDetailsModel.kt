package com.artisan.un.apiModel

import kotlin.collections.ArrayList

data class OrderDetailsModel(
    val order_details: OrderDetails
)

data class OrderDetails(
    val userData: OrderDetailsUserData,
    val sellerData: OrderDetailsSellerData,
    val orderData: OrderDetailsOrderData,
)

data class OrderDetailsUserData(
    val userName: String,
    val userPhone: String,
    val address_line_one: String,
    val pincode: String,
    val district: String,
    val state: String,
    val country: String,
) {
    fun getAddress(): String {
        return "$address_line_one, $district, $state, $country, $pincode"
    }
}

data class OrderDetailsSellerData(
    val sellerName: String,
    val sellerEmail: String?,
    val sellerPhone: String,
    val address_line_one: String,
    val pincode: String,
    val district: String,
    val state: String,
    val country: String,
) {
    fun getAddress(): String {
        return "$address_line_one, $district, $state, $country, $pincode"
    }
}

data class OrderDetailsOrderData(
    val orderStatus: String,
    val paymentStatus: String,
    val deliveryDate: String,
    val purchaseDate: String,
    val deliveryCharge: Float,
    val productItem: ArrayList<OrderDetailsProductItem>,
) {
    fun getFormattedStatus(): String {
        return orderStatus[0].toUpperCase() + orderStatus.substring(1)
    }

    fun getTotal(): String {
        return String.format("%.2f", productItem.map { it.productPrice*it.productQty }.reduce { first, second -> first+second})
    }

    fun getGrandeTotal(): String {
        return String.format("%.2f", productItem.map { it.productPrice*it.productQty }.reduce { first, second -> first+second} + deliveryCharge)
    }
}

data class OrderDetailsProductItem(
    val productName: String,
    val productPrice: Float,
    val productQty: Int,
)