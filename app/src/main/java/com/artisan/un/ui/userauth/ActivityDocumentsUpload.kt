package com.artisan.un.ui.userauth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityDocumentsUploadBinding
import com.artisan.un.ui.common.ActivityGoogleMap
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.home.MainActivity
import com.artisan.un.ui.userauth.viewModel.DocumentUploadViewModel
import com.artisan.un.ui.userauth.viewModel.LoginViewModel
import com.artisan.un.utils.*
import com.yalantis.ucrop.UCrop
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.io.File
import java.util.*

class ActivityDocumentsUpload :
    BaseActivity<ActivityDocumentsUploadBinding, DocumentUploadViewModel>(
        R.layout.activity_documents_upload,
        DocumentUploadViewModel::class
    ), DocumentVerificationListener {
    private lateinit var loginViewModel: LoginViewModel
    private var documentType: String? = null
    private var userRole: Int? = null
    private val cameraRequestCode = 1001
    private val galleryRequestCode = 1002
    private val googleMapRequestCode = 1003
    private var docRequestCode = -1

    val fields = HashMap<String, RequestBody>()
    val files = ArrayList<MultipartBody.Part>()

    override fun onCreate() {
        loginViewModel = getViewModel()
        viewDataBinding.model = mViewModel
        viewDataBinding.lifecycleOwner = this
        viewDataBinding.listener = this

        documentType = intent.extras?.getString(DOCUMENT_TYPES)
        userRole = intent.extras?.getInt(USER_ROLE)

        viewDataBinding.radioShg.isChecked = userRole == Roles.SHG.ID
        viewDataBinding.radioArtisan.isChecked = userRole == Roles.Artisan.ID

        initView()
        observeData()
        setListener()
    }

    private fun setListener() {
        viewDataBinding.run {
            radioGroupUserType.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_shg -> userRole = Roles.SHG.ID
                    R.id.radio_artisan -> userRole = Roles.Artisan.ID
                }

                initView()
            }

            resetPan.setOnClickListener {
                mViewModel.panName.postValue("")
                mViewModel.panNumber.postValue("")
                mViewModel.panDOB.postValue("")
                mViewModel.panImage.postValue("")
                mViewModel.panImageName.postValue("")
            }

            etRegisterCity.doOnTextChanged { text, _, _, _ ->
                val cityId =
                    mViewModel.cityRegister().value?.firstOrNull { it.name == text?.toString() }?.id
                mViewModel.addressRegisterTehsil.postValue(null)
                if (cityId != null) mViewModel.getTehsil(cityId)
            }
        }
    }

    private fun initView() {
        if (documentType != null) {
            when (documentType) {
                DOC_AADHAR -> viewDataBinding.cvAadharCard.visibility = View.VISIBLE
                DOC_PAN -> viewDataBinding.cvPanCard.visibility = View.VISIBLE
                DOC_BRN -> viewDataBinding.cvBrnCard.visibility = View.VISIBLE
            }
        } else {
            viewDataBinding.run {
                cvUserTypeCard.visibility = View.VISIBLE
                cvAadharCard.visibility = View.GONE
                cvPanCard.visibility = View.GONE
                cvBrnCard.visibility = View.GONE
                cvAddressRegister.visibility = View.GONE

                when (userRole) {
                    Roles.Artisan.ID -> {
                        cvAadharCard.visibility = View.VISIBLE
                        cvAddressRegister.visibility = View.VISIBLE
                    }
                    Roles.SHG.ID -> {
                        resetPan.visibility = View.VISIBLE
                        cvAadharCard.visibility = View.VISIBLE
                        cvPanCard.visibility = View.VISIBLE
                        cvBrnCard.visibility = View.VISIBLE
                        cvAddressRegister.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun observeData() {
        mViewModel.requestStatus.observe(this) {
            if (it == true) {
                CustomAlertDialog(
                    context = this,
                    message = getString(R.string.document_uploaded_successfully),
                    primaryKey = getString(R.string.ok),
                    isCancelable = false,
                    primaryKeyAction = {
                        loginViewModel.requestUserProfile()
                        viewDataBinding.isLoading = true
                        viewDataBinding.executePendingBindings()
                    }
                ).show()
            }
        }

        mViewModel.panNumber.observe(this) {
            if (it != it.toUpperCase(Locale.ROOT)) {
                viewDataBinding.inputPan.setText(it.toUpperCase(Locale.ROOT))
                viewDataBinding.inputPan.setSelection(it.length)
            }
        }

        loginViewModel.userResponse.observe(this) {
            it.user?.api_token?.let { token -> mPref.authToken = token }
            ApplicationData.user = it

            navigateTo(MainActivity::class.java)
            finishAffinity()
        }
    }

    override fun onBackClick() = onBackPressed()

    override fun doPickAADHARDOB() = viewDataBinding.etAadharDob.toDatePicker()

    override fun doPickPANBOB() = viewDataBinding.etPanDob.toDatePicker()

    override fun doPickAADHARFront() = pickImage(PICK_AADHAR_IMAGE_FRONT)

    override fun doPickAADHARBack() = pickImage(PICK_AADHAR_IMAGE_BACK)

    override fun doPickPAN() = pickImage(PICK_PAN_IMAGE)

    override fun doPickBRN() = pickImage(PICK_BRN_IMAGE)

    private fun pickImage(requestCode: Int) {
        docRequestCode = requestCode

        CustomAlertDialog(
            context = this,
            message = getString(R.string.like_to_add_photo),
            primaryKey = getString(R.string.camera),
            secondaryKey = getString(R.string.gallery),
            primaryKeyAction = {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    cameraRequestCode
                )
            },
            secondaryKeyAction = {
                val intent = Intent()
                intent.type = "image/jpeg"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.pick_image)),
                    galleryRequestCode
                )
            }
        ).show()
    }

    override fun onBackPressed() {
        if (documentType == null) {
            mPref.clear()
            ApplicationData.user = null
            navigateTo(ActivityLogin::class.java)
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }

    override fun onVerifyClick() {
        var error: String?

        if (viewDataBinding.cvIsSanjeeviniShg.visibility == View.VISIBLE) validateSanjeeviniSHGInfo()?.run {
            viewDataBinding.root.showMessage(this)
            return
        } ?: run {
            viewDataBinding.cvIsSanjeeviniShg.run {
                when (viewDataBinding.sanjeeviniShg.checkedRadioButtonId) {
                    R.id.sanjeevini_shg_yes -> {
                        val sanjeeviniSHGName = viewDataBinding.etSHGName.text.toString().trim()
                        val sanjeeviniSHGGplf =
                            viewDataBinding.etSHGGPLGNumber.text.toString().trim()
                        val sanjeeviniSHGMisCode =
                            viewDataBinding.etMISCodeNumber.text.toString().trim()

                        fields["is_sanjeevini"] = "1".toRequestBody()
                        fields["sanjeevini_name"] = sanjeeviniSHGName.toRequestBody()
                        fields["sanjeevini_gplf"] = sanjeeviniSHGGplf.toRequestBody()
                        fields["sanjeevini_mis"] = sanjeeviniSHGMisCode.toRequestBody()
                    }
                    R.id.sanjeevini_shg_no -> {
                        fields["is_sanjeevini"] = "0".toRequestBody()
                    }
                }
            }
        }

        if (viewDataBinding.cvAadharCard.isVisible) {
            error = validateAadharCard()
            if (error != null) {
                viewDataBinding.root.showMessage(error)
                return
            }
        }

        if (viewDataBinding.bankCard.isVisible) {
            error = validateBank()
            if (error != null) {
                viewDataBinding.root.showMessage(error)
                return
            }
        }

        if (viewDataBinding.cvPanCard.isVisible) {
            error = validatePanCard()
            if (error != null) {
                viewDataBinding.root.showMessage(error)
                return
            }
        }

        if (viewDataBinding.cvBrnCard.isVisible) {
            error = validateBrnCard()
            if (error != null) {
                viewDataBinding.root.showMessage(error)
                return
            }
        }

        if (viewDataBinding.cvAddressRegister.isVisible) {
            error = validateRegisteredAddress()
            if (error != null) {
                viewDataBinding.root.showMessage(error)
                return
            }
        }

        if (viewDataBinding.bankCard.isVisible) {
            fields["acc_holder_name"] = (mViewModel.bankName.value ?: "").toRequestBody()
            fields["gst_number"] = (mViewModel.bankGst.value ?: "").toRequestBody()
            fields["fssai_number"] = (mViewModel.bankFCC.value ?: "").toRequestBody()
            fields["acc_number"] = (mViewModel.bankNumber.value ?: "").toRequestBody()
            fields["ifsc_code"] = (mViewModel.bankISFC.value ?: "").toRequestBody()


            Log.d("bankISFC", ""+mViewModel.bankISFC.value)
            Log.d("bankISFC", ""+mViewModel.bankNumber.value)


        }

        /*-----------AADHAR  CARD  -------------*/
        if (viewDataBinding.cvAadharCard.isVisible) {
            fields["adhar_card_no"] = (mViewModel.aadharNumber.value ?: "").toRequestBody()
            fields["adhar_name"] = (mViewModel.aadharName.value ?: "").toRequestBody()
            fields["adhar_dob"] = (mViewModel.aadharDOB.value ?: "").toRequestBody()

            mViewModel.aadharImageFront.value?.let {
                files.add(File(it).toMultiPartFile("adhar_card_front_file", "image/*"))
            }
            mViewModel.aadharImageBack.value?.let {
                files.add(File(it).toMultiPartFile("adhar_card_back_file", "image/*"))
            }
        }

        /*-----------  PAN CARD  -------------*/
        if (viewDataBinding.cvPanCard.isVisible) {
            mViewModel.panNumber.value?.let {
                if (it.isNotEmpty()) fields["pancard_no"] = it.toRequestBody()
            }
            mViewModel.panName.value?.let {
                if (it.isNotEmpty()) fields["pancard_name"] = it.toRequestBody()
            }
            mViewModel.panDOB.value?.let {
                if (it.isNotEmpty()) fields["pancard_dob"] = it.toRequestBody()
            }

            mViewModel.panImage.value?.let {
                if (it.isNotEmpty()) files.add(File(it).toMultiPartFile("pancard_file", "image/*"))
            }
        }

        /*-----------  PAHCHAN ID -------------*/
        if (viewDataBinding.cvBrnCard.isVisible) {
            fields["brn_no"] = (mViewModel.brnNumber.value ?: "").toRequestBody()
            fields["brn_name"] = (mViewModel.brnName.value ?: "").toRequestBody()
            mViewModel.brnImage.value?.let {
                files.add(File(it).toMultiPartFile("brn_file", "image/*"))
            }
        }
        /*-----------  ADDRESS REGISTER  -------------*/
        if (viewDataBinding.cvAddressRegister.isVisible) {
            fields["address_line_one_registered"] =
                (mViewModel.addressRegisterLine1.value ?: "").toRequestBody()
            fields["pincode_registered"] =
                (mViewModel.addressRegisterPin.value ?: "").toRequestBody()

            val regCountry = (mViewModel.countryData().value?.find {
                it.name.equals(mViewModel.addressRegisterCountry.value, true)
            }?.id ?: 0).toString()

            val regState = (mViewModel.stateRegister().value?.find {
                it.name.equals(mViewModel.addressRegisterState.value, true)
            }?.id ?: 0).toString()

            val regCity = (mViewModel.cityRegister().value?.find {
                it.name.equals(mViewModel.addressRegisterCity.value, true)
            }?.id ?: 0).toString()

            val regTehsil = (mViewModel.tehsilData().value?.find {
                it.name.equals(mViewModel.addressRegisterTehsil.value, true)
            }?.id ?: 0).toString()

            fields["country_registered"] = regCountry.toRequestBody()
            fields["state_registered"] = regState.toRequestBody()
            fields["district_registered"] = regCity.toRequestBody()
            fields["tehsil_id"] = regTehsil.toRequestBody()
        }

        documentType?.let {
            fields["is_adhar_update"] = (if (it == DOC_AADHAR) 1 else 0).toString().toRequestBody()
            fields["is_pan_update"] = (if (it == DOC_PAN) 1 else 0).toString().toRequestBody()
            fields["is_brn_update"] = (if (it == DOC_BRN) 1 else 0).toString().toRequestBody()

            progressBar.setMessage(getString(R.string.uploading_documents))
            mViewModel.reUploadDocument(fields, files)
        } ?: run {
            var address = ""
            address += "${mViewModel.addressRegisterLine1.value}, "
            address += "${mViewModel.addressRegisterTehsil.value}, "
            address += "${mViewModel.addressRegisterCity.value}, "
            address += "${mViewModel.addressRegisterState.value}, "
            address += mViewModel.addressRegisterCountry.value

            val intent = Intent(this, ActivityGoogleMap::class.java)
            intent.putExtra(USER_ADDRESS, address)

            startActivityForResult(intent, googleMapRequestCode)
        }
    }

    private fun validateSanjeeviniSHGInfo(): String? {
        viewDataBinding.cvIsSanjeeviniShg.run {
            when (viewDataBinding.sanjeeviniShg.checkedRadioButtonId) {
                R.id.sanjeevini_shg_yes -> {
                    val sanjeeviniSHGName = viewDataBinding.etSHGName.text.toString().trim()
                    val sanjeeviniSHGGplf = viewDataBinding.etSHGGPLGNumber.text.toString().trim()
                    val sanjeeviniSHGMisCode =
                        viewDataBinding.etMISCodeNumber.text.toString().trim()

                    return when {
                        sanjeeviniSHGName.isEmpty() -> getString(R.string.enter_valid_sanjeevini_shg_name)
                        sanjeeviniSHGGplf.isEmpty() -> getString(R.string.enter_valid_sanjeevini_gplf)
                        else -> null
                    }
                }
                R.id.sanjeevini_shg_no -> {
                    return getString(R.string.please_select_if_sanjeevini_shg_or_not)
                }
                else -> {
                    return null
                }
            }
        }
    }

    private fun validateAadharCard(): String? = run {
        if (mViewModel.aadharNumber.value.isNullOrEmpty() || mViewModel.aadharName.value.isNullOrEmpty() || mViewModel.aadharDOB.value.isNullOrEmpty() || mViewModel.aadharImageFront.value.isNullOrEmpty() || mViewModel.aadharImageBack.value.isNullOrEmpty())
            getString(R.string.aadhar_information_required)
        else if (mViewModel.aadharNumber.value?.length ?: 0 < 12)
            getString(R.string.enter_valid_aadhar_number)
        else null
    }

    private fun validateBank(): String? = run {
        if (mViewModel.bankName.value.isNullOrEmpty() ||
            mViewModel.bankNumber.value.isNullOrEmpty() ||
            mViewModel.bankISFC.value.isNullOrEmpty() ||
            mViewModel.bankGst.value.isNullOrEmpty() ||
            mViewModel.bankFCC.value.isNullOrEmpty()
        )
            getString(R.string.bank_information_required)

        else null
    }

    private fun validatePanCard(): String? = run {
        if (documentType == DOC_PAN) {
            if (mViewModel.panNumber.value.isNullOrEmpty() || mViewModel.panName.value.isNullOrEmpty() || mViewModel.panDOB.value.isNullOrEmpty() || mViewModel.panImage.value.isNullOrEmpty())
                getString(R.string.pan_information_required)
            else if (mViewModel.panNumber.value?.length ?: 0 < 10)
                getString(R.string.enter_valid_pan_number)
            else null
        } else {
            if (!mViewModel.panNumber.value.isNullOrEmpty() && !mViewModel.panName.value.isNullOrEmpty() && !mViewModel.panDOB.value.isNullOrEmpty() && !mViewModel.panImage.value.isNullOrEmpty()) {
                if (mViewModel.panNumber.value?.length ?: 0 < 10) getString(R.string.enter_valid_pan_number)
                else null
            } else if (!mViewModel.panNumber.value.isNullOrEmpty() || !mViewModel.panName.value.isNullOrEmpty() || !mViewModel.panDOB.value.isNullOrEmpty() || !mViewModel.panImage.value.isNullOrEmpty()) {
                getString(R.string.partial_pan_information_not_allowed)
            } else null
        }
    }

    private fun validateBrnCard(): String? = run {
        if (documentType == DOC_BRN) {
            if (mViewModel.brnNumber.value.isNullOrEmpty() || mViewModel.brnName.value.isNullOrEmpty() || mViewModel.brnImage.value.isNullOrEmpty())
                getString(R.string.brn_information_required)
            else if (mViewModel.brnNumber.value?.length ?: 0 < 16)
                getString(R.string.enter_valid_brn_number)
            else null
        } else {
            if (!mViewModel.brnNumber.value.isNullOrEmpty() && !mViewModel.brnName.value.isNullOrEmpty() && !mViewModel.brnImage.value.isNullOrEmpty()) {
                if (mViewModel.brnNumber.value?.length ?: 0 < 16)
                    getString(R.string.enter_valid_brn_number)
                else null
            } else if (!mViewModel.brnNumber.value.isNullOrEmpty() || !mViewModel.brnName.value.isNullOrEmpty() || !mViewModel.brnImage.value.isNullOrEmpty()) {
                getString(R.string.partial_brn_information_not_allowed)
            } else null
        }
    }

    private fun validateRegisteredAddress(): String? = run {
        if (mViewModel.addressRegisterLine1.value.isNullOrEmpty() || mViewModel.addressRegisterCountry.value.isNullOrEmpty() || mViewModel.addressRegisterState.value.isNullOrEmpty() || mViewModel.addressRegisterCity.value.isNullOrEmpty() || mViewModel.addressRegisterTehsil.value.isNullOrEmpty() || mViewModel.addressRegisterPin.value.isNullOrEmpty())
            getString(R.string.registered_address_required)
        else if (mViewModel.addressRegisterPin.value?.length ?: 0 < 6)
            getString(R.string.enter_valid_pin_number)
        else null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            cameraRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPicturesFileUri(CAMERA_CAPTURED_DOCUMENT_NAME)?.let {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                            putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                it
                            )
                        }
                        startActivityForResult(intent, cameraRequestCode)
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
                        val path = uri.toFile().path

                        when (docRequestCode) {
                            PICK_AADHAR_IMAGE_FRONT -> {
                                mViewModel.aadharImageFrontName.value = uri.toFile().name
                                mViewModel.aadharImageFront.value = path
                            }
                            PICK_AADHAR_IMAGE_BACK -> {
                                mViewModel.aadharImageBackName.value = uri.toFile().name
                                mViewModel.aadharImageBack.value = path
                            }
                            PICK_PAN_IMAGE -> {
                                mViewModel.panImageName.value = uri.toFile().name
                                mViewModel.panImage.value = path
                            }
                            PICK_BRN_IMAGE -> {
                                mViewModel.brnImageName.value = uri.toFile().name
                                mViewModel.brnImage.value = path
                            }
                        }
                    }
                }
            }
            cameraRequestCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    val file = File(
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        CAMERA_CAPTURED_DOCUMENT_NAME
                    )

                    val destination = File(cacheDir, getImageName(docRequestCode))
                    if (!destination.exists()) destination.createNewFile()

                    UCrop.of(Uri.fromFile(file), Uri.fromFile(destination))
                        .withAspectRatio(1F, 1F)
                        .start(this)
                }
            }
            galleryRequestCode -> {
                data?.let {
                    val response = it.data ?: Uri.parse("")

                    val destination = File(cacheDir, getImageName(docRequestCode))
                    if (!destination.exists()) destination.createNewFile()

                    UCrop.of(response, Uri.fromFile(destination))
                        .withAspectRatio(1.59F, 1F)
                        .start(this)
                }
            }
            googleMapRequestCode -> {
                data?.run {
                    val address = getStringExtra("address")
                    val lat = getDoubleExtra("lat", 0.0)
                    val long = getDoubleExtra("long", 0.0)

                    if (address != null && lat != 0.0 && long != 0.0) {
                        fields["role_id"] = userRole.toString().toRequestBody()
                        fields["lat"] = lat.toString().toRequestBody()
                        fields["log"] = long.toString().toRequestBody()
                        mViewModel.uploadDocuments(fields, files)
                    }
                }
            }
        }
    }

    private fun getImageName(imageType: Int): String = run {
        when (imageType) {
            PICK_AADHAR_IMAGE_FRONT -> "aadhar_card_front.jpg"
            PICK_AADHAR_IMAGE_BACK -> "aadhar_card_back.jpg"
            PICK_PAN_IMAGE -> "pan_image.jpg"
            PICK_BRN_IMAGE -> "brn_image.jpg"
            else -> System.currentTimeMillis().toString()
        }
    }

    companion object {
        const val PICK_AADHAR_IMAGE_FRONT = 100
        const val PICK_AADHAR_IMAGE_BACK = 101
        const val PICK_PAN_IMAGE = 102
        const val PICK_BRN_IMAGE = 103
    }
}