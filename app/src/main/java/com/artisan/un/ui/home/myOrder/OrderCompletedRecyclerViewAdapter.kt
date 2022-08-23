package com.artisan.un.ui.home.myOrder

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ItemCompletedBinding
import com.artisan.un.ui.order.ActivityOrderDetails
import com.artisan.un.utils.ORDER_ID

class OrderCompletedRecyclerViewAdapter: BaseRecyclerViewAdapter() {

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        ProductListItemHolder(ItemCompletedBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = 10

    inner class ProductListItemHolder(val viewDataBinding : ItemCompletedBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            viewDataBinding.root.setOnClickListener {
                val intent = Intent(it.context, ActivityOrderDetails::class.java)
                intent.putExtra(ORDER_ID, 0)
                it.context.startActivity(intent)
            }
        }
    }
}
