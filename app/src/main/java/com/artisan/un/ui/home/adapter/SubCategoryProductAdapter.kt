package com.artisan.un.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artisan.un.apiModel.SubCategoryWiseProductListModel
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ViewProductListingBinding
import com.artisan.un.ui.home.viewModel.HomeViewModel
import com.artisan.un.ui.product.SubCategoryActivity
import com.artisan.un.utils.PAGE_TITLE
import com.artisan.un.utils.SUB_CATEGORY_ID
import com.artisan.un.utils.navigateTo
import com.artisan.un.utils.recyclerViewDecoration.AllSideDecoration

class SubCategoryProductAdapter(val viewModel: HomeViewModel) : BaseRecyclerViewAdapter() {
    private var dataListModel: ArrayList<SubCategoryWiseProductListModel>? = null

    fun setData(dataListModel: ArrayList<SubCategoryWiseProductListModel>?) {
        this.dataListModel = dataListModel
        notifyDataSetChanged()
    }

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        ProductListItemHolder(ViewProductListingBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = dataListModel?.size ?: 0

    inner class ProductListItemHolder(val viewDataBinding : ViewProductListingBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            dataListModel?.get(adapterPosition)?.let {
                val context = viewDataBinding.root.context
                val adapter = HorizontalProductAdapter(viewModel)
                adapter.setData(it.products)

                viewDataBinding.title = it.subCategoryName
                viewDataBinding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                viewDataBinding.recyclerView.adapter = adapter
                if(viewDataBinding.recyclerView.itemDecorationCount == 0)
                    viewDataBinding.recyclerView.addItemDecoration(AllSideDecoration())

                viewDataBinding.tvViewAll.setOnClickListener { _ ->
                    context.navigateTo(SubCategoryActivity::class.java, arrayListOf(
                        Pair(PAGE_TITLE, it.subCategoryName),
                        Pair(SUB_CATEGORY_ID, it.subCategoryId)
                    ))
                }
            }
        }
    }
}
