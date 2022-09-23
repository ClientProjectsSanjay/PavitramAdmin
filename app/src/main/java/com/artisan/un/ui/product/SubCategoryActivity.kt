package com.artisan.un.ui.product

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivitySubCategoryBinding
import com.artisan.un.ui.product.adapter.SubCategoryProductListAdapter
import com.artisan.un.ui.product.viewModel.ProductViewModel
import com.artisan.un.utils.*
import com.artisan.un.utils.recyclerViewDecoration.AllSideDecoration

class SubCategoryActivity : BaseActivity<ActivitySubCategoryBinding, ProductViewModel>(R.layout.activity_sub_category, ProductViewModel::class), AppBarListener {
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: SubCategoryProductListAdapter

    private var totalAvailablePages: Int? = null
    private var isInProgress: Boolean = false
    private var currentPage: Int = 1

    override fun onCreate() {
        viewDataBinding.viewModel = mViewModel
        viewDataBinding.appBarListener = this
        viewDataBinding.title = intent.extras?.getString(PAGE_TITLE)

        gridLayoutManager = GridLayoutManager(this, 2)

        adapter = SubCategoryProductListAdapter(mViewModel)
        viewDataBinding.recyclerView.layoutManager = gridLayoutManager
        viewDataBinding.recyclerView.adapter = adapter
        viewDataBinding.recyclerView.addItemDecoration(AllSideDecoration())
        viewDataBinding.recyclerView.addOnScrollListener(ScrollListener())

        observeData()
        initListener()
        requestData(currentPage)
        viewDataBinding.isLoading = true
    }

    override fun onStart() {
        super.onStart()

        if(ApplicationData.productDeleted) {
            currentPage = 1
            requestData(currentPage)
            adapter.setData(null)

            viewDataBinding.message = null
            viewDataBinding.isLoading = true
            ApplicationData.productDeleted = false
        }
    }

    private fun initListener() {
        viewDataBinding.swipeRefresh.setOnRefreshListener {
            currentPage=1
            requestData(currentPage)
        }
    }

    private fun requestData(page: Int) {
        isInProgress = true
        mViewModel.requestSubCategoryProductList(intent.extras?.getInt(SUB_CATEGORY_ID), page)
    }

    private fun observeData() {
        mViewModel.mQuantityUpdateObservable.observe(this) {
            currentPage = 1
            requestData(currentPage)
        }

        mViewModel.products.observe(this) {
            viewDataBinding.swipeRefresh.isRefreshing = false
            viewDataBinding.isLoading = false
            viewDataBinding.message = null

            it.run {
                isInProgress = false
                totalAvailablePages = pagination?.last_page
                if (currentPage == 1) adapter.setData(products, currentPage == totalAvailablePages)
                else adapter.addData(products, currentPage == totalAvailablePages)
            }
        }

        mViewModel.errorMessage.observe(this) {
            viewDataBinding.swipeRefresh.isRefreshing = false
            viewDataBinding.message = it.message
            viewDataBinding.isLoading = false
        }
    }

    override fun onBackClick() = onBackPressed()

    inner class ScrollListener: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if(gridLayoutManager.findLastVisibleItemPosition() == (adapter.itemCount - 1) && !isInProgress && !adapter.isBottomTouched)
                requestData(++currentPage)
        }
    }
}