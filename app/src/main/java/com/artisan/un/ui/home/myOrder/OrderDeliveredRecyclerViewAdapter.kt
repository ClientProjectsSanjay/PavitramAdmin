package com.artisan.un.ui.home.myOrder

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.artisan.un.apiModel.Order
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ItemShippedBinding
import com.artisan.un.ui.order.ActivityOrderDetails
import com.artisan.un.utils.ORDER_ID

class OrderDeliveredRecyclerViewAdapter : BaseRecyclerViewAdapter() {
    private var dataList = ArrayList<Order>()
    var isBottomTouched: Boolean = false

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
        ProductListItemHolder(ItemShippedBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = dataList.size

    inner class ProductListItemHolder(val viewDataBinding : ItemShippedBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            viewDataBinding.order = dataList[adapterPosition]

            viewDataBinding.root.setOnClickListener {
                val intent = Intent(it.context, ActivityOrderDetails::class.java)
                intent.putExtra(ORDER_ID, dataList[adapterPosition].order_id)
                it.context.startActivity(intent)
            }
        }
    }
}
