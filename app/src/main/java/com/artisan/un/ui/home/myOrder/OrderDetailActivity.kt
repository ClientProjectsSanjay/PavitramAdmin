package com.artisan.un.ui.home.myOrder

import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityOrderDetailsBinding
import com.artisan.un.ui.home.myOrder.viewmodel.OrderDetailsViewModel
import com.artisan.un.utils.ORDER_ID
import com.artisan.un.ui.home.myOrder.adapter.OrderDetailsPurchasedItemAdapter

class OrderDetailActivity : BaseActivity<ActivityOrderDetailsBinding, OrderDetailsViewModel>(R.layout.activity_order_details, OrderDetailsViewModel::class) {
    private val mOrderDetailsPurchasedItemAdapter = OrderDetailsPurchasedItemAdapter()

    override fun onCreate() {
        viewDataBinding.recyclerView.adapter = mOrderDetailsPurchasedItemAdapter
        initObservers()
        initListeners()

        mViewModel.getSellerOrderDetails(intent.getIntExtra(ORDER_ID, 0))
    }

    private fun initObservers() {
        mViewModel.mOrderDetailsObservable.observe(this) {
            viewDataBinding.data = it
            //mOrderDetailsPurchasedItemAdapter.addData(it.order_details.get_ordre_product)
        }
    }

    private fun initListeners() {
        viewDataBinding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }
}