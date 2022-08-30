package com.artisan.un.ui.home.myOrder

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.artisan.un.apiModel.Order
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ItemPendingBinding
import com.artisan.un.ui.order.ActivityOrderDetails
import com.artisan.un.utils.ORDER_ID

class OrderPendingRecyclerViewAdapter(val onPendingClick: (Int) -> Unit) : BaseRecyclerViewAdapter() {
    private var dataList = ArrayList<Order>()
    var isBottomTouched: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: ArrayList<Order>, isLast: Boolean = true) {
        this.isBottomTouched = isLast
        this.dataList.clear()
        this.dataList.addAll(data)
        notifyDataSetChanged()
    }

    fun addData(data: ArrayList<Order>, isLast: Boolean = true) {
        this.isBottomTouched = isLast
        this.dataList.addAll(data)

        notifyItemRangeInserted(this.dataList.size, data.size)
    }

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        ProductListItemHolder(ItemPendingBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = dataList.size

    inner class ProductListItemHolder(val viewDataBinding : ItemPendingBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            val orderData = dataList[adapterPosition]
            viewDataBinding.order = orderData

            viewDataBinding.viewBtn.setOnClickListener {
                val intent = Intent(it.context, ActivityOrderDetails::class.java)
                intent.putExtra(ORDER_ID, orderData.order_id)
                it.context.startActivity(intent)
            }

            viewDataBinding.shippedBtn.setOnClickListener {
                onPendingClick(orderData.order_id)
            }
        }
    }
}
