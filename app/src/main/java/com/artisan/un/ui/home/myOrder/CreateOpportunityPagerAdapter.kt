package com.artisan.un.ui.home.myOrder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class CreateOpportunityPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment = run {
        when(position) {
            0 -> FragmentPending()
            1 -> FragmentDelivered()
            2 -> FragmentShipped()
            3 -> FragmentPicked()
            else -> Fragment()
        }
    }
}