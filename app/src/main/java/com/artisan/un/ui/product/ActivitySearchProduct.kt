package com.artisan.un.ui.product

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivitySearchProductBinding
import com.artisan.un.ui.product.adapter.SubCategoryProductListAdapter
import com.artisan.un.ui.product.viewModel.ProductViewModel
import com.artisan.un.utils.AppBarListener
import com.artisan.un.utils.ApplicationData
import com.artisan.un.utils.hideKeyBoard
import com.artisan.un.utils.recyclerViewDecoration.AllSideDecoration
import java.util.*

class ActivitySearchProduct : BaseActivity<ActivitySearchProductBinding, ProductViewModel>(R.layout.activity_search_product, ProductViewModel::class), AppBarListener {
    private lateinit var timer: Timer
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var productListAdapter: SubCategoryProductListAdapter

    private var totalAvailablePages: Int? = null
    private var isInProgress: Boolean = false
    private var currentPage: Int = 1

    override fun onCreate() {
        viewDataBinding.appBarListener = this

        gridLayoutManager = GridLayoutManager(this, 2)
        productListAdapter = SubCategoryProductListAdapter()

        viewDataBinding.recyclerView.layoutManager = gridLayoutManager
        viewDataBinding.recyclerView.adapter = productListAdapter
        viewDataBinding.recyclerView.addOnScrollListener(ScrollListener())
        if(viewDataBinding.recyclerView.itemDecorationCount == 0)
            viewDataBinding.recyclerView.addItemDecoration(AllSideDecoration())

        observeData()
        handleSearch()
    }

    override fun onStart() {
        super.onStart()
        viewDataBinding.inputSearch.requestFocus()

        if(ApplicationData.productDeleted) {
            handleSearch()
            productListAdapter.setData(null)

            viewDataBinding.message = null
            viewDataBinding.isLoading = true
            ApplicationData.productDeleted = false
        }
    }

    private fun handleSearch() {
        viewDataBinding.inputSearch.doOnTextChanged { text, _, _, _ ->
            if(::timer.isInitialized) timer.cancel()

            viewDataBinding.message = null
            viewDataBinding.isLoading = false
            productListAdapter.setData(null)

            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    currentPage = 1
                    if(!text.isNullOrEmpty() && text.length > 2) {
                        viewDataBinding.isLoading = true
                        requestData(text.toString(), currentPage)
                    }
                }
            }, 200L)
        }
    }

    private fun requestData(value: String, page: Int) {
        isInProgress = true
        mViewModel.searchProduct(value, page)
    }

    private fun observeData() {
        mViewModel.searchProduct.observe(this) {
            viewDataBinding.isLoading = false
            it.products?.let { productPage ->
                if (productPage.data != null && productPage.last_page != null) {
                    viewDataBinding.isLoading = false

                    if (productPage.data?.size ?: 0 == 0) {
                        viewDataBinding.message = getString(R.string.no_data_found)
                        productListAdapter.setData(null)
                    } else {
                        isInProgress = false
                        viewDataBinding.message = null
                        totalAvailablePages = productPage.last_page
                        if (currentPage == 1) productListAdapter.setData(
                            it.products?.data,
                            currentPage == totalAvailablePages
                        )
                        else productListAdapter.addData(
                            it.products?.data,
                            currentPage == totalAvailablePages
                        )
                    }
                }
            } ?: run {
                viewDataBinding.message = getString(R.string.something_went_wrong)
            }
        }
    }

    override fun onBackPressed() {
        hideKeyBoard(viewDataBinding.root)

        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

    override fun onBackClick() = onBackPressed()

    inner class ScrollListener: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if((gridLayoutManager.findLastVisibleItemPosition() == (productListAdapter.itemCount - 1)) && !isInProgress && !productListAdapter.isBottomTouched)
                requestData(viewDataBinding.inputSearch.text.toString(), ++currentPage)
        }
    }
}