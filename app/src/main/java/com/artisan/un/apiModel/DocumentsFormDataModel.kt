package com.artisan.un.apiModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.artisan.un.R
import com.artisan.un.utils.apis.CityData
import com.artisan.un.utils.apis.CountryData
import com.artisan.un.utils.apis.StateData
import com.artisan.un.utils.toMultiPartFile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

data class DocumentsFormDataModel(
    var roleId: Int? = null,
    var lat: Double? = null,
    var long: Double? = null,
    var nameAsPerAadhar: String? = null,
    var aadharNumber: String? = null,
    var dobAsPerAadhar: String? = null,
    var aadharFrontImage: File? = null,
    var aadharBackImage: File? = null,
    var companyName: String? = null,
    var companyCinNumber: String? = null,
    var letterHeadImage: File? = null,
    var panNumber: String? = null,
    var nameAsPerPan: String? = null,
    var dobAsPerPan: String? = null,
    var panImage: File? = null,
    var gstNumber: String? = null,
    var gstImage: File? = null,
    var brnNumber: String? = null,
    var brnName: String? = null,
    var brnImage: File? = null,
    var udhyamName: String? = null,
    var udhyamImage: File? = null,
    var factoryNumber: String? = null,
    var factoryImage: File? = null,
    var fssaiNumber: String? = null,
    var fssaiImage: File? = null,
    var bankAccountNumber: String? = null,
    var bankAccountName: String? = null,
    var bankIFSC: String? = null,
    var accountProofImage: File? = null,
    var registeredAddressLine: String? = null,
    var registeredAddressCountry: String? = null,
    var registeredAddressState: String? = null,
    var registeredAddressCity: String? = null,
    var registeredAddressPin: String? = null,
) {

    fun getAadharError(context: Context): String? = run {
        if (aadharNumber.isNullOrEmpty() || nameAsPerAadhar.isNullOrEmpty() || dobAsPerAadhar.isNullOrEmpty() || aadharFrontImage == null || aadharBackImage == null)
            context.getString(R.string.aadhar_information_required)
        else if ((aadharNumber?.length ?: 0) < 12)
            context.getString(R.string.enter_valid_aadhar_number)
        else null
    }

    fun getCompanyError(context: Context): String? = run {
        if (companyName.isNullOrEmpty() || companyCinNumber.isNullOrEmpty() || aadharNumber.isNullOrEmpty() || nameAsPerAadhar.isNullOrEmpty() || dobAsPerAadhar.isNullOrEmpty() || aadharFrontImage == null || aadharBackImage == null || letterHeadImage == null)
            context.getString(R.string.company_information_required)
        else if ((aadharNumber?.length ?: 0) < 12)
            context.getString(R.string.enter_valid_aadhar_number)
        else null
    }

    fun getPanError(context: Context, isRequired: Boolean = false): String? = run {
        if(isRequired) {
            if (panNumber.isNullOrEmpty() || nameAsPerPan.isNullOrEmpty() || dobAsPerPan.isNullOrEmpty() || panImage == null)
                context.getString(R.string.pan_information_required)
            else if ((aadharNumber?.length ?: 0) < 10)
                context.getString(R.string.enter_valid_pan_number)
            else null
        } else {
            if (!panNumber.isNullOrEmpty() && !nameAsPerPan.isNullOrEmpty() && !dobAsPerPan.isNullOrEmpty() && panImage != null) {
                if ((panNumber?.length ?: 0) < 10) context.getString(R.string.enter_valid_pan_number)
                else null
            } else if (!panNumber.isNullOrEmpty() || !nameAsPerPan.isNullOrEmpty() || !dobAsPerPan.isNullOrEmpty() || panImage != null)
                context.getString(R.string.partial_pan_information_not_allowed)
            else null
        }
    }

    fun getGstError(context: Context, isRequired: Boolean = false): String? = run {
        if(isRequired) {
            if(gstNumber.isNullOrEmpty() || gstImage == null)
                context.getString(R.string.gst_information_required)
            else if((gstNumber?.length ?: 0) != 15)
                context.getString(R.string.enter_valid_gst_number)
            else null
        } else {
            if(!gstNumber.isNullOrEmpty() && gstImage != null) {
                if((gstNumber?.length ?: 0) != 15) context.getString(R.string.enter_valid_gst_number)
                else null
            } else if(!gstNumber.isNullOrEmpty() || gstImage != null)
                context.getString(R.string.partial_gst_information_not_allowed)
            else null
        }
    }

    fun getBrnError(context: Context, isRequired: Boolean = false): String? = run {
        if (isRequired) {
            if (brnNumber.isNullOrEmpty() || brnName.isNullOrEmpty() || brnImage == null)
                context.getString(R.string.brn_information_required)
            else if ((brnNumber?.length ?: 0) < 16)
                context.getString(R.string.enter_valid_brn_number)
            else null
        } else {
            if (!brnNumber.isNullOrEmpty() && !brnName.isNullOrEmpty() && brnImage != null) {
                if ((brnNumber?.length ?: 0) < 16)
                    context.getString(R.string.enter_valid_brn_number)
                else null
            } else if (!brnNumber.isNullOrEmpty() || !brnName.isNullOrEmpty() || brnImage != null) {
                context.getString(R.string.partial_brn_information_not_allowed)
            } else null
        }
    }

    fun getMsmeError(context: Context, isRequired: Boolean = false): String? = run {
        if (isRequired) {
            if (udhyamName.isNullOrEmpty() || factoryNumber.isNullOrEmpty() || udhyamImage == null || factoryImage == null)
                context.getString(R.string.msme_information_required)
            else null
        } else {
            if(!udhyamName.isNullOrEmpty() && !factoryNumber.isNullOrEmpty() && udhyamImage != null && factoryImage != null) null
            else if (!udhyamName.isNullOrEmpty() || !factoryNumber.isNullOrEmpty() || udhyamImage != null || factoryImage != null)
                context.getString(R.string.partial_msme_information_not_allowed)
            else null
        }
    }

    fun getFssaiError(context: Context, isRequired: Boolean = false): String? = run {
        if (isRequired) {
            if (fssaiNumber.isNullOrEmpty() || fssaiImage == null)
                context.getString(R.string.fssai_information_required)
            else null
        } else {
            if (!fssaiNumber.isNullOrEmpty() && fssaiImage != null) null
            else if (!fssaiNumber.isNullOrEmpty() || fssaiImage != null)
                context.getString(R.string.partial_fssai_information_not_allowed)
            else null
        }
    }

    fun getBankAccountError(context: Context, isRequired: Boolean = false): String? = run {
        println(bankAccountNumber)

        if(isRequired) {
            if(bankAccountNumber.isNullOrEmpty() || bankAccountName.isNullOrEmpty() || bankIFSC.isNullOrEmpty() || accountProofImage == null)
                context.getString(R.string.bank_information_required)
            else null
        } else {
            if(!bankAccountNumber.isNullOrEmpty() && !bankAccountName.isNullOrEmpty() && !bankIFSC.isNullOrEmpty() && accountProofImage != null) null
            else if(!bankAccountNumber.isNullOrEmpty() || !bankAccountName.isNullOrEmpty() || !bankIFSC.isNullOrEmpty() || accountProofImage != null)
                context.getString(R.string.partial_bank_information_not_allowed)
            else null
        }
    }

    fun getRegisteredAddressError(context: Context): String? = run {
        if (registeredAddressLine.isNullOrEmpty() || registeredAddressCountry.isNullOrEmpty() || registeredAddressState.isNullOrEmpty() || registeredAddressCity.isNullOrEmpty() || registeredAddressPin.isNullOrEmpty())
            context.getString(R.string.registered_address_required)
        else if ((registeredAddressPin?.length ?: 0) < 6)
            context.getString(R.string.enter_valid_pin_number)
        else null
    }

    fun getRequestFields(
        countryData: MutableLiveData<ArrayList<CountryData>>,
        stateData: MutableLiveData<ArrayList<StateData>>,
        cityData: MutableLiveData<ArrayList<CityData>>,
    ): HashMap<String, RequestBody> {
        val requestMap = HashMap<String, RequestBody>()
        if(roleId != null) requestMap["role_id"] = roleId.toString().toRequestBody()
        if(lat != null) requestMap["lat"] = lat.toString().toRequestBody()
        if(long != null) requestMap["log"] = long.toString().toRequestBody()
        if(nameAsPerAadhar != null) requestMap["adhar_name"] = nameAsPerAadhar.toString().toRequestBody()
        if(aadharNumber != null) requestMap["adhar_card_no"] = aadharNumber.toString().toRequestBody()
        if(dobAsPerAadhar != null) requestMap["adhar_dob"] = dobAsPerAadhar.toString().toRequestBody()
        if(companyName != null) requestMap["company_name"] = companyName.toString().toRequestBody()
        if(companyCinNumber != null) requestMap["cin_number"] = companyCinNumber.toString().toRequestBody()
        if(panNumber != null) requestMap["pancard_no"] = panNumber.toString().toRequestBody()
        if(nameAsPerPan != null) requestMap["pancard_name"] = nameAsPerPan.toString().toRequestBody()
        if(dobAsPerPan != null) requestMap["pancard_dob"] = dobAsPerPan.toString().toRequestBody()
        if(gstNumber != null) requestMap["gst_number"] = gstNumber.toString().toRequestBody()
        if(brnNumber != null) requestMap["brn_no"] = brnNumber.toString().toRequestBody()
        if(brnName != null) requestMap["brn_name"] = brnName.toString().toRequestBody()
        if(udhyamName != null) requestMap["udhyam_reg_no"] = udhyamName.toString().toRequestBody()
        if(factoryNumber != null) requestMap["factory_licence_no"] = factoryNumber.toString().toRequestBody()
        if(fssaiNumber != null) requestMap["fssai_number"] = fssaiNumber.toString().toRequestBody()
        if(bankAccountNumber != null) requestMap["acc_number"] = bankAccountNumber.toString().toRequestBody()
        if(bankAccountName != null) requestMap["acc_holder_name"] = bankAccountName.toString().toRequestBody()
        if(bankIFSC != null) requestMap["ifsc_code"] = bankIFSC.toString().toRequestBody()
        if(registeredAddressLine != null) requestMap["address_line_one_registered"] = registeredAddressLine.toString().toRequestBody()
        if(registeredAddressPin != null) requestMap["pincode_registered"] = registeredAddressPin.toString().toRequestBody()
        requestMap["tehsil_id"] = "0".toRequestBody()
        if(registeredAddressCountry != null) {
            requestMap["country_registered"] = (countryData.value?.find {
                it.name.equals(registeredAddressCountry, true)
            }?.id ?: 0).toString().toRequestBody()
        }

        if(registeredAddressState != null) {
            requestMap["state_registered"] = (stateData.value?.find {
                it.name.equals(registeredAddressState, true)
            }?.id ?: 0).toString().toRequestBody()
        }

        if(registeredAddressCity != null) {
            requestMap["district_registered"] = (cityData.value?.find {
                it.name.equals(registeredAddressCity, true)
            }?.id ?: 0).toString().toRequestBody()
        }

        return requestMap
    }

    fun getRequestFiles(): ArrayList<MultipartBody.Part> {
        val requestBody = ArrayList<MultipartBody.Part>()
        if(aadharFrontImage != null) requestBody.add(aadharFrontImage!!.toMultiPartFile("adhar_card_front_file", "image/*"))
        if(aadharBackImage != null) requestBody.add(aadharBackImage!!.toMultiPartFile("adhar_card_back_file", "image/*"))
        if(letterHeadImage != null) requestBody.add(letterHeadImage!!.toMultiPartFile("letterhead_file", "image/*"))
        if(panImage != null) requestBody.add(panImage!!.toMultiPartFile("pancard_file", "image/*"))
        if(gstImage != null) requestBody.add(gstImage!!.toMultiPartFile("gst_file", "image/*"))
        if(udhyamImage != null) requestBody.add(udhyamImage!!.toMultiPartFile("udhyam_reg_no", "image/*"))
        if(factoryImage != null) requestBody.add(factoryImage!!.toMultiPartFile("factory_file", "image/*"))
        if(brnImage != null) requestBody.add(brnImage!!.toMultiPartFile("brn_file", "image/*"))
        if(fssaiImage != null) requestBody.add(fssaiImage!!.toMultiPartFile("fssai_file", "image/*"))
        if(accountProofImage != null) requestBody.add(accountProofImage!!.toMultiPartFile("bank_proof_file", "image/*"))

        return requestBody
    }
}