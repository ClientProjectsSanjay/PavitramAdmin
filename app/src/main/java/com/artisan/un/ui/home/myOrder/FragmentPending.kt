package com.artisan.un.ui.home.myOrder

import com.artisan.un.R
import com.artisan.un.baseClasses.BaseFragment
import com.artisan.un.databinding.ViewRecyclerviewBinding
import com.artisan.un.ui.home.myOrder.viewmodel.OrderListViewModel
import com.artisan.un.utils.ApplicationData

class FragmentPending : BaseFragment<ViewRecyclerviewBinding, OrderListViewModel>(R.layout.view_recyclerview, OrderListViewModel::class) {
    private val mOrderPendingRecyclerViewAdapter = OrderPendingRecyclerViewAdapter()

    override fun onCreateView() {
        viewDataBinding.recyclerView.adapter = mOrderPendingRecyclerViewAdapter

        initObserver()
        initListener()

        mViewModel.getSellerOrderList(ApplicationData.user?.user?.id?.toInt() ?: 0)
    }

    private fun initObserver() {
        mViewModel.mOrderListObservable.observe(requireActivity()) {
            mOrderPendingRecyclerViewAdapter.addData(it.order_list)
        }
    }

    private fun initListener() {

    }
}