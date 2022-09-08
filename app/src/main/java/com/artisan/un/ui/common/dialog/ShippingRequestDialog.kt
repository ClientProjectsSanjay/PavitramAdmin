package com.artisan.un.ui.common.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import com.artisan.un.R
import com.artisan.un.databinding.DialogShippingRequestBinding
import com.artisan.un.utils.pxRespectToDeviceWidth

class ShippingRequestDialog(context: Context, val onSubmitClick: (weight: String, length: String, height: String, breadth: String) -> Unit) : Dialog(context) {
    private var mDataBinding: DialogShippingRequestBinding

    init {
        mDataBinding = DialogShippingRequestBinding.inflate(LayoutInflater.from(context))

        setContentView(mDataBinding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(pxRespectToDeviceWidth(context, 1.2F).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)

        setCanceledOnTouchOutside(true)
        setCancelable(true)
        initListener()
    }

    private fun initListener() {
        mDataBinding.submitBtn.setOnClickListener {
            val weight = mDataBinding.inputWeight.text.toString().trim()
            val length = mDataBinding.inputLength.text.toString().trim()
            val height = mDataBinding.inputHeight.text.toString().trim()
            val breadth = mDataBinding.inputBreadth.text.toString().trim()

            if(weight.isNotEmpty() && length.isNotEmpty() && height.isNotEmpty() && breadth.isNotEmpty()) {
                dismiss()
                onSubmitClick(weight, length, height, breadth)
            } else {
                if(weight.isEmpty()) Toast.makeText(context, context.getString(R.string.enter_shipment_weight), Toast.LENGTH_SHORT).show()
                else if(length.isEmpty()) Toast.makeText(context, context.getString(R.string.enter_shipment_length), Toast.LENGTH_SHORT).show()
                else if(height.isEmpty()) Toast.makeText(context, context.getString(R.string.enter_shipment_height), Toast.LENGTH_SHORT).show()
                else if(breadth.isEmpty()) Toast.makeText(context, context.getString(R.string.enter_shipment_breadth), Toast.LENGTH_SHORT).show()
            }
        }

        mDataBinding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }
}