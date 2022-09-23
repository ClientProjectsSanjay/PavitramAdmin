package com.artisan.un.ui.order

import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityOrderDetailsBinding
import com.artisan.un.ui.order.adapter.OrderDetailsPurchasedItemAdapter
import com.artisan.un.ui.order.viewmodel.OrderDetailsViewModel
import com.artisan.un.utils.ORDER_ID
import com.artisan.un.utils.downloadFile

class ActivityOrderDetails : BaseActivity<ActivityOrderDetailsBinding, OrderDetailsViewModel>(R.layout.activity_order_details, OrderDetailsViewModel::class) {
    private val mOrderDetailsPurchasedItemAdapter = OrderDetailsPurchasedItemAdapter()

    override fun onCreate() {
        viewDataBinding.recyclerView.adapter = mOrderDetailsPurchasedItemAdapter
        initObservers()
        initListeners()

        mViewModel.getUserOrderDetails(intent.getIntExtra(ORDER_ID, 0))
    }

    private fun initObservers() {
        mViewModel.mOrderDetailsObservable.observe(this) {
            viewDataBinding.data = it
            viewDataBinding.executePendingBindings()
            mOrderDetailsPurchasedItemAdapter.addData(it.order_details.orderData.productItem)
        }
    }

    private fun initListeners() {
        viewDataBinding.backBtn.setOnClickListener {
            onBackPressed()
        }

        viewDataBinding.printBtn.setOnClickListener {
            downloadFile(viewDataBinding.data?.order_details?.orderData?.orderPdfPath)
        }
    }
}