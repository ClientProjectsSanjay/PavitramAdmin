package com.yuwaah.view.opportunity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.artisan.un.databinding.ViewRecyclerviewBinding
import com.artisan.un.ui.home.myOrder.OrderDetailActivity
import com.artisan.un.utils.navigateTo
import com.yuwaah.view.opportunity.adapter.OrderCompletedRecyclerViewAdapter

class FragmentCompleted : Fragment() {
    private lateinit var mDataBinding: ViewRecyclerviewBinding
    private val mRecyclerViewAdapter = OrderCompletedRecyclerViewAdapter(::onClick)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mDataBinding = ViewRecyclerviewBinding.inflate(inflater, container, false)
        mDataBinding.recyclerView.adapter = mRecyclerViewAdapter
        return mDataBinding.root
    }

    private fun onClick(position: Int) {
        requireActivity().navigateTo(OrderDetailActivity::class.java)

    }
}