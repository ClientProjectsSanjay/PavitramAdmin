package com.artisan.un.ui.common.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import com.artisan.un.databinding.DialogUpdateQuantityBinding
import com.artisan.un.utils.pxRespectToDeviceWidth

class UpdateQuantityDialog(context: Context, private val listener: () -> Unit = {}) {
    private var dialog: Dialog
    private var viewDataBinding: DialogUpdateQuantityBinding

    init {
        val layoutInflater = LayoutInflater.from(context)
        viewDataBinding = DialogUpdateQuantityBinding.inflate(layoutInflater)

        dialog = Dialog(context)
        dialog.setContentView(viewDataBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            pxRespectToDeviceWidth(context, 1.2F).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        viewDataBinding.cancelBtn.setOnClickListener { dialog.dismiss() }

        viewDataBinding.updateBtn.setOnClickListener {
            dialog.dismiss()
            listener()
        }
    }

    fun show() { if (!dialog.isShowing) dialog.show(); }
}