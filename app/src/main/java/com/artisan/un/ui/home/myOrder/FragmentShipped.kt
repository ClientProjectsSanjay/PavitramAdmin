package com.artisan.un.ui.home.myOrder

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseFragment
import com.artisan.un.databinding.ViewRecyclerviewBinding
import com.artisan.un.ui.order.viewmodel.OrderDetailsViewModel
import com.artisan.un.utils.ApplicationData

class FragmentShipped : BaseFragment<ViewRecyclerviewBinding, OrderDetailsViewModel>(R.layout.view_recyclerview, OrderDetailsViewModel::class) {
    private lateinit var mRecyclerViewAdapter: OrderShippedRecyclerViewAdapter
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private var mTotalAvailablePages: Int? = null
    private var mIsInProgress: Boolean = false
    private var mCurrentPage: Int = 1

    override fun onCreateView() {
        mRecyclerViewAdapter = OrderShippedRecyclerViewAdapter { order_id ->
            mViewModel.markPackageHandover(ApplicationData.user?.user?.id?.toInt() ?: 0, order_id)
        }

        mLinearLayoutManager = LinearLayoutManager(requireContext())
        viewDataBinding.recyclerView.layoutManager = mLinearLayoutManager
        viewDataBinding.recyclerView.adapter = mRecyclerViewAdapter
        viewDataBinding.recyclerView.addOnScrollListener(ScrollListener())

        observeData()
        initListener()
        requestData(mCurrentPage)
    }

    private fun initListener() {
        viewDataBinding.swipeRefreshLayout.setOnRefreshListener {
            mCurrentPage = 1
            mTotalAvailablePages = null
            requestData(mCurrentPage)
        }
    }

    private fun observeData() {
        mViewModel.mShippedOrderListObservable.observe(viewLifecycleOwner) {
            viewDataBinding.swipeRefreshLayout.isRefreshing = false
            viewDataBinding.executePendingBindings()

            viewDataBinding.errorMessage = if(mCurrentPage == 1 && (it?.order_list?.isEmpty() != false)) getString(R.string.order_data_not_found)
                                           else null
            it?.run {
                mIsInProgress = false
                mTotalAvailablePages = pagination?.last_page

                if (mCurrentPage == 1) mRecyclerViewAdapter.setData(order_list, mCurrentPage == mTotalAvailablePages)
                else mRecyclerViewAdapter.addData(order_list, mCurrentPage == mTotalAvailablePages)
            }
        }

        mViewModel.mHandoverResponseObservable.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()

            mCurrentPage = 1
            mTotalAvailablePages = null
            requestData(mCurrentPage)
        }
    }

    private fun requestData(page: Int) {
        mIsInProgress = true
        mViewModel.requestShippedOrderList(ApplicationData.user?.user?.id?.toInt() ?:0, page)
    }

    inner class ScrollListener: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if(mLinearLayoutManager.findLastVisibleItemPosition() == (mRecyclerViewAdapter.itemCount - 1) && !mIsInProgress && !mRecyclerViewAdapter.isBottomTouched)
                requestData(++mCurrentPage)
        }
    }
}