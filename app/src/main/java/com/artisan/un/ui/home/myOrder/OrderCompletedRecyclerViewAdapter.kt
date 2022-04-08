package com.yuwaah.view.opportunity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ItemCompletedBinding

class OrderCompletedRecyclerViewAdapter(var callBack: (Int) -> Unit) :
    BaseRecyclerViewAdapter() {

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        ProductListItemHolder(ItemCompletedBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = 10

    inner class ProductListItemHolder(val viewDataBinding : ItemCompletedBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            viewDataBinding.root.setOnClickListener {
                callBack(adapterPosition)
            }
        }
    }
}
