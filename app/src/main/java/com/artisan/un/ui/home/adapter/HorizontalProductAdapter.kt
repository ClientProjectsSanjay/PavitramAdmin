package com.artisan.un.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.artisan.un.R
import com.artisan.un.apiModel.ProductData
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ViewProductListItemBinding
import com.artisan.un.ui.common.dialog.UpdateQuantityDialog
import com.artisan.un.ui.home.viewModel.HomeViewModel
import com.artisan.un.ui.product.ProductDescriptionActivity
import com.artisan.un.utils.PRODUCT_ID
import com.artisan.un.utils.navigateTo
import com.artisan.un.utils.pxRespectToDeviceWidth

class HorizontalProductAdapter(val viewModel: HomeViewModel) : BaseRecyclerViewAdapter() {
    private var dataList: ArrayList<ProductData>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(dataList: ArrayList<ProductData>?) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        ProductListItemHolder(ViewProductListItemBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = dataList?.size ?: 0

    inner class ProductListItemHolder(val viewDataBinding : ViewProductListItemBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            dataList?.get(adapterPosition)?.let { product ->
                val context = viewDataBinding.root.context

                val params = viewDataBinding.parentView.layoutParams
                params.width = pxRespectToDeviceWidth(viewDataBinding.root.context, 2.5F).toInt()

                viewDataBinding.image = product.image_1
                viewDataBinding.productCode = "${viewDataBinding.root.context.getString(R.string.pdx_id)} ${String.format("%06d", product.id)}"
                viewDataBinding.title = "${product.template?.name} (${product.name})"
                viewDataBinding.price = product.price.toString()
                viewDataBinding.isActive = product.is_active == 1
                viewDataBinding.availableCount = product.qty ?: "0"

                viewDataBinding.root.setOnClickListener {
                    context.navigateTo(ProductDescriptionActivity::class.java, arrayListOf(
                        Pair(PRODUCT_ID, product.id)
                    ))
                }

                viewDataBinding.updateCountBtnContainer.setOnClickListener {
                    UpdateQuantityDialog(
                        context = it.context,
                        quantityValue = product.qty?.toInt(),
                        listener = { quantity ->
                            viewModel.updateProductQuantity(product.id ?: 0, quantity)
                        },
                    ).show()
                }
            }
        }
    }
}
