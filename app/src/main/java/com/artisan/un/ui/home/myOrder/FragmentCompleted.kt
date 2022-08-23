package com.artisan.un.ui.home.myOrder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseFragment
import com.artisan.un.databinding.ViewRecyclerviewBinding
import com.artisan.un.ui.order.ActivityOrderDetails
import com.artisan.un.ui.order.viewmodel.OrderDetailsViewModel
import com.artisan.un.utils.ORDER_ID

class FragmentCompleted : BaseFragment<ViewRecyclerviewBinding, OrderDetailsViewModel>(R.layout.view_recyclerview, OrderDetailsViewModel::class) {
    var mRecyclerViewAdapter = OrderCompletedRecyclerViewAdapter()

    override fun onCreateView() {
        viewDataBinding.recyclerView.adapter = mRecyclerViewAdapter
    }
}