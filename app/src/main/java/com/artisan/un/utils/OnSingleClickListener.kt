package com.artisan.un.utils

import android.view.View
import android.os.SystemClock

abstract class OnSingleClickListener : View.OnClickListener {
    private var mPreClickTime = 0L
    override fun onClick(v: View) {
        doClick(v)
    }
    @Synchronized
    private fun doClick(v: View) {
        val current = SystemClock.elapsedRealtime()
        if (current - mPreClickTime > GAP) {
            mPreClickTime = current
            onSingleClick(v)
        }
    }
    abstract fun onSingleClick(v: View)

    companion object {
        private const val GAP = 700L
    }
}