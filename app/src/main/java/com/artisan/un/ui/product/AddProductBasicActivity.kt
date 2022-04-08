package com.artisan.un.ui.product

import android.util.Log
import androidx.core.widget.doOnTextChanged
import com.artisan.un.R
import com.artisan.un.apiModel.ProductBundle
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityAddProductBasicBinding
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.product.viewModel.ProductAddBasicViewModel
import com.artisan.un.utils.*

class AddProductBasicActivity : BaseActivity<ActivityAddProductBasicBinding, ProductAddBasicViewModel>(R.layout.activity_add_product_basic, ProductAddBasicViewModel::class), ProductListener, AppBarListener {
    private var bundle: ProductBundle? = null
    private var isToEdit: Boolean = false
    private var isDataInitialized = false

    override fun onCreate() {
        bundle = intent.getSerializableExtra(DRAFT_PRODUCT) as? ProductBundle
        isToEdit = intent.getBooleanExtra(EDIT_PRODUCT, false)

        ApplicationData.productBundle = bundle ?: ProductBundle()

        viewDataBinding.listener = this
        viewDataBinding.appBarListener = this
        viewDataBinding.model = mViewModel
        viewDataBinding.bundle = ApplicationData.productBundle

        observeData()
        initListeners()
        initDefaultData()
    }

    override fun onDestroy() {
        super.onDestroy()
        ApplicationData.productBundle = null
    }

    private fun observeData() {
        mViewModel.requestStatus.observe(this) {
            super.onBackPressed()
        }
    }

    private fun initDefaultData() {
        ApplicationData.productBundle?.productId?.let {
            mViewModel.categoriesArray.observe(this) {
                ApplicationData.productBundle?.categoryId?.let { id ->
                    val category = mViewModel.categoriesData.value?.find { it.id?.toInt() == id }
                    viewDataBinding.actionCategories.setText(category?.name)
                }
            }

            mViewModel.categoriesSubArray.observe(this) {
                ApplicationData.productBundle?.subCategoryId?.let { id ->
                    val subCategory =
                        mViewModel.categoriesSubData.value?.find { it.id?.toInt() == id }
                    viewDataBinding.actionCategoriesSub.setText(subCategory?.name)
                }
            }

            mViewModel.materialArray.observe(this) {
                ApplicationData.productBundle?.materialId?.let { id ->
                    val material = mViewModel.materialData.value?.find { it.id?.toInt() == id }
                    viewDataBinding.actionMaterial.setText(material?.name)
                }
            }

            mViewModel.templateArray.observe(this) {
                ApplicationData.productBundle?.templateId?.let { id ->
                    val template = mViewModel.templateData.value?.find { it.id?.toInt() == id }
                    viewDataBinding.actionName.setText(template?.name)
                }
            }
        }
    }

    override fun onContinueClick() {
        Log.d("normal_price", ""+ApplicationData.productBundle?.normal_price)

        if (validateProductInfo())
            navigateTo(AddProductSpecsActivity::class.java, arrayListOf(
                Pair(EDIT_PRODUCT, isToEdit)
            ))
        else
            viewDataBinding.root.showMessage(getString(R.string.fill_all_the_fields))
    }

    private fun initListeners() {
        viewDataBinding.actionCategories.doOnTextChanged { text, _, _, _ ->
            text?.let { search ->
                val category = mViewModel.categoriesData.value?.find { it.name.equals(search.toString(), true) }

                category?.let {
                    ApplicationData.productBundle?.categoryId = it.id?.toInt()
                    mViewModel.categorySub.value = null
                    mViewModel.material.value = null
                    mViewModel.name.value = null

                    if(isDataInitialized) {
                        ApplicationData.productBundle?.subCategoryId = null
                        ApplicationData.productBundle?.materialId = null
                        ApplicationData.productBundle?.templateId = null
                    }

                    mViewModel.getCategoriesSub(it.id)
                }
            }
        }

        viewDataBinding.actionCategoriesSub.doOnTextChanged { text, _, _, _ ->
            text?.let { search ->
                val category = mViewModel.categoriesSubData.value?.find {
                    it.name.equals(search.toString(), true)
                }
                category?.let {
                    ApplicationData.productBundle?.subCategoryId = it.id?.toInt()
                    mViewModel.material.value = null
                    mViewModel.name.value = null

                    if(isDataInitialized) {
                        ApplicationData.productBundle?.materialId = null
                        ApplicationData.productBundle?.templateId = null
                    }

                    mViewModel.getMaterials(it.id)
                }
            }
        }

        viewDataBinding.actionMaterial.doOnTextChanged { text, _, _, _ ->
            text?.let { search ->
                val material = mViewModel.materialData.value?.find { it.name.equals(search.toString(), true) }

                material?.let {
                    mViewModel.name.value = null

                    ApplicationData.productBundle?.run {
                        materialId = it.id?.toInt()

                        if(isDataInitialized) {
                            ApplicationData.productBundle?.templateId = null
                        }

                        mViewModel.getTemplates(categoryId, subCategoryId, materialId)
                    }
                }
            }
        }

        viewDataBinding.actionName.doOnTextChanged { text, _, _, _ ->
            text?.let { search ->
                val template = mViewModel.templateData.value?.find { it.name.equals(search.toString(), true) }
                template?.let {
                    ApplicationData.productBundle?.apply {
                        templateId = it.id?.toInt()
                        length = it.length
                        width = it.width
                        height = it.height
                        weight = it.weight
                        volume = it.volume

                        isDataInitialized = true
                        if(productId == null) {
                            descriptionEn = it.description_en
                            descriptionKn = it.description_kn
                        }
                    }
                }
            }
        }
    }

    override fun onBackClick() = onBackPressed()

    override fun onBackPressed() {
        if(!isToEdit && validateProductInfo()) {
            CustomAlertDialog(
                context = this,
                message = getString(R.string.save_progress_as_draft),
                primaryKey = getString(R.string.save_as_draft),
                secondaryKey = getString(R.string.discart_draft),
                secondaryKeyAction = { super.onBackPressed() },
                primaryKeyAction = {
                    mViewModel.draftProduct(ApplicationData.productBundle?.productId)
                }
            ).show()
        } else super.onBackPressed()
    }

    private fun validateProductInfo(): Boolean = run {
        ApplicationData.productBundle?.run {
            categoryId != null &&
                    subCategoryId != null &&
                    materialId != null &&
                    templateId != null &&
                    !price.isNullOrEmpty() &&
                    !normal_price.isNullOrEmpty() &&
                    !quantity.isNullOrEmpty() &&
                    !localNameEn.isNullOrEmpty() &&
                    !localNameKn.isNullOrEmpty()
        } ?: false
    }
}