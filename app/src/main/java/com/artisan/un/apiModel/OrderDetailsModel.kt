package com.artisan.un.apiModel

import java.util.*
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
    val orderPdfPath: String?,
    val orderStatus: String,
    val paymentStatus: String,
    val deliveryDate: String,
    val purchaseDate: String,
    val deliveryCharge: String,
    val productItem: ArrayList<OrderDetailsProductItem>,
) {
    fun getFormattedStatus(): String {
        return orderStatus[0].uppercaseChar() + orderStatus.substring(1)
    }

    fun getFormattedPaymentStatus(): String {
        return paymentStatus[0].uppercaseChar() + paymentStatus.substring(1)
    }

    fun getTotal(): String {
        return String.format("%.2f", productItem.map { it.productPrice*it.productQty }.reduce { first, second -> first+second})
    }

    fun getGrandeTotal(): String {
        return String.format("%.2f", productItem.map { it.productPrice*it.productQty }.reduce { first, second -> first+second} + deliveryCharge.toFloat())
    }
}

data class OrderDetailsProductItem(
    val productName: String,
    val productPrice: Float,
    val productQty: Int,
)