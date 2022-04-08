package com.artisan.un.utils.apis

import java.io.Serializable

data class BaseResponse<T>(
    val status: Boolean = false,
    val statusCode: Int = 0,
    val message: String = "",
    val data: T? = null
)

data class UserRole(val id: String? = "", val role_name: String? = "")

data class OTP(val otp: Int? = null)

data class UserResponse(
    val otp: Int? = null,
    val user: UserInfo? = null,
    val address: UserAddressResponse? = null,
    val is_otp_verified: Int? = null,
    val mobile: String? = null
): Serializable

data class UserInfo(
    var name: String? = "",
    var id: String? = "",
    var email: String? = "",
    var mobile: String? = "",
    var email_verified_at: String? = "",
    var profileImage: String? = "",
    var role_id: Int? = null,
    var title: String? = "",
    var district: String? = "",
    var language: String? = "",
    var api_token: String? = null,
    var isActive: Int = 0,
    var is_document_added: Int = 0,
    var is_document_verified: Int = 0,
    var is_address_added: Int? = null,
    var is_adhar_verify: Int? = null,
    var is_pan_verify: Int? = null,
    var is_brn_verify: Int? = null,
    var is_adhar_added: Int? = null,
    var is_pan_added: Int? = null,
    var is_brn_added: Int? = null,
    var adhar_card_front_file: String? = null,
    var pancard_file: String? = null,
    var brn_file: String? = null,
    var is_sanjeevini: Int,
    var sanjeevini_name: String?,
    var sanjeevini_gplf: String?,
    var sanjeevini_mis: String? = null,
    var lat: Double,
    var log: Double,
): Serializable

data class UserAddressResponse(
    var personal: UserAddress? = null,
    var registered: UserAddress? = null
): Serializable

data class UserAddress(
    var id: Int? = null,
    var address_line_one: String? = "",
    var address_line_two: String? = "",
    var pincode: String? = null,
    var countryId: Int? = null,
    var stateId: Int? = null,
    var districtId: Int? = null,
    var tehsil_id: Int? = null,
    var country: String? = "",
    var state: String? = "",
    var district: String? = "",
    var tehsil: String? = "",
    var lat: Double,
    var log: Double,
): Serializable

data class CountryModel(
    var country: ArrayList<CountryData>? = null
)

data class StateModel(
    var states: ArrayList<StateData>? = null
)

data class CityModel(
    var district: ArrayList<CityData>? = null
)

data class TehsilModel(
    var tehsil: ArrayList<TehsilData>? = null
)

data class CountryData(
    var id: Int? = null,
    var sortname: String? = null,
    var name: String? = null,
    var phonecode: Int? = null,
)

data class StateData(
    var id: Int? = null,
    var name: String? = null,
    var country_id: Int? = null,
)

data class CityData(
    var id: Int? = null,
    var name: String? = null,
    var state_id: Int? = null,
)

data class TehsilData(
    var id: Int? = null,
    var name: String? = null,
    var dist_id: Int? = null,
)

data class CategoryResponse(
    var category: ArrayList<CategoryData>? = null,
)

data class CategoryData(
    var id: String? = "",
    var name: String? = "",
    var image: String? = "",
)

data class MaterialResponse(
    var material: ArrayList<MaterialData>? = null,
)

data class MaterialData(
    var id: String? = "",
    var name: String? = "",
    var image: String? = "",
)

data class TemplateData(
    var id: String? = "",
    var name: String? = "",
    var description_en: String? = "",
    var description_kn: String? = "",
    var length: String? = "",
    var width: String? = "",
    var height: String? = "",
    var weight: String? = "",
    var volume: String? = "",
)

data class TemplateResponse(
    var template: ArrayList<TemplateData>? = null
)

data class ProductDraftResponse(
    var draftproduct: DraftInfo? = null
)

data class DraftInfo(
    var id: Int? = null,
    var material_id: Int? = null,
    var categoryId: Int? = null,
    var subcategoryId: Int? = null,
    var price: Int? = null,
    var qty: Int? = null,
    var localname_en: String? = null,
    var localname_kn: String? = null,
    var length: Float? = null,
    var width: Float? = null,
    var height: Float? = null,
    var weight: Float? = null,
    var vol: Float? = null,
    var length_unit: String? = null,
    var width_unit: String? = null,
    var height_unit: String? = null,
    var weight_unit: String? = null,
    var vol_unit: String? = null,
    var image_1: String? = null,
    var image_2: String? = null,
    var image_3: String? = null,
    var image_4: String? = null,
    var image_5: String? = null,
    var video_url: String? = null,
    var des_en: String? = null,
    var des_kn: String? = null,
    var template_id: Int? = null,
): Serializable
