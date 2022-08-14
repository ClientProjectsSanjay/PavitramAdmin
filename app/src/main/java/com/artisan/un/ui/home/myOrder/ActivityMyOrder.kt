package com.artisan.un.ui.home.myOrder

import com.artisan.un.R
import com.artisan.un.baseClasses.BaseFragment
import com.artisan.un.databinding.MyOrderHolderBinding
import com.artisan.un.ui.home.myOrder.viewmodel.OrderListViewModel
import com.google.android.material.tabs.TabLayoutMediator

class ActivityMyOrder : BaseFragment<MyOrderHolderBinding, OrderListViewModel>(R.layout.my_order_holder, OrderListViewModel::class) {
    private lateinit var mCreateOpportunityPagerAdapter: CreateOpportunityPagerAdapter

    override fun onCreateView() {
        mCreateOpportunityPagerAdapter = CreateOpportunityPagerAdapter(childFragmentManager, lifecycle)
        viewDataBinding.viewPager.adapter = mCreateOpportunityPagerAdapter
        TabLayoutMediator(viewDataBinding.tabLayout, viewDataBinding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pending"
                1 -> "Accepted"
                else -> "Completed"
            }
        }.attach()
    }
}