package com.artisan.un.ui.home.myOrder

import com.artisan.un.R
import com.artisan.un.baseClasses.BaseFragment
import com.artisan.un.databinding.ViewRecyclerviewBinding
import com.artisan.un.ui.order.viewmodel.OrderDetailsViewModel

class FragmentAccepted : BaseFragment<ViewRecyclerviewBinding, OrderDetailsViewModel>(R.layout.view_recyclerview, OrderDetailsViewModel::class) {
    var mRecyclerViewAdapter = OrderAcceptedRecyclerViewAdapter()

    override fun onCreateView() {
        viewDataBinding.recyclerView.adapter = mRecyclerViewAdapter
    }
}