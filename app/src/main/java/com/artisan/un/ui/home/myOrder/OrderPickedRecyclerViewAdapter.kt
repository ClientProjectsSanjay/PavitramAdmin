package com.artisan.un.ui.home.myOrder

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.artisan.un.apiModel.Order
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ItemPickedBinding
import com.artisan.un.ui.order.ActivityOrderDetails
import com.artisan.un.utils.ORDER_ID

class OrderPickedRecyclerViewAdapter : BaseRecyclerViewAdapter() {
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
        ProductListItemHolder(ItemPickedBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = dataList.size

    inner class ProductListItemHolder(val viewDataBinding : ItemPickedBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            viewDataBinding.order = dataList[adapterPosition]

            viewDataBinding.viewBtn.setOnClickListener {
                val intent = Intent(it.context, ActivityOrderDetails::class.java)
                intent.putExtra(ORDER_ID, dataList[adapterPosition].order_id)
                it.context.startActivity(intent)
            }
        }
    }
}
