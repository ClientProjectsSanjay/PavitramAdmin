package com.artisan.un.ui.common.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.artisan.un.R
import com.artisan.un.databinding.ViewAlertDialogBinding
import com.artisan.un.utils.AlertDialogListener
import com.artisan.un.utils.pxRespectToDeviceWidth

class CustomAlertDialog(
    context: Context,
    title: String? = null,
    message: String? = null,
    primaryKey: String? = null,
    secondaryKey: String? = null,
    isCancelable: Boolean = true,
    private val primaryKeyAction: () -> Unit = {},
    private val secondaryKeyAction: () -> Unit = {}
) : AlertDialogListener {
    private var dialog: Dialog

    init {
        val layoutInflater = LayoutInflater.from(context)
        val viewDataBinding = DataBindingUtil.inflate<ViewAlertDialogBinding>(layoutInflater, R.layout.view_alert_dialog, null, false)
        viewDataBinding.title = title
        viewDataBinding.message = message
        viewDataBinding.primary = primaryKey
        viewDataBinding.secondary = secondaryKey
        viewDataBinding.listener = this

        dialog = Dialog(context)
        dialog.setContentView(viewDataBinding.root)
        dialog.setCanceledOnTouchOutside(isCancelable)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(pxRespectToDeviceWidth(context, 1.2F).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    fun show() { if(!dialog.isShowing) dialog.show(); }
    override fun onPrimaryKeyClick() { dialog.dismiss(); primaryKeyAction() }
    override fun onSecondaryKeyClick() { dialog.dismiss(); secondaryKeyAction() }
}