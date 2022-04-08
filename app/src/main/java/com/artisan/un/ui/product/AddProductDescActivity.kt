package com.artisan.un.ui.product

import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityAddProductDescBinding
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.home.MainActivity
import com.artisan.un.ui.product.viewModel.ProductAddDescViewModel
import com.artisan.un.utils.*

class AddProductDescActivity : BaseActivity<ActivityAddProductDescBinding, ProductAddDescViewModel>(R.layout.activity_add_product_desc, ProductAddDescViewModel::class), ProductListener, AppBarListener {
    private var isToEdit: Boolean = false

    override fun onCreate() {
        isToEdit = intent.getBooleanExtra(EDIT_PRODUCT, false)

        viewDataBinding.listener = this
        viewDataBinding.appBarListener = this
        viewDataBinding.isToEditProduct = isToEdit
        viewDataBinding.bundle = ApplicationData.productBundle

        observeData()
    }

    private fun observeData() {
        mViewModel.requestStatus.observe(this) {
            CustomAlertDialog(
                context = this,
                isCancelable = false,
                message = getString(ApplicationData.productBundle?.productId?.let { R.string.product_updated_successfully }
                    ?: R.string.product_added_successfully),
                primaryKey = getString(R.string.ok),
                primaryKeyAction = {
                    navigateTo(MainActivity::class.java)
                    finishAffinity()
                }
            ).show()
        }
    }

    override fun onContinueClick() {
        ApplicationData.productBundle?.apply {
            if (descriptionEn.isNullOrEmpty() || descriptionKn.isNullOrEmpty()) {
                viewDataBinding.root.showMessage(getString(R.string.fill_all_the_fields))
                return
            }

            progressBar.setMessage(getString(productId?.let { R.string.updating_product } ?: R.string.adding_product))
            mViewModel.doSubmitForm(0, productId)
        }
    }

    override fun onBackClick() {
        onBackPressed()
    }

    override fun onEditClick() {
        viewDataBinding.isEditable = true
    }
}