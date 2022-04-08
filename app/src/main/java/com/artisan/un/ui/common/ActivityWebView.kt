package com.artisan.un.ui.common

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.baseClasses.CommonViewModel
import com.artisan.un.databinding.ActivityWebviewBinding
import com.artisan.un.utils.AppBarListener
import com.artisan.un.utils.PAGE_TITLE
import com.artisan.un.utils.WEB_URL

class ActivityWebView: BaseActivity<ActivityWebviewBinding, CommonViewModel>(R.layout.activity_webview, CommonViewModel::class), AppBarListener {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate() {
        viewDataBinding.appBarListener = this
        viewDataBinding.title = intent.extras?.getString(PAGE_TITLE)

        with(viewDataBinding) {
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = MyWebViewClient()
            webView.loadUrl(intent.extras?.getString(WEB_URL) ?: "")
        }
    }

    inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            viewDataBinding.endLoading = true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            viewDataBinding.endLoading = true
        }
    }

    override fun onBackClick() = onBackPressed()
}