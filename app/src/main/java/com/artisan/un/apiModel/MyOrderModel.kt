package com.artisan.un.apiModel

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
)