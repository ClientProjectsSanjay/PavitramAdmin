package com.artisan.un.ui.common.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import com.artisan.un.R
import com.artisan.un.databinding.DialogUpdateQuantityBinding
import com.artisan.un.utils.pxRespectToDeviceWidth

class UpdateQuantityDialog(context: Context, quantityValue: Int?, private val listener: (Int) -> Unit = {}) {
    private var dialog: Dialog
    private var viewDataBinding: DialogUpdateQuantityBinding

    init {
        val layoutInflater = LayoutInflater.from(context)
        viewDataBinding = DialogUpdateQuantityBinding.inflate(layoutInflater)
        viewDataBinding.quantityValue = quantityValue?.toString()

        dialog = Dialog(context)
        dialog.setContentView(viewDataBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            pxRespectToDeviceWidth(context, 1.2F).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        viewDataBinding.cancelBtn.setOnClickListener { dialog.dismiss() }

        viewDataBinding.updateBtn.setOnClickListener {
            val quantity = viewDataBinding.inputQuantity.text.toString().trim().let { if(it.isEmpty()) -1 else it.toInt() }

            if(quantity >= 0) {
                dialog.dismiss()
                listener(quantity)
            } else {
                Toast.makeText(context, context.getString(R.string.enter_a_valid_quantity), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun show() { if (!dialog.isShowing) dialog.show(); }
}