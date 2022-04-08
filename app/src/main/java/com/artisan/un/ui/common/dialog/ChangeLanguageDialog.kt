package com.artisan.un.ui.common.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import com.artisan.un.databinding.DialogChangeLanguageBinding
import com.artisan.un.utils.AppLanguage
import com.artisan.un.utils.LanguageSelectionListener
import com.artisan.un.utils.pxRespectToDeviceWidth

class ChangeLanguageDialog(context: Context, selectedLanguage: AppLanguage, private val onProceed: (AppLanguage) -> Unit) {
    private val viewDataBinding = DialogChangeLanguageBinding.inflate(LayoutInflater.from(context))
    private val dialog = Dialog(context)

    init {
        viewDataBinding.selectedLanguage = selectedLanguage
        viewDataBinding.listener = ActionListener()

        dialog.setContentView(viewDataBinding.root)
        dialog.window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(pxRespectToDeviceWidth(context, 1.2F).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }

    fun show() {
        dialog.show()
    }

    inner class ActionListener : LanguageSelectionListener {
        private lateinit var language: AppLanguage

        override fun selectLanguage(language: AppLanguage) {
            viewDataBinding.selectedLanguage = language
            this.language = language
        }

        override fun onProceedClick() {
            onProceed(language)
        }

        override fun onCancelClick() {
            dialog.dismiss()
        }
    }
}