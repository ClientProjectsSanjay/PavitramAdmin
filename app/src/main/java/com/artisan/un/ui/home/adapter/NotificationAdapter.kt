package com.artisan.un.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.artisan.un.apiModel.NotificationData
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ViewNotificationListItemBinding

class NotificationAdapter : BaseRecyclerViewAdapter() {
    private var dataList: ArrayList<NotificationData>? = null
    var isBottomTouched: Boolean = false

    fun setData(dataList: ArrayList<NotificationData>?, isLast: Boolean = true) {
        this.isBottomTouched = isLast
        this.dataList = dataList
        notifyDataSetChanged()
    }

    fun addData(dataList: ArrayList<NotificationData>?, isLast: Boolean = true) {
        this.isBottomTouched = isLast

        if(this.dataList == null) this.dataList = ArrayList()
        dataList?.toList()?.let { this.dataList?.addAll(it) }

        notifyItemRangeInserted(this.dataList?.size ?: 0, dataList?.size ?: 0)
    }

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        ProductListItemHolder(ViewNotificationListItemBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = dataList?.size ?: 0

    inner class ProductListItemHolder(val viewDataBinding : ViewNotificationListItemBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            dataList?.let { viewDataBinding.notification = it[adapterPosition] }
        }
    }
}
