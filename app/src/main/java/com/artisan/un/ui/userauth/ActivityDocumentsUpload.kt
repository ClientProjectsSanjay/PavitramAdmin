package com.artisan.un.ui.userauth

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.core.widget.doOnTextChanged
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityDocumentsUploadBinding
import com.artisan.un.ui.common.ActivityGoogleMap
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.home.MainActivity
import com.artisan.un.ui.userauth.viewModel.DocumentUploadViewModel
import com.artisan.un.utils.*
import com.google.android.material.textfield.TextInputEditText
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.ArrayList

class ActivityDocumentsUpload : BaseActivity<ActivityDocumentsUploadBinding, DocumentUploadViewModel>(
    R.layout.activity_documents_upload,
    DocumentUploadViewModel::class
), DocumentVerificationListener {
    private lateinit var mDocumentFileType: DocumentFileType
    private var userRoleId: Int? = null
    private var mDocumentType: String? = null
    private val mCameraRequestCode = 1001
    private val mGalleryRequestCode = 1002
    private val mGoogleMapRequestCode = 1003

    override fun onCreate() {
        viewDataBinding.viewModel = mViewModel
        viewDataBinding.listener = this

        userRoleId = intent.getIntExtra(USER_ROLE, 0)
        mDocumentType = intent.getStringExtra(DOCUMENT_TYPES)

        mDocumentType?.let {
            when(mDocumentType) {
                DOC_AADHAR -> viewDataBinding.aadharCardContainer.visibility = View.VISIBLE
                DOC_COMPANY -> viewDataBinding.companyDetailsContainer.visibility = View.VISIBLE
                DOC_PAN -> viewDataBinding.panCardContainer.visibility = View.VISIBLE
                DOC_GST -> viewDataBinding.gstDetailsContainer.visibility = View.VISIBLE
                DOC_BRN -> viewDataBinding.brnDetailsContainer.visibility = View.VISIBLE
                DOC_MSME -> viewDataBinding.msmeDetailsContainer.visibility = View.VISIBLE
                DOC_FSSAI -> viewDataBinding.fssaiDetailsContainer.visibility = View.VISIBLE
                DOC_ACCOUNT -> viewDataBinding.bankAccountContainer.visibility = View.VISIBLE
            }
        } ?: run {
            viewDataBinding.userTypeContainer.visibility = View.VISIBLE
            viewDataBinding.radioCompany.isChecked = userRoleId == Roles.SHG.ID
            viewDataBinding.radioIndividual.isChecked = userRoleId == Roles.Artisan.ID
            handleFormView(userRoleId)
        }

        initListeners()
        initObserver()
    }

    private fun handleFormView(userRoleId: Int?) {
        this.userRoleId = Roles.Artisan.ID
        when(userRoleId) {
            Roles.Artisan.ID -> {
                viewDataBinding.companyDetailsContainer.visibility = View.GONE
                viewDataBinding.gstDetailsContainer.visibility = View.GONE

                viewDataBinding.aadharCardContainer.visibility = View.VISIBLE
                viewDataBinding.panCardContainer.visibility = View.VISIBLE
                viewDataBinding.brnDetailsContainer.visibility = View.VISIBLE
                viewDataBinding.fssaiDetailsContainer.visibility = View.VISIBLE
                viewDataBinding.bankAccountContainer.visibility = View.VISIBLE
                viewDataBinding.registeredAddressContainer.visibility = View.VISIBLE
            }
            Roles.SHG.ID -> {
                viewDataBinding.aadharCardContainer.visibility = View.GONE

                viewDataBinding.companyDetailsContainer.visibility = View.VISIBLE
                viewDataBinding.panCardContainer.visibility = View.VISIBLE
                viewDataBinding.gstDetailsContainer.visibility = View.VISIBLE
                viewDataBinding.brnDetailsContainer.visibility = View.VISIBLE
                viewDataBinding.msmeDetailsContainer.visibility = View.VISIBLE
                viewDataBinding.fssaiDetailsContainer.visibility = View.VISIBLE
                viewDataBinding.bankAccountContainer.visibility = View.VISIBLE
                viewDataBinding.registeredAddressContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun initListeners() {
        viewDataBinding.radioCompany.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) handleFormView(Roles.SHG.ID)
        }

        viewDataBinding.radioIndividual.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) handleFormView(Roles.Artisan.ID)
        }

        viewDataBinding.inputState.doOnTextChanged { inputStateName, _, _, _ ->
            val stateId = mViewModel.mStateDataObservable.value?.firstOrNull { it.name == inputStateName.toString() }?.id
            viewDataBinding.inputCity.text = null

            if(stateId != null) mViewModel.getCity(stateId)
        }
    }

    private fun initObserver() {
        mViewModel.mCountryDataObservable.observe(this) {

        }

        mViewModel.mStateDataObservable.observe(this) { stateData ->
            viewDataBinding.stateList = stateData.map { it.name ?: "" } as ArrayList<String>
        }

        mViewModel.mCityDataObservable.observe(this) { cityData ->
            viewDataBinding.cityList = cityData.map { it.name ?: "" } as ArrayList<String>
        }

        mViewModel.mRequestStatusObservable.observe(this) {
            if (it == true) {
                CustomAlertDialog(
                    context = this,
                    message = getString(R.string.document_uploaded_successfully),
                    primaryKey = getString(R.string.ok),
                    isCancelable = false,
                    primaryKeyAction = { mViewModel.requestUserProfile() }
                ).show()
            }
        }

        mViewModel.mUserResponseObservable.observe(this) {
            it.user?.api_token?.let { token -> mPref.authToken = token }
            ApplicationData.user = it

            navigateTo(MainActivity::class.java)
            finishAffinity()
        }
    }

    override fun onUploadFileClick(context: Context, fileType: DocumentFileType) {
        mDocumentFileType = fileType

        CustomAlertDialog(
            context = this,
            message = getString(R.string.like_to_add_photo),
            primaryKey = getString(R.string.camera),
            secondaryKey = getString(R.string.gallery),
            primaryKeyAction = {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), mCameraRequestCode)
            },
            secondaryKeyAction = {
                val intent = Intent()
                intent.type = "image/jpeg"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.pick_image)),
                    mGalleryRequestCode
                )
            }
        ).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            mCameraRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPicturesFileUri(CAMERA_CAPTURED_DOCUMENT_NAME)?.let {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, it)
                        startActivityForResult(intent, mCameraRequestCode)
                    }
                } else {
                    viewDataBinding.root.showMessage(getString(R.string.camera_permission_required))
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UCrop.REQUEST_CROP -> {
                data?.let {
                    UCrop.getOutput(it)?.let { uri ->
                        mViewModel.mDocumentsFormModel.postValue(when (mDocumentFileType) {
                            DocumentFileType.AADHARFRONT -> {
                                mViewModel.mDocumentsFormModel.value?.apply { aadharFrontImage = uri.toFile() }
                            }
                            DocumentFileType.AADHARBACK -> {
                                mViewModel.mDocumentsFormModel.value?.apply { aadharBackImage = uri.toFile() }
                            }
                            DocumentFileType.LETTERHEAD -> {
                                mViewModel.mDocumentsFormModel.value?.apply { letterHeadImage = uri.toFile() }
                            }
                            DocumentFileType.PAN -> {
                                mViewModel.mDocumentsFormModel.value?.apply { panImage = uri.toFile() }
                            }
                            DocumentFileType.GST -> {
                                mViewModel.mDocumentsFormModel.value?.apply { gstImage = uri.toFile() }
                            }
                            DocumentFileType.BRN -> {
                                mViewModel.mDocumentsFormModel.value?.apply { brnImage = uri.toFile() }
                            }
                            DocumentFileType.UDHYAM -> {
                                mViewModel.mDocumentsFormModel.value?.apply { udhyamImage = uri.toFile() }
                            }
                            DocumentFileType.FACTORY -> {
                                mViewModel.mDocumentsFormModel.value?.apply { factoryImage = uri.toFile() }
                            }
                            DocumentFileType.FSSAI -> {
                                mViewModel.mDocumentsFormModel.value?.apply { fssaiImage = uri.toFile() }
                            }
                            DocumentFileType.ACCOUNTPROOF -> {
                                mViewModel.mDocumentsFormModel.value?.apply { accountProofImage = uri.toFile() }
                            }
                        })
                    }
                }
            }
            mCameraRequestCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    val file = File(
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        CAMERA_CAPTURED_DOCUMENT_NAME
                    )

                    val destination = File(cacheDir, getImageName(mDocumentFileType))
                    if (!destination.exists()) destination.createNewFile()

                    UCrop.of(Uri.fromFile(file), Uri.fromFile(destination))
                        .withAspectRatio(1F, 1F)
                        .start(this)
                }
            }
            mGalleryRequestCode -> {
                data?.let {
                    val response = it.data ?: Uri.parse("")

                    val destination = File(cacheDir, getImageName(mDocumentFileType))
                    if (!destination.exists()) destination.createNewFile()

                    UCrop.of(response, Uri.fromFile(destination))
                        .withAspectRatio(1.59F, 1F)
                        .start(this)
                }
            }
            mGoogleMapRequestCode -> {
                data?.run {
                    val address = getStringExtra("address")
                    val lat = getDoubleExtra("lat", 0.0)
                    val long = getDoubleExtra("long", 0.0)

                    if (address != null && lat != 0.0 && long != 0.0) {
                        mViewModel.mDocumentsFormModel.value?.roleId = userRoleId
                        mViewModel.mDocumentsFormModel.value?.lat = lat
                        mViewModel.mDocumentsFormModel.value?.long = long
                        mViewModel.uploadDocuments()
                    }
                }
            }
        }
    }

    override fun onSelectDateClick(context: Context, dateType: DateOfBirthType, view: TextInputEditText) {
        view.toDatePicker()
    }

    override fun onVerifyClick() {
        if(viewDataBinding.aadharCardContainer.visibility == View.VISIBLE) {
            mViewModel.mDocumentsFormModel.value?.getAadharError(this)?.let {
                viewDataBinding.root.showMessage(it)
                return
            }
        }

        if(viewDataBinding.companyDetailsContainer.visibility == View.VISIBLE) {
            mViewModel.mDocumentsFormModel.value?.getCompanyError(this)?.let {
                viewDataBinding.root.showMessage(it)
                return
            }
        }

        if(viewDataBinding.panCardContainer.visibility == View.VISIBLE) {
            mViewModel.mDocumentsFormModel.value?.getPanError(this, mDocumentType != null)?.let {
                viewDataBinding.root.showMessage(it)
                return
            }
        }

        if(viewDataBinding.gstDetailsContainer.visibility == View.VISIBLE) {
            mViewModel.mDocumentsFormModel.value?.getGstError(this, mDocumentType != null)?.let {
                viewDataBinding.root.showMessage(it)
                return
            }
        }

        if(viewDataBinding.brnDetailsContainer.visibility == View.VISIBLE) {
            mViewModel.mDocumentsFormModel.value?.getBrnError(this, mDocumentType != null)?.let {
                viewDataBinding.root.showMessage(it)
                return
            }
        }

        if(viewDataBinding.msmeDetailsContainer.visibility == View.VISIBLE) {
            mViewModel.mDocumentsFormModel.value?.getMsmeError(this, mDocumentType != null)?.let {
                viewDataBinding.root.showMessage(it)
                return
            }
        }

        if(viewDataBinding.fssaiDetailsContainer.visibility == View.VISIBLE) {
            mViewModel.mDocumentsFormModel.value?.getFssaiError(this, mDocumentType != null)?.let {
                viewDataBinding.root.showMessage(it)
                return
            }
        }

        if(viewDataBinding.bankAccountContainer.visibility == View.VISIBLE) {
            mViewModel.mDocumentsFormModel.value?.getBankAccountError(this, mDocumentType != null)?.let {
                viewDataBinding.root.showMessage(it)
                return
            }
        }

        if(viewDataBinding.registeredAddressContainer.visibility == View.VISIBLE) {
            mViewModel.mDocumentsFormModel.value?.getRegisteredAddressError(this)?.let {
                viewDataBinding.root.showMessage(it)
                return
            }
        }

        mDocumentType?.let {
            progressBar.setMessage(getString(R.string.uploading_documents))
            mViewModel.reUploadDocument()
        } ?: run {
            var address = ""
            address += "${mViewModel.mDocumentsFormModel.value?.registeredAddressLine}, "
            address += "${mViewModel.mDocumentsFormModel.value?.registeredAddressCity}, "
            address += "${mViewModel.mDocumentsFormModel.value?.registeredAddressState}, "
            address += mViewModel.mDocumentsFormModel.value?.registeredAddressCountry

            val intent = Intent(this, ActivityGoogleMap::class.java)
            intent.putExtra(USER_ADDRESS, address)

            startActivityForResult(intent, mGoogleMapRequestCode)
        }
    }

    private fun getImageName(docType: DocumentFileType): String = run {
        when(docType) {
            DocumentFileType.AADHARFRONT -> "aadhar_card_front.jpg"
            DocumentFileType.AADHARBACK -> "aadhar_card_back.jpg"
            DocumentFileType.LETTERHEAD -> "letterhead_image.jpg"
            DocumentFileType.PAN -> "pan_image.jpg"
            DocumentFileType.GST -> "gst_image.jpg"
            DocumentFileType.BRN -> "brn_image.jpg"
            DocumentFileType.UDHYAM -> "udhyam_image.jpg"
            DocumentFileType.FACTORY -> "factory_image.jpg"
            DocumentFileType.FSSAI -> "fssai_image.jpg"
            DocumentFileType.ACCOUNTPROOF -> "account_proof_image.jpg"
        }
    }

    override fun onBackPressed() {
        if (mDocumentType == null) {
            mPref.clear()
            ApplicationData.user = null
            navigateTo(ActivityLogin::class.java)
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }
}