package com.artisan.un.ui.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.artisan.un.R
import com.artisan.un.apiModel.ProductData
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ViewListLoadingBinding
import com.artisan.un.databinding.ViewProductListItemBinding
import com.artisan.un.ui.product.ProductDescriptionActivity
import com.artisan.un.utils.PRODUCT_ID
import com.artisan.un.utils.navigateTo

class SubCategoryProductListAdapter :  BaseRecyclerViewAdapter() {
    private var dataList: ArrayList<ProductData>? = null
    var isBottomTouched: Boolean = false

    fun setData(dataList: ArrayList<ProductData>?, isLast: Boolean = true) {
        this.isBottomTouched = isLast
        this.dataList = dataList
        notifyDataSetChanged()
    }

    fun addData(dataList: ArrayList<ProductData>?, isLast: Boolean = true) {
        this.isBottomTouched = isLast

        if(this.dataList == null) this.dataList = ArrayList()
        dataList?.toList()?.let { this.dataList?.addAll(it) }

        notifyItemRangeInserted(this.dataList?.size ?: 0, dataList?.size ?: 0)
    }

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        when(viewType) {
            1 -> ProductListItemHolder(ViewProductListItemBinding.inflate(inflater, parent, false))
            else -> LoadingViewHolder(ViewListLoadingBinding.inflate(inflater, parent, false))
        }
    }

    override fun getItemCount() = dataList?.let { it.size + if(isBottomTouched) 0 else 1 } ?: 0

    override fun getItemViewType(position: Int) = if(position < dataList?.size ?: 0) 1 else 0

    inner class ProductListItemHolder(val viewDataBinding: ViewProductListItemBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            dataList?.get(adapterPosition)?.let {
                val context = viewDataBinding.root.context

                viewDataBinding.image = it.image_1
                viewDataBinding.productCode = "${viewDataBinding.root.context.getString(R.string.pdx_id)} ${String.format("%06d", it.id)}"
                viewDataBinding.title = "${it.template?.name} (${it.name})"
                viewDataBinding.price = it.price.toString()
                viewDataBinding.isActive = it.is_active == 1

                viewDataBinding.root.setOnClickListener { _ ->
                    context.navigateTo(ProductDescriptionActivity::class.java, arrayListOf(
                        Pair(PRODUCT_ID, it.id)
                    ))
                }
            }
        }
    }

    inner class LoadingViewHolder(viewDataBinding: ViewListLoadingBinding): BaseViewHolder(viewDataBinding) {
        override fun bindData() {}
    }
}