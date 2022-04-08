package com.artisan.un.ui.home.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.net.toFile
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseFragment
import com.artisan.un.databinding.FragmentProfileBinding
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.home.EditProfileActivity
import com.artisan.un.ui.home.viewModel.HomeViewModel
import com.artisan.un.ui.userauth.ActivityDocumentsUpload
import com.artisan.un.ui.userauth.viewModel.LoginViewModel
import com.artisan.un.utils.*
import com.artisan.un.utils.apis.UserResponse
import com.yalantis.ucrop.UCrop
import java.io.File

class ProfileFragment(private val loginViewModel: LoginViewModel) : BaseFragment<FragmentProfileBinding, HomeViewModel>(R.layout.fragment_profile, HomeViewModel::class), ProfileListener, View.OnClickListener {
    private var userData: UserResponse? = ApplicationData.user
    private val galleryRequestCode = 102
    private val cameraRequestCode = 101

    override fun onCreateView() {
        viewDataBinding.listener = this

        observeData()
        loginViewModel.requestUserProfile()

        viewDataBinding.profileCard.setOnClickListener(this)
        viewDataBinding.docAadhar.root.setOnClickListener(this)
        viewDataBinding.addPan.setOnClickListener(this)
        viewDataBinding.addBrn.setOnClickListener(this)
    }

    private fun setUserData() {
        userData?.let {
            val user = it.user
            viewDataBinding.name = user?.name
            viewDataBinding.image = user?.profileImage
            viewDataBinding.userId = user?.id?.toInt() ?: 0
            viewDataBinding.title = user?.title
            viewDataBinding.mobile = user?.mobile
            viewDataBinding.email = user?.email
            viewDataBinding.address =  formatAddress(it.address?.registered)

            viewDataBinding.isSanjeeviniShg = user?.is_sanjeevini ?: 0
            viewDataBinding.sanjeeviniShgName = user?.sanjeevini_name
            viewDataBinding.sanjeeviniShgGplf = user?.sanjeevini_gplf
            viewDataBinding.sanjeeviniShgMisCode = user?.sanjeevini_mis

            viewDataBinding.showBrn = user?.role_id == Roles.SHG.ID && !user.brn_file.isNullOrEmpty()
            viewDataBinding.showAddBrn = user?.role_id == Roles.SHG.ID && user.brn_file.isNullOrEmpty()
            viewDataBinding.showPan = user?.role_id == Roles.SHG.ID && !user.pancard_file.isNullOrEmpty()
            viewDataBinding.showAddPan = user?.role_id == Roles.SHG.ID && user.pancard_file.isNullOrEmpty()

            viewDataBinding.isAadharVerified = user?.is_adhar_verify == 1
            viewDataBinding.isAadharRejected = user?.is_adhar_verify == 2
            viewDataBinding.isBrnVerified = !user?.brn_file.isNullOrEmpty()
            viewDataBinding.isPanVerified = !user?.pancard_file.isNullOrEmpty()
        }
    }

    override fun onStart() {
        super.onStart()
        setUserData()
    }

    private fun observeData() {
        loginViewModel.userResponse.observe(requireActivity(), {
            userData = it
            setUserData()
            ApplicationData.user = it
        })

        mViewModel.requestStatus.observe(requireActivity(), {
            loginViewModel.requestUserProfile()
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == cameraRequestCode) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requireActivity().getPicturesFileUri(CAMERA_CAPTURED_PROFILE_NAME)?.let {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply { putExtra(MediaStore.EXTRA_OUTPUT, it) }
                    startActivityForResult(intent, cameraRequestCode)
                }
            } else {
                viewDataBinding.root.showMessage(getString(R.string.camera_permission_required))
            }
        }
    }

    override fun onEditProfileClick() {
        requireActivity().navigateTo(EditProfileActivity::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            UCrop.REQUEST_CROP -> {
                data?.let { intent ->
                    UCrop.getOutput(intent)?.let {
                        progressBar.setMessage(getString(R.string.uploading_image))
                        mViewModel.requestProfileImageUpload(it.toFile())
                    }
                }
            }
            cameraRequestCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), CAMERA_CAPTURED_PROFILE_NAME)

                    val destination = File(requireContext().cacheDir, "un_user_profile.jpg")
                    if(!destination.exists()) destination.createNewFile()

                    UCrop.of(Uri.fromFile(file), Uri.fromFile(destination))
                        .withAspectRatio(1F, 1F)
                        .start(requireActivity(), this)
                }
            }
            galleryRequestCode -> {
                data?.let {
                    val response = it.data ?: Uri.parse("")

                    val destination = File(requireContext().cacheDir, "un_user_profile.jpg")
                    if(!destination.exists()) destination.createNewFile()

                    UCrop.of(response, Uri.fromFile(destination))
                        .withAspectRatio(1F, 1F)
                        .start(requireContext(), this)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            viewDataBinding.docAadhar.root.id -> {
                if(userData?.user?.is_adhar_verify != 1)
                    requireContext().navigateTo(ActivityDocumentsUpload::class.java, arrayListOf(Pair(DOCUMENT_TYPES, DOC_AADHAR)))
            }
            viewDataBinding.addPan.id -> {
                requireContext().navigateTo(ActivityDocumentsUpload::class.java, arrayListOf(Pair(DOCUMENT_TYPES, DOC_PAN)))
            }
            viewDataBinding.addBrn.id -> {
                requireContext().navigateTo(ActivityDocumentsUpload::class.java, arrayListOf(Pair(DOCUMENT_TYPES, DOC_BRN)))
            }
            viewDataBinding.profileCard.id -> {
                CustomAlertDialog(
                    context = requireContext(),
                    message = getString(R.string.how_to_add_photo),
                    primaryKey = getString(R.string.camera),
                    secondaryKey = getString(R.string.gallery),
                    primaryKeyAction = {
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), cameraRequestCode)
                    },
                    secondaryKeyAction = {
                        val intent = Intent()
                        intent.type = "image/jpeg"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(Intent.createChooser(intent, "Select Image"), galleryRequestCode)
                    }
                ).show()
            }
        }
    }
}