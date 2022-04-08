package com.artisan.un.ui.product

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.artisan.un.R
import com.artisan.un.apiModel.ProductBundle
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityProductDescriptionBinding
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.product.adapter.PreviewProductImageAdapter
import com.artisan.un.ui.product.viewModel.ProductionDescriptionViewModel
import com.artisan.un.utils.*
import com.artisan.un.utils.recyclerViewDecoration.AllSideDecoration

class ProductDescriptionActivity : BaseActivity<ActivityProductDescriptionBinding, ProductionDescriptionViewModel>(R.layout.activity_product_description, ProductionDescriptionViewModel::class), ProductDescriptionListener, AppBarListener {
    private val adapter = PreviewProductImageAdapter()

    override fun onCreate() {
        viewDataBinding.listener = this
        viewDataBinding.appBarListener = this

        val layoutManager = GridLayoutManager(this, 4)
        viewDataBinding.recyclerView.layoutManager = layoutManager
        viewDataBinding.recyclerView.adapter = adapter

        if(viewDataBinding.recyclerView.itemDecorationCount == 0)
            viewDataBinding.recyclerView.addItemDecoration(AllSideDecoration())

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = if (position == 0) 4 else 1
        }

        observeData()
        viewDataBinding.isLoading = true
        mViewModel.requestProductDetails(intent.extras?.getInt(PRODUCT_ID))
    }

    private fun observeData() {
        mViewModel.productDetails.observe(this) { product ->
            val productImages = ArrayList<String>()
            product.productdetail?.image_1?.let { productImages.add(it) }
            product.productdetail?.image_2?.let { productImages.add(it) }
            product.productdetail?.image_3?.let { productImages.add(it) }
            product.productdetail?.image_4?.let { productImages.add(it) }
            product.productdetail?.image_5?.let { productImages.add(it) }
            adapter.setData(productImages, product.productdetail?.video_url)
            viewDataBinding.message = null
            viewDataBinding.isLoading = false
            viewDataBinding.product = product.productdetail
        }

        mViewModel.requestStatus.observe(this) {
            CustomAlertDialog(
                context = this,
                message = getString(R.string.product_deleted_successfully),
                primaryKey = getString(R.string.ok),
                primaryKeyAction = {
                    ApplicationData.productDeleted = true
                    onBackPressed()
                }
            ).show()
        }

        mViewModel.errorMessage.observe(this) {
            viewDataBinding.product = null
            viewDataBinding.isLoading = false
            viewDataBinding.message = it.message
        }
    }

    override fun onBackClick() {
        onBackPressed()
    }

    override fun onEditClick() {
        navigateTo(AddProductBasicActivity::class.java, arrayListOf(
            Pair(DRAFT_PRODUCT, ProductBundle().parseData(viewDataBinding.product)),
            Pair(EDIT_PRODUCT, true)
        ))
    }

    override fun onDeleteClick() {
        CustomAlertDialog(
            context = this,
            message = getString(R.string.delete_product_message),
            primaryKey = getString(R.string.delete),
            secondaryKey = getString(R.string.cancel),
            primaryKeyAction = {
                mViewModel.deleteProduct(viewDataBinding.product?.id)
            }
        ).show()
    }
}