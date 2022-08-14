package com.artisan.un.ui.home.myOrder

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.artisan.un.apiModel.Order
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ItemPendingBinding
import com.artisan.un.utils.ORDER_ID

class OrderPendingRecyclerViewAdapter : BaseRecyclerViewAdapter() {
    private val mDataList = ArrayList<Order>()

    fun addData(data: ArrayList<Order>) {
        mDataList.clear()
        mDataList.addAll(data)
        notifyItemRangeChanged(0, data.size)
    }

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        ProductListItemHolder(ItemPendingBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = mDataList.size

    inner class ProductListItemHolder(val viewDataBinding : ItemPendingBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            val order = mDataList[adapterPosition]
            viewDataBinding.root.setOnClickListener {
                val intent = Intent(it.context, OrderDetailActivity::class.java)
                intent.putExtra(ORDER_ID, order.order_id)
                it.context.startActivity(intent)
            }
        }
    }
}
