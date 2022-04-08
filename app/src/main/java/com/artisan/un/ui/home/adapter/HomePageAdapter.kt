package com.artisan.un.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.artisan.un.apiModel.CategoryWiseProductModel
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ViewListLoadingBinding
import com.artisan.un.databinding.ViewProductHeadingBinding

class HomePageAdapter : BaseRecyclerViewAdapter() {
    private var dataList: ArrayList<CategoryWiseProductModel>? = null
    var isBottomTouched: Boolean = false

    fun resetData() {
        dataList = null
        isBottomTouched = false
        notifyDataSetChanged()
    }

    fun setData(dataList: ArrayList<CategoryWiseProductModel>?, isLast: Boolean = true) {
        this.isBottomTouched = isLast
        this.dataList = dataList
        notifyDataSetChanged()
    }

    fun addData(dataList: ArrayList<CategoryWiseProductModel>?, isLast: Boolean = true) {
        this.isBottomTouched = isLast

        if(this.dataList == null) this.dataList = ArrayList()
        dataList?.toList()?.let { this.dataList?.addAll(it) }

        notifyItemRangeInserted(this.dataList?.size ?: 0, dataList?.size ?: 0)
    }

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        when(viewType) {
            1 -> ProductHeadingHolder(ViewProductHeadingBinding.inflate(inflater, parent, false))
            else -> LoadingViewHolder(ViewListLoadingBinding.inflate(inflater, parent, false))
        }
    }

    override fun getItemCount() = dataList?.let { it.size + if(isBottomTouched) 0 else 1 } ?: 0

    override fun getItemViewType(position: Int) = if(position < dataList?.size ?: 0) 1 else 0

    inner class ProductHeadingHolder(val viewDataBinding : ViewProductHeadingBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            dataList?.get(adapterPosition)?.let {
                val context = viewDataBinding.root.context
                val adapter = SubCategoryProductAdapter()
                adapter.setData(it.subCategories)

                viewDataBinding.title = it.categoryName
                viewDataBinding.recyclerView.layoutManager = LinearLayoutManager(context)
                viewDataBinding.recyclerView.adapter = adapter
            }
        }
    }

    inner class LoadingViewHolder(viewDataBinding: ViewListLoadingBinding): BaseViewHolder(viewDataBinding) {
        override fun bindData() {}
    }
}