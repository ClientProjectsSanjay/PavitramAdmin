package com.artisan.un.ui.home

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.baseClasses.CommonViewModel
import com.artisan.un.databinding.ActivityNotificationBinding
import com.artisan.un.ui.home.adapter.NotificationAdapter
import com.artisan.un.utils.AppBarListener

class NotificationActivity : BaseActivity<ActivityNotificationBinding, CommonViewModel>(R.layout.activity_notification,CommonViewModel::class), AppBarListener {
    private val notificationAdapter = NotificationAdapter()
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var totalAvailablePages: Int? = null
    private var isInProgress: Boolean = false
    private var currentPage: Int = 1

    override fun onCreate() {
        linearLayoutManager = LinearLayoutManager(this)
        viewDataBinding.viewModel = mViewModel
        viewDataBinding.appBarListener = this
        viewDataBinding.recyclerView.layoutManager = linearLayoutManager
        viewDataBinding.recyclerView.adapter = notificationAdapter
        viewDataBinding.recyclerView.addOnScrollListener(ScrollListener())

        observeData()
        initListener()
        requestData(currentPage)
        viewDataBinding.isLoading = true
    }

    private fun initListener() {
        viewDataBinding.swipeRefresh.setOnRefreshListener {
            currentPage=1
            requestData(currentPage)
        }
    }

    private fun requestData(page: Int) {
        isInProgress = true
        viewDataBinding.message = null
        mViewModel.requestNotifications(page)
    }

    private fun observeData() {
        mViewModel.notification.observe(this, {
            viewDataBinding.message = null
            viewDataBinding.isLoading = false
            viewDataBinding.swipeRefresh.isRefreshing = false

            it?.run {
                isInProgress = false
                totalAvailablePages = pagination?.last_page
                if(currentPage == 1) notificationAdapter.setData(getnotification, currentPage == totalAvailablePages)
                else notificationAdapter.addData(getnotification, currentPage == totalAvailablePages)
            }
        })

        mViewModel.errorMessage.observe(this, {
            viewDataBinding.isLoading = false
            viewDataBinding.message = it.message
            viewDataBinding.swipeRefresh.isRefreshing = false
        })
    }

    override fun onBackClick() = onBackPressed()

    inner class ScrollListener: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if(linearLayoutManager.findLastVisibleItemPosition() == (notificationAdapter.itemCount - 1) && !isInProgress && !notificationAdapter.isBottomTouched)
                requestData(++currentPage)
        }
    }
}