package com.artisan.un.ui.home.myOrder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.artisan.un.R
import com.artisan.un.databinding.MyOrderHolderBinding
import com.google.android.material.tabs.TabLayoutMediator

class ActivityMyOrder : Fragment() {
    private lateinit var mCreateOpportunityPagerAdapter: CreateOpportunityPagerAdapter
    private lateinit var mDataBinding: MyOrderHolderBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mDataBinding = MyOrderHolderBinding.inflate(inflater, container, false)
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCreateOpportunityPagerAdapter = CreateOpportunityPagerAdapter(childFragmentManager, lifecycle)
        mDataBinding.viewPager.adapter = mCreateOpportunityPagerAdapter
        TabLayoutMediator(mDataBinding.tabLayout, mDataBinding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.pending)
                1 -> getString(R.string.delivered)
                2 -> getString(R.string.shipped)
                else -> getString(R.string.picked)
            }
        }.attach()
    }
}