package com.artisan.un.apiModel

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class MyOrderModel(
    val order_list: ArrayList<Order>,
    val pagination: Pagination?=null
)

data class Pagination(
    val currentPage: Int?=null,
    val last_page: Int?=null,
    val per_page: Int?=null
)

data class Order(
    val display_order_id: String,
    val order_amount: Int,
    val order_date: String,
    val order_id: Int,
    val order_status: String,
    val payment_status: String,
    val seller_name: String
) {
    fun getOrderStatus(): String = run {
        order_status.lowercase().let { it[0].uppercase() + it.substring(1) }
    }

    fun getOrderDate(): String = run {
        val formattedDate: Date? = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault()).parse(order_date)
        formattedDate?.let { SimpleDateFormat("yyyy-MM-dd | HH:mm:ss", Locale.getDefault()).format(it) } ?: ""
    }
}