package com.artisan.un.ui.home.myOrder

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseFragment
import com.artisan.un.databinding.ViewRecyclerviewBinding
import com.artisan.un.ui.order.viewmodel.OrderDetailsViewModel
import com.artisan.un.utils.ApplicationData

class FragmentDelivered : BaseFragment<ViewRecyclerviewBinding, OrderDetailsViewModel>(R.layout.view_recyclerview, OrderDetailsViewModel::class) {
    private var mRecyclerViewAdapter = OrderDeliveredRecyclerViewAdapter()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var totalAvailablePages: Int? = null
    private var isInProgress: Boolean = false
    private var currentPage: Int = 1

    override fun onCreateView() {
        linearLayoutManager = LinearLayoutManager(requireContext())
        viewDataBinding.recyclerView.layoutManager = linearLayoutManager
        viewDataBinding.recyclerView.adapter = mRecyclerViewAdapter
        viewDataBinding.recyclerView.addOnScrollListener(ScrollListener())

        observeData()
        initListener()
        requestData(currentPage)
    }

    private fun initListener() {
        viewDataBinding.swipeRefreshLayout.setOnRefreshListener {
            currentPage = 1
            totalAvailablePages = null
            requestData(currentPage)
        }
    }

    private fun observeData() {
        mViewModel.mDeliveredOrderListObservable.observe(viewLifecycleOwner) {
            viewDataBinding.swipeRefreshLayout.isRefreshing = false
            viewDataBinding.executePendingBindings()

            viewDataBinding.errorMessage = if(currentPage == 1 && (it?.order_list?.isEmpty() != false)) getString(R.string.order_data_not_found)
                                           else null

            it?.run {
                isInProgress = false
                totalAvailablePages = pagination?.last_page

                if (currentPage == 1) mRecyclerViewAdapter.setData(
                    order_list,
                    currentPage == totalAvailablePages
                )
                else mRecyclerViewAdapter.addData(
                    order_list,
                    currentPage == totalAvailablePages
                )
            }
        }
    }

    private fun requestData(page: Int) {
        isInProgress = true
        mViewModel.requestDeliveredOrderList(ApplicationData.user?.user?.id?.toInt() ?:0, page)
    }

    inner class ScrollListener: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if(linearLayoutManager.findLastVisibleItemPosition() == (mRecyclerViewAdapter.itemCount - 1) && !isInProgress && !mRecyclerViewAdapter.isBottomTouched)
                requestData(++currentPage)
        }
    }
}