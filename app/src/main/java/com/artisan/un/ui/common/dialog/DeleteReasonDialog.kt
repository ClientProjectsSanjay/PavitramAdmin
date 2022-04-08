package com.artisan.un.ui.common.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.widget.doOnTextChanged
import com.artisan.un.R
import com.artisan.un.databinding.DialogDeleteReasonBinding
import com.artisan.un.utils.pxRespectToDeviceWidth
import com.artisan.un.utils.showMessage

class DeleteReasonDialog(context: Context, onDelete: (String) -> Unit) {
    private var deleteReasonValue: String? = null
    private val dialog = Dialog(context)

    init {
        val viewDataBinding = DialogDeleteReasonBinding.inflate(LayoutInflater.from(context))

        dialog.setContentView(viewDataBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(pxRespectToDeviceWidth(context, 1.1F).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)

        viewDataBinding.run {
            deleteReasons = context.resources.getStringArray(R.array.deleteProfileReasons).toCollection(ArrayList())

            inputDeleteReason.doOnTextChanged { text, _, _, _ ->
                if(!text.isNullOrEmpty()) { deleteReasonValue = text.toString() }
            }

            btnDeleteProfile.setOnClickListener{
                if(!deleteReasonValue.isNullOrEmpty()) {
                    dialog.dismiss()
                    deleteReasonValue?.let { onDelete(it) }
                } else viewDataBinding.root.showMessage(context.getString(R.string.select_your_reason))
            }
        }
    }

    fun show() = dialog.show()
}