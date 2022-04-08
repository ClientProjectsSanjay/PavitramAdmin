package com.artisan.un.ui.home.myOrder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.artisan.un.R
import com.artisan.un.databinding.OrderDetailViewBinding

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var mDataBinding: OrderDetailViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.order_detail_view)
    }
}