package com.artisan.un.ui.home

import android.content.Intent
import androidx.core.widget.doOnTextChanged
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityEditAddressBinding
import com.artisan.un.ui.common.ActivityGoogleMap
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.home.viewModel.EditAddressViewModel
import com.artisan.un.utils.*
import com.artisan.un.utils.apis.CityData
import com.artisan.un.utils.apis.TehsilData
import com.artisan.un.utils.apis.UserAddress

class EditAddressActivity : BaseActivity<ActivityEditAddressBinding, EditAddressViewModel>(R.layout.activity_edit_address, EditAddressViewModel::class), EditAddressListener, AppBarListener {
    private var selectedCity: CityData? = null
    private var selectedTehsil: TehsilData? = null
    private var addressData: UserAddress? = null
    private lateinit var savedAddressData: UserAddress
    private val googleMapRequestCode = 1003
    private var isInitialized = false

    override fun onCreate() {
        addressData = ApplicationData.user?.address?.registered?.copy()
        addressData?.district = null
        addressData?.tehsil = null

        viewDataBinding.listener = this
        viewDataBinding.appBarListener = this
        viewDataBinding.address = addressData
        viewDataBinding.viewModel = mViewModel

        observeData()
        initListener()
    }

    private fun initListener() {
        viewDataBinding.inputCity.doOnTextChanged { text, _, _, _ ->
            selectedCity = mViewModel.cityList.value?.find{ it.name.equals(text.toString(), true) }

            if(isInitialized) {
                addressData?.tehsil = null
                addressData?.tehsil_id = null
                viewDataBinding.address = addressData
            } else {
                isInitialized = true
            }

            if(selectedCity != null) mViewModel.getTehsil(selectedCity?.id ?: 0)
        }

        viewDataBinding.inputTahsil.doOnTextChanged { text, _, _, _ ->
            selectedTehsil = mViewModel.tehsilList.value?.find { it.name.equals(text.toString(), true) }
        }
    }

    private fun observeData() {
        mViewModel.cityList.observe(this, {
            it.find { data -> data.id == addressData?.districtId }?.let { city ->
                addressData?.district = city.name
                viewDataBinding.address = addressData
            }
        })

        mViewModel.tehsilList.observe(this, {
            it.find { data -> data.id == addressData?.tehsil_id }?.let { tehsil ->
                addressData?.tehsil = tehsil.name
                viewDataBinding.address = addressData
            }
        })

        mViewModel.requestStatus.observe(this, {
            ApplicationData.user?.address?.registered = addressData
            CustomAlertDialog(
                context = this,
                isCancelable = false,
                message = getString(R.string.address_updated_successfully),
                primaryKey = getString(R.string.ok),
                primaryKeyAction = {
                    navigateTo(MainActivity::class.java, arrayListOf(Pair(MENU_TYPE, MenuType.PROFILE.name)))
                    finishAffinity()
                }
            ).show()
        })
    }

    override fun onBackClick() = onBackPressed()

    override fun onSaveChangesClick(address: UserAddress) {
        val error = validateData(address)
        if(error.isNotEmpty()) viewDataBinding.root.showMessage(error)
        else {
            savedAddressData = address

            var tempAddress = ""
            tempAddress += "${address.address_line_one}, "
            tempAddress += "${selectedCity?.name ?: address.district}, "
            tempAddress += "${selectedTehsil?.name ?: address.tehsil}, "
            tempAddress += "${address.state}, "
            tempAddress += address.country

            val intent = Intent(this, ActivityGoogleMap::class.java)
            intent.putExtra(USER_ADDRESS, tempAddress)
            intent.putExtra(LATITUDE, ApplicationData.user?.user?.lat)
            intent.putExtra(LONGITUDE, ApplicationData.user?.user?.log)

            startActivityForResult(intent, googleMapRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == googleMapRequestCode) {
            data?.run {
                val address = getStringExtra("address")
                val lat = getDoubleExtra("lat", 0.0)
                val long = getDoubleExtra("long", 0.0)

                if(address != null && lat != 0.0 && long != 0.0) {
                    addressData?.let {
                        it.address_line_one = savedAddressData.address_line_one
                        it.country = savedAddressData.country
                        it.state = savedAddressData.state
                        it.district = selectedCity?.name ?: savedAddressData.district
                        it.countryId = savedAddressData.countryId
                        it.stateId = savedAddressData.stateId
                        it.districtId = selectedCity?.id ?: savedAddressData.districtId
                        it.tehsil_id = selectedTehsil?.id ?: savedAddressData.tehsil_id
                        it.tehsil = selectedTehsil?.name ?: savedAddressData.tehsil
                        it.pincode = savedAddressData.pincode
                        it.lat = lat
                        it.log = long

                        progressBar.setMessage(getString(R.string.updating_address))
                        mViewModel.requestUpdateAddress(it)
                    }
                }
            }
        }
    }

    private fun validateData(address: UserAddress?): String = run {
        if(
            address?.address_line_one.isNullOrEmpty() ||
            address?.pincode.isNullOrEmpty() ||
            address?.country.isNullOrEmpty() ||
            address?.state.isNullOrEmpty() ||
            address?.district.isNullOrEmpty() ||
            address?.tehsil.isNullOrEmpty()
        ) getString(R.string.fill_all_the_fields)
        else if(address?.pincode?.length ?: 0 < 6) getString(R.string.enter_valid_pin_number)
        else ""
    }
}