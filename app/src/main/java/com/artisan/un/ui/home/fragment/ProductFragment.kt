package com.artisan.un.ui.home.fragment

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseFragment
import com.artisan.un.databinding.FragmentRecyclerViewBinding
import com.artisan.un.ui.home.adapter.HomePageAdapter
import com.artisan.un.ui.home.viewModel.HomeViewModel
import com.artisan.un.utils.ApplicationData
import com.artisan.un.utils.Roles
import com.artisan.un.utils.toPx

class ProductFragment : BaseFragment<FragmentRecyclerViewBinding, HomeViewModel>(R.layout.fragment_recycler_view, HomeViewModel::class) {
    private val homeAdapter = HomePageAdapter()
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var totalAvailablePages: Int? = null
    private var isInProgress: Boolean = false
    private var currentPage: Int = 1

    override fun onCreateView() {
        linearLayoutManager = LinearLayoutManager(this.context)

        viewDataBinding.viewModel = mViewModel
        viewDataBinding.recyclerView.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.page_bg_color))
        viewDataBinding.recyclerView.layoutManager = linearLayoutManager
        viewDataBinding.recyclerView.adapter = homeAdapter
        viewDataBinding.recyclerView.addOnScrollListener(ScrollListener())
        viewDataBinding.recyclerView.setPadding(
            8.toPx(requireContext()).toInt(),
            4.toPx(requireContext()).toInt(),
            8.toPx(requireContext()).toInt(),
            120.toPx(requireContext()).toInt(),
        )

        observeData()
        initListener()
        requestData(currentPage)
        viewDataBinding.isLoading = true
    }

    override fun onStart() {
        super.onStart()
        if(ApplicationData.productDeleted) {
            currentPage=1
            requestData(currentPage)
            homeAdapter.setData(null)

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
        mViewModel.requestArtisanProducts(page)
    }

    private fun observeData() {
        mViewModel.artisanProducts.observe(requireActivity()) {
            viewDataBinding.swipeRefresh.isRefreshing = false
            viewDataBinding.isLoading = false
            viewDataBinding.message = null

            it?.run {
                isInProgress = false
                totalAvailablePages = pagination?.last_page
                if (currentPage == 1) homeAdapter.setData(
                    products,
                    currentPage == totalAvailablePages
                )
                else homeAdapter.addData(products, currentPage == totalAvailablePages)
            }
        }

        mViewModel.errorMessage.observe(requireActivity()) {
            viewDataBinding.swipeRefresh.isRefreshing = false
            viewDataBinding.message = it.message
            viewDataBinding.isLoading = false
        }
    }

    inner class ScrollListener: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if(linearLayoutManager.findLastVisibleItemPosition() == (homeAdapter.itemCount - 1) && !isInProgress && !homeAdapter.isBottomTouched)
                requestData(++currentPage)
        }
    }
}