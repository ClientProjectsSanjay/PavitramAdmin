package com.artisan.un.ui.home.myOrder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ItemPendingBinding

class OrderPendingRecyclerViewAdapter(var callBack: (Int) -> Unit) : BaseRecyclerViewAdapter() {

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        ProductListItemHolder(ItemPendingBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = 10

    inner class ProductListItemHolder(val viewDataBinding : ItemPendingBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            viewDataBinding.root.setOnClickListener {
                callBack(adapterPosition)
            }
        }
    }
}
