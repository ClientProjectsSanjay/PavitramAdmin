package com.artisan.un.ui.home.myOrder

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseFragment
import com.artisan.un.databinding.ViewRecyclerviewBinding
import com.artisan.un.ui.common.dialog.ShippingRequestDialog
import com.artisan.un.ui.order.viewmodel.OrderDetailsViewModel
import com.artisan.un.utils.ApplicationData

class FragmentPending : BaseFragment<ViewRecyclerviewBinding, OrderDetailsViewModel>(R.layout.view_recyclerview, OrderDetailsViewModel::class) {
    private lateinit var mRecyclerViewAdapter: OrderPendingRecyclerViewAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var totalAvailablePages: Int? = null
    private var isInProgress: Boolean = false
    private var currentPage: Int = 1

    override fun onCreateView() {
        mRecyclerViewAdapter = OrderPendingRecyclerViewAdapter { order_id ->
            ShippingRequestDialog(requireContext()) { weight, length, height, breadth ->
                mViewModel.markPackageShipped(order_id, length.toFloat(), breadth.toFloat(), height.toFloat(), weight.toFloat())
            }.show()
        }

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
        mViewModel.mPendingOrderListObservable.observe(viewLifecycleOwner) {
            viewDataBinding.swipeRefreshLayout.isRefreshing = false
            viewDataBinding.executePendingBindings()

            viewDataBinding.errorMessage = if(currentPage == 1 && (it?.order_list?.isEmpty() != false)) getString(R.string.order_data_not_found)
                                           else null

            it?.run {
                isInProgress = false
                totalAvailablePages = pagination?.last_page

                if (currentPage == 1) mRecyclerViewAdapter.setData(order_list, currentPage == totalAvailablePages)
                else mRecyclerViewAdapter.addData(order_list, currentPage == totalAvailablePages)
            }
        }

        mViewModel.mShippedResponseObservable.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()

            currentPage = 1
            totalAvailablePages = null
            requestData(currentPage)
        }
    }

    private fun requestData(page: Int) {
        isInProgress = true
        mViewModel.requestPendingOrderList(ApplicationData.user?.user?.id?.toInt() ?:0, page)
    }

    inner class ScrollListener: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if(linearLayoutManager.findLastVisibleItemPosition() == (mRecyclerViewAdapter.itemCount - 1) && !isInProgress && !mRecyclerViewAdapter.isBottomTouched)
                requestData(++currentPage)
        }
    }
}