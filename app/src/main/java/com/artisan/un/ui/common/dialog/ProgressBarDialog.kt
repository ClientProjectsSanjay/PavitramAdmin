package com.artisan.un.ui.common.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.artisan.un.R
import com.artisan.un.databinding.DialogProgressViewBinding

class ProgressBarDialog private constructor(context: Context) {
    private var viewDataBinding: DialogProgressViewBinding? = null
    private var message: String = context.getString(R.string.loading)
    private var dialog = Dialog(context)

    init {
        val layoutInflater = LayoutInflater.from(context)
        viewDataBinding = DialogProgressViewBinding.inflate(layoutInflater, null, false)

        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(viewDataBinding?.root ?: View(context))
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    companion object {
        fun createDialog(context: Context): ProgressBarDialog = run {
            ProgressBarDialog(context)
        }
    }

    fun show() {
        viewDataBinding?.message = message
        dialog.show()
    }

    fun setMessage(message: String) {
        this.message = message
        viewDataBinding?.message = message
        viewDataBinding?.executePendingBindings()
    }

    fun dismiss() = dialog.dismiss()
}