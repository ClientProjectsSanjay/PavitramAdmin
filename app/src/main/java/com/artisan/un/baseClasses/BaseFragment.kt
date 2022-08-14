package com.artisan.un.baseClasses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.artisan.un.R
import com.artisan.un.apiModel.ErrorPriority
import com.artisan.un.helpers.PreferencesHelper
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.common.dialog.ProgressBarDialog
import com.artisan.un.ui.userauth.ActivityLogin
import com.artisan.un.utils.ApplicationData
import com.artisan.un.utils.navigateTo
import com.artisan.un.utils.showMessage
import com.artisan.un.utils.updateFCM
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.getViewModel
import kotlin.reflect.KClass

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel>(private val resourceId: Int, private val aClass: KClass<V>) : Fragment() {
    protected lateinit var progressBar: ProgressBarDialog
    protected val mPref: PreferencesHelper = get()
    protected lateinit var viewDataBinding: T
    protected lateinit var mViewModel: V
    abstract fun onCreateView()

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = run {
        progressBar = ProgressBarDialog.createDialog(requireContext())

        viewDataBinding = DataBindingUtil.inflate(inflater, resourceId, container, false)
        viewDataBinding.lifecycleOwner = this
        mViewModel = getViewModel(aClass)
        onCreateView()
        observerData()

        viewDataBinding.root

    }

    private fun observerData() {
        mViewModel.errorMessage.observe(requireActivity()) {
            if (isAdded) {
                it.message?.let { message ->
                    if (it.priority == ErrorPriority.MEDIUM) viewDataBinding.root.showMessage(
                        message
                    )
                }
            }
        }

        mViewModel.noInternet.observe(requireActivity()) {
            if (isAdded) {
                CustomAlertDialog(
                    context = requireContext(),
                    isCancelable = false,
                    message = getString(R.string.no_network_connection),
                    primaryKey = getString(R.string.retry),
                    primaryKeyAction = { onCreateView() }
                ).show()
            }
        }

        mViewModel.progress.observe(requireActivity()) {
            if (isAdded) {
                if (it) progressBar.show() else progressBar.dismiss()
            }
        }

        mViewModel.userBlocked.observe(requireActivity()) {
            it?.let {
                CustomAlertDialog(
                    context = requireContext(),
                    isCancelable = false,
                    message = it.message,
                    primaryKey = getString(R.string.ok),
                    primaryKeyAction = {
                        mPref.clear()

                        ApplicationData.user = null
                        updateFCM(mPref)

                        requireActivity().navigateTo(ActivityLogin::class.java)
                        finishAffinity(requireActivity())
                    }
                ).show()
            }
        }
    }
}