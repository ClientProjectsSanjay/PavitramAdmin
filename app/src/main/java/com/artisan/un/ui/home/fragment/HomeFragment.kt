package com.artisan.un.ui.home.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseFragment
import com.artisan.un.databinding.FragmentHomeBinding
import com.artisan.un.ui.home.adapter.HomePageAdapter
import com.artisan.un.ui.home.viewModel.HomeViewModel
import com.artisan.un.ui.product.ActivitySearchProduct
import com.artisan.un.utils.ApplicationData
import com.artisan.un.utils.HomePageListener
import com.artisan.un.utils.navigateTo

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home, HomeViewModel::class), HomePageListener {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var homeAdapter: HomePageAdapter

    private var totalAvailablePages: Int? = null
    private var isInProgress: Boolean = false
    private var currentPage: Int = 1

    override fun onCreateView() {
        viewDataBinding.listener = this
        homeAdapter = HomePageAdapter(mViewModel)

        viewDataBinding.viewModel = mViewModel
        linearLayoutManager = LinearLayoutManager(this.context)
        viewDataBinding.recyclerView.layoutManager = linearLayoutManager
        viewDataBinding.recyclerView.adapter = homeAdapter
        viewDataBinding.recyclerView.visibility = View.VISIBLE
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
            homeAdapter.setData(null)

            viewDataBinding.message = null
            viewDataBinding.isLoading = true
            ApplicationData.productDeleted = false
        }
    }

    private fun initListener() {
        viewDataBinding.swipeRefresh.setOnRefreshListener {
            currentPage = 1
            requestData(currentPage)
        }
    }

    private fun requestData(page: Int) {
        isInProgress = true
        mViewModel.requestHomeData(page)
    }

    override fun onSearchClick() {
        requireContext().navigateTo(ActivitySearchProduct::class.java)
        requireActivity().overridePendingTransition(0, 0)
    }

    private fun observeData() {
        mViewModel.homeData.observe(requireActivity()) {
            viewDataBinding.swipeRefresh.isRefreshing = false
            viewDataBinding.isLoading = false
            viewDataBinding.message = null

            it?.let { products ->
                products.products?.let {
                    isInProgress = false
                    totalAvailablePages = products.categories?.last_page
                    if (currentPage == 1) homeAdapter.setData(
                        products.products,
                        currentPage == totalAvailablePages
                    )
                    else homeAdapter.addData(products.products, currentPage == totalAvailablePages)
                }
            }
        }

        mViewModel.errorMessage.observe(requireActivity()) {
            viewDataBinding.swipeRefresh.isRefreshing = false
            viewDataBinding.message = it.message
            viewDataBinding.isLoading = false
            homeAdapter.resetData()
        }

        mViewModel.mQuantityUpdateObservable.observe(this) {
            currentPage = 1
            requestData(currentPage)
        }
    }

    inner class ScrollListener: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if((linearLayoutManager.findLastVisibleItemPosition() == (linearLayoutManager.itemCount - 1)) && !isInProgress && !homeAdapter.isBottomTouched)
                requestData(++currentPage)
        }
    }
}