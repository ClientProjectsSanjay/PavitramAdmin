package com.artisan.un.ui.product

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.recyclerview.widget.GridLayoutManager
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityAddProductSpecsBinding
import com.artisan.un.ui.common.dialog.AddMediaDialog
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.home.MainActivity
import com.artisan.un.ui.product.adapter.AddProductImageAdapter
import com.artisan.un.ui.product.viewModel.ProductAddDescViewModel
import com.artisan.un.utils.*
import com.artisan.un.utils.recyclerViewDecoration.AllSideDecoration
import com.yalantis.ucrop.UCrop
import java.io.File

class AddProductSpecsActivity : BaseActivity<ActivityAddProductSpecsBinding, ProductAddDescViewModel>(R.layout.activity_add_product_specs, ProductAddDescViewModel::class), ProductListener, AppBarListener {
    private lateinit var addProductImageAdapter: AddProductImageAdapter
    private var isToEdit: Boolean = false
    private val galleryRequestCode = 102
    private val cameraRequestCode = 101
    private var imagePosition: Int = -1

    override fun onCreate() {
        isToEdit = intent.getBooleanExtra(EDIT_PRODUCT, false)

        addProductImageAdapter = AddProductImageAdapter(::onClick)

        val sizeArray = resources.getStringArray(R.array.sizeArray)
        val weightArray = resources.getStringArray(R.array.weightArray)
        val volumeArray = resources.getStringArray(R.array.volumeArray)

        viewDataBinding.listener = this
        viewDataBinding.appBarListener = this
        viewDataBinding.isToEditProduct = isToEdit
        viewDataBinding.bundle = ApplicationData.productBundle

        viewDataBinding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        viewDataBinding.recyclerView.adapter = addProductImageAdapter
        if(viewDataBinding.recyclerView.itemDecorationCount == 0)
            viewDataBinding.recyclerView.addItemDecoration(AllSideDecoration())

        viewDataBinding.spinnerWidth.adapter = SpinnerAdapter(this, sizeArray)
        viewDataBinding.spinnerHeight.adapter = SpinnerAdapter(this, sizeArray)
        viewDataBinding.spinnerLength.adapter = SpinnerAdapter(this, sizeArray)
        viewDataBinding.spinnerWeight.adapter = SpinnerAdapter(this, weightArray)
        viewDataBinding.spinnerVolume.adapter = SpinnerAdapter(this, volumeArray)

        viewDataBinding.apply {
            ApplicationData.productBundle?.let {
                inputWidth.visibility = if(it.width?.isOn() == true) View.VISIBLE else View.GONE
                inputHeight.visibility = if(it.height?.isOn() == true) View.VISIBLE else View.GONE
                inputLength.visibility = if(it.length?.isOn() == true) View.VISIBLE else View.GONE
                inputWeight.visibility = if(it.weight?.isOn() == true) View.VISIBLE else View.GONE
                inputVolume.visibility = if(it.volume?.isOn() == true) View.VISIBLE else View.GONE
            }
        }

        observeData()
        initDefaultData()
    }

    private fun observeData() {
        mViewModel.requestStatus.observe(this) {
            CustomAlertDialog(
                context = this,
                isCancelable = false,
                message = getString(R.string.product_added_to_draft_successfully),
                primaryKey = getString(R.string.ok),
                primaryKeyAction = {
                    navigateTo(MainActivity::class.java)
                    finishAffinity()
                }
            ).show()
        }
    }

    private fun initDefaultData() {
        ApplicationData.productBundle?.apply {
            addProductImageAdapter.resetData()

            youtubeUrl?.let {
                addProductImageAdapter.addData(ProductMediaType.VIDEO, it)
            }

            addProductImageAdapter.run {
                image1?.let { addImage(it) }
                image2?.let { addImage(it) }
                image3?.let { addImage(it) }
                image4?.let { addImage(it) }
                image5?.let { addImage(it) }
            }

            addProductImageAdapter.notifyDataSetChanged()
        }
    }

    private fun onClick(position: Int) {
        this.imagePosition = position

        ApplicationData.productBundle?.youtubeUrl?.let {
            CustomAlertDialog(
                context = this,
                message = getString(R.string.like_to_add_photo),
                primaryKey = getString(R.string.camera),
                secondaryKey = getString(R.string.gallery),
                primaryKeyAction = {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraRequestCode)
                },
                secondaryKeyAction = {
                    val intent = Intent()
                    intent.type = "image/jpeg"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), galleryRequestCode)
                }
            ).show()
        } ?: run {
            AddMediaDialog(
                context = this,
                cameraAction = {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraRequestCode)
                },
                galleryAction = {
                    val intent = Intent()
                    intent.type = "image/jpeg"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), galleryRequestCode)
                },
                saveAction = {
                    ApplicationData.productBundle?.youtubeUrl = it
                    addProductImageAdapter.addData(ProductMediaType.VIDEO, it)
                    addProductImageAdapter.notifyDataSetChanged()
                }
            ).show()
        }
    }

    override fun onContinueClick() {
        if (
            ApplicationData.productBundle?.run {
                (width.isOn() && widthValue.isNullOrEmpty()) || (height.isOn() && heightValue.isNullOrEmpty()) ||
                (length.isOn() && lengthValue.isNullOrEmpty()) || (weight.isOn() && weightValue.isNullOrEmpty()) ||
                (volume.isOn() && volumeValue.isNullOrEmpty())
            } == true
        ) {
            viewDataBinding.root.showMessage(getString(R.string.fill_all_the_fields))
            return
        }

        if(!addProductImageAdapter.isImageSelected()) {
            viewDataBinding.root.showMessage(getString(R.string.at_least_one_image_required))
            return
        } else {
            val selectedImages = addProductImageAdapter.getSelectedImages()

            ApplicationData.productBundle?.run {
                image1 = if(selectedImages.isNotEmpty()) selectedImages[0].second else null
                image2 = if(selectedImages.size > 1) selectedImages[1].second else null
                image3 = if(selectedImages.size > 2) selectedImages[2].second else null
                image4 = if(selectedImages.size > 3) selectedImages[3].second else null
                image5 = if(selectedImages.size > 4) selectedImages[4].second else null
            }
        }

        navigateTo(AddProductDescActivity::class.java, arrayListOf(
            Pair(EDIT_PRODUCT, isToEdit)
        ))
    }

    override fun onSaveAsDraftClick() {
        ApplicationData.productBundle?.run {
            progressBar.setMessage(getString(productId?.let { R.string.updating_product } ?: R.string.adding_product))
            mViewModel.doSubmitForm(1, productId)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == cameraRequestCode) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPicturesFileUri(CAMERA_CAPTURED_PRODUCT_NAME)?.let {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply { putExtra(MediaStore.EXTRA_OUTPUT, it) }
                    startActivityForResult(intent, cameraRequestCode)
                }
            } else {
                viewDataBinding.root.showMessage(getString(R.string.camera_permission_required))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UCrop.REQUEST_CROP -> {
                data?.let {
                    UCrop.getOutput(it)?.let { uri ->
                        val file = uri.toFile()

                        if(validateFileSize(file, 2F)) {
                            val path = file.path

                            (viewDataBinding.recyclerView.adapter as AddProductImageAdapter).run {
                                val imageIndex = addData(ProductMediaType.IMAGE, path, imagePosition)
                                notifyDataSetChanged()

                                ApplicationData.productBundle?.run {
                                    when(imageIndex) {
                                        0 -> image1 = path
                                        1 -> image2 = path
                                        2 -> image3 = path
                                        3 -> image4 = path
                                        4 -> image5 = path
                                    }
                                }
                            }
                        } else {
                            CustomAlertDialog(
                                context = this,
                                message = getString(R.string.max_allowed_size_2_mb),
                                primaryKey = getString(R.string.ok),
                            ).show()
                        }
                    }
                }
            }
            cameraRequestCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), CAMERA_CAPTURED_PRODUCT_NAME)

                    val destination = File(cacheDir, "un_user_product.jpg")
                    if(!destination.exists()) destination.createNewFile()

                    UCrop.of(Uri.fromFile(file), Uri.fromFile(destination))
                        .withAspectRatio(1F, 1F)
                        .start( this)
                }
            }
            galleryRequestCode -> {
                data?.data?.let{
                    val destination = File(cacheDir, "${System.currentTimeMillis()}_gl_un.jpg")
                    if(!destination.exists()) destination.createNewFile()

                    UCrop.of(it, Uri.fromFile(destination))
                        .withAspectRatio(1F, 1F)
                        .start( this)
                }
            }
        }
    }

    override fun onBackClick() {
        onBackPressed()
    }
}