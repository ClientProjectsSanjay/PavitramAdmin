package com.artisan.un.ui.common.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Patterns
import android.view.LayoutInflater
import android.view.WindowManager
import com.artisan.un.R
import com.artisan.un.databinding.DialogAddMediaBinding
import com.artisan.un.utils.AddMediaListener
import com.artisan.un.utils.pxRespectToDeviceWidth
import com.artisan.un.utils.showMessage

class AddMediaDialog(
    private val context: Context,
    private val cameraAction: () -> Unit = {},
    private val galleryAction: () -> Unit = {},
    private val saveAction: (String) -> Unit = {},
) : AddMediaListener {
    private var dialog: Dialog
    private var viewDataBinding: DialogAddMediaBinding

    init {
        val layoutInflater = LayoutInflater.from(context)
        viewDataBinding = DialogAddMediaBinding.inflate(layoutInflater)
        viewDataBinding.listener = this

        dialog = Dialog(context)
        dialog.setContentView(viewDataBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            pxRespectToDeviceWidth(context, 1.2F).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun show() { if (!dialog.isShowing) dialog.show(); }

    override fun onGalleryClick() { dialog.dismiss(); galleryAction() }

    override fun onCameraClick() { dialog.dismiss(); cameraAction() }

    override fun onSaveClick() {
        val url = viewDataBinding.inputUrl.text
        val error: String? = when {
            url.isNullOrEmpty() -> context.getString(R.string.enter_valid_youtube_url)
            !Patterns.WEB_URL.matcher(url).matches() -> context.getString(R.string.enter_valid_youtube_url)
            else -> null
        }

        error?.let {
            viewDataBinding.root.showMessage(error)
        } ?: run {
            dialog.dismiss()
            saveAction(url.toString())
        }
    }
}