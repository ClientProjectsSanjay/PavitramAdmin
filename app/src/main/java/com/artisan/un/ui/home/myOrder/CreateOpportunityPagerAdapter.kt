package com.artisan.un.ui.home.myOrder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yuwaah.view.opportunity.FragmentPending
import com.yuwaah.view.opportunity.FragmentCompleted

class CreateOpportunityPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = run {
        when(position) {
            0 -> FragmentPending()
            1 -> FragmentAccepted()
            else -> FragmentCompleted()
        }
    }
}