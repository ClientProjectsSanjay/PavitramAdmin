package com.artisan.un.ui.home.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseFragment
import com.artisan.un.baseClasses.CommonViewModel
import com.artisan.un.databinding.FragmentWebviewBinding

class FragmentWebView(private val url: String) : BaseFragment<FragmentWebviewBinding, CommonViewModel>(R.layout.fragment_webview, CommonViewModel::class) {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView() {
        with(viewDataBinding) {
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = MyWebViewClient()
            webView.loadUrl(url)
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            viewDataBinding.endLoading = true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            viewDataBinding.endLoading = true
        }
    }

    fun onBackPressed(callback: (WebView) -> Unit) {
        callback(viewDataBinding.webView)
    }
}