package com.artisan.un.baseClasses

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.artisan.un.R
import com.artisan.un.apiModel.ErrorPriority
import com.artisan.un.helpers.PreferencesHelper
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.common.dialog.ProgressBarDialog
import com.artisan.un.ui.userauth.ActivityLogin
import com.artisan.un.utils.ApplicationData
import com.artisan.un.utils.locale.LocaleHelper
import com.artisan.un.utils.navigateTo
import com.artisan.un.utils.showMessage
import com.artisan.un.utils.updateFCM
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.getViewModel
import kotlin.reflect.KClass

abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel>(private val resourceId: Int, private val aClass: KClass<V>): AppCompatActivity() {
    protected lateinit var progressBar: ProgressBarDialog
    protected val mPref: PreferencesHelper = get()
    protected lateinit var viewDataBinding: T
    protected lateinit var mViewModel: V

    abstract fun onCreate()

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBar = ProgressBarDialog.createDialog(this)

        mViewModel = getViewModel(aClass)
        viewDataBinding = DataBindingUtil.setContentView(this, resourceId)
        viewDataBinding.lifecycleOwner = this
        observerData()
        onCreate()
    }

    private fun observerData() {
        mViewModel.errorMessage.observe(this) {
            it?.let {
                it.message?.let { message ->
                    if (it.priority == ErrorPriority.MEDIUM)
                        viewDataBinding.root.showMessage(message)
                }
            }
        }

        mViewModel.noInternet.observe(this) {
            CustomAlertDialog(
                context = this,
                isCancelable = false,
                message = getString(R.string.no_network_connection),
                primaryKey = getString(R.string.retry),
                primaryKeyAction = { onCreate() }
            ).show()
        }

        mViewModel.userBlocked.observe(this) {
            it?.let {
                CustomAlertDialog(
                    context = this,
                    isCancelable = false,
                    message = it.message,
                    primaryKey = getString(R.string.ok),
                    primaryKeyAction = {
                        mPref.clear()
                        ApplicationData.user = null
                        updateFCM(mPref)

                        if (this !is ActivityLogin) {
                            navigateTo(ActivityLogin::class.java)
                            finishAffinity()
                        }
                    }
                ).show()
            }
        }

        mViewModel.progress.observe(this) {
            if (it) progressBar.show() else progressBar.dismiss()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.updateResources(newBase, mPref.appLanguage))
    }
}