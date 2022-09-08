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
import com.artisan.un.utils.apis.StateData
import com.artisan.un.utils.apis.TehsilData
import com.artisan.un.utils.apis.UserAddress

class EditAddressActivity : BaseActivity<ActivityEditAddressBinding, EditAddressViewModel>(R.layout.activity_edit_address, EditAddressViewModel::class), EditAddressListener, AppBarListener {
    private var selectedState: StateData? = null
    private var selectedCity: CityData? = null
    private var addressData: UserAddress? = null
    private lateinit var savedAddressData: UserAddress
    private val googleMapRequestCode = 1003

    override fun onCreate() {
        addressData = ApplicationData.user?.address?.registered?.copy()

        viewDataBinding.listener = this
        viewDataBinding.appBarListener = this
        viewDataBinding.address = addressData
        viewDataBinding.viewModel = mViewModel

        initListener()
        observeData()
    }

    private fun initListener() {
        viewDataBinding.inputState.doOnTextChanged { inputStateName, _, _, _ ->
            mViewModel.stateList.value?.find { it.name.equals(inputStateName.toString(), true) }?.let { state ->
                if(state.id != addressData?.stateId) {
                    selectedState = state
                    addressData?.stateId = state.id
                    addressData?.state = state.name
                    addressData?.district = null
                    addressData?.districtId = null

                    viewDataBinding.address = addressData
                    viewDataBinding.executePendingBindings()

                    mViewModel.getCity(state.id)
                }
            }
        }

        viewDataBinding.inputCity.doOnTextChanged { inputCityName, _, _, _ ->
            mViewModel.cityList.value?.find { it.name.equals(inputCityName.toString(), true) }?.let { city ->
                selectedCity = city

                addressData?.district = city.name
                addressData?.districtId = city.id
                viewDataBinding.address = addressData
                viewDataBinding.executePendingBindings()
            }
        }
    }

    private fun observeData() {
        mViewModel.stateList.observe(this) { states ->
            states.find { it.id == addressData?.stateId }?.let { state ->
                addressData?.state = state.name
                addressData?.stateId = state.id
                viewDataBinding.address = addressData
                viewDataBinding.executePendingBindings()
            }
        }

        mViewModel.cityList.observe(this) { cities ->
            cities.find { it.id == addressData?.districtId }?.let { city ->
                addressData?.district = city.name
                addressData?.districtId = city.id
                viewDataBinding.address = addressData
                viewDataBinding.executePendingBindings()
            }
        }

        mViewModel.requestStatus.observe(this) {
            ApplicationData.user?.address?.registered = addressData
            CustomAlertDialog(
                context = this,
                isCancelable = false,
                message = getString(R.string.address_updated_successfully),
                primaryKey = getString(R.string.ok),
                primaryKeyAction = {
                    navigateTo(
                        MainActivity::class.java,
                        arrayListOf(Pair(MENU_TYPE, MenuType.PROFILE.name))
                    )
                    finishAffinity()
                }
            ).show()
        }
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
            address?.district.isNullOrEmpty()
        ) getString(R.string.fill_all_the_fields)
        else if((address?.pincode?.length ?: 0) < 6) getString(R.string.enter_valid_pin_number)
        else ""
    }
}