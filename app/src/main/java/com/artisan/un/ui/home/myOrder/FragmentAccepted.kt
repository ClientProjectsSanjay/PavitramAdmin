package com.artisan.un.ui.home.myOrder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.artisan.un.databinding.ViewRecyclerviewBinding
import com.artisan.un.ui.userauth.ActivityLogin
import com.artisan.un.utils.navigateTo

class FragmentAccepted : Fragment() {
    private lateinit var mDataBinding: ViewRecyclerviewBinding
    private val mRecyclerViewAdapter= OrderAcceptedRecyclerViewAdapter(::onClick)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mDataBinding = ViewRecyclerviewBinding.inflate(inflater, container, false)
        mDataBinding.recyclerView.adapter = mRecyclerViewAdapter
        return mDataBinding.root
    }

    private fun onClick(position: Int) {
        requireActivity().navigateTo(OrderDetailActivity::class.java)
    }
}