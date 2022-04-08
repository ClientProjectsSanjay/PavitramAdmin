package com.artisan.un.apiModel

import com.artisan.un.BuildConfig
import com.artisan.un.utils.apis.DraftInfo
import java.io.Serializable

class ProductBundle: Serializable{
    var productId: Int? = null
    var name: String? = null
    var price: String? = null
    var normal_price: String? = null
    var quantity: String? = null
    var localNameEn: String? = null
    var localNameKn: String? = null
    var length: String? = null
    var width: String? = null
    var height: String? = null
    var weight: String? = null
    var volume: String? = null
    var descriptionEn: String? = null
    var descriptionKn: String? = null
    var categoryId: Int? = null
    var subCategoryId: Int? = null
    var materialId: Int? = null
    var templateId: Int? = null
    var lengthValue: String? = null
    var lengthUnit: String? = null
    var widthValue: String? = null
    var widthUnit: String? = null
    var heightValue: String? = null
    var heightUnit: String? = null
    var weightValue: String? = null
    var weightUnit: String? = null
    var volumeValue: String? = null
    var volumeUnit: String? = null
    var image1: String? = null
    var image2: String? = null
    var image3: String? = null
    var image4: String? = null
    var image5: String? = null
    var youtubeUrl: String? = null

    fun parseData(draftInfo: DraftInfo) = apply {
        productId = draftInfo.id
        categoryId = draftInfo.categoryId
        subCategoryId = draftInfo.subcategoryId
        materialId = draftInfo.material_id
        templateId = draftInfo.template_id
        price = draftInfo.price?.toString()
        quantity = draftInfo.qty?.toString()
        localNameEn = draftInfo.localname_en
        localNameKn = draftInfo.localname_kn

        lengthValue = draftInfo.length?.toString()
        widthValue = draftInfo.width?.toString()
        heightValue = draftInfo.height?.toString()
        weightValue = draftInfo.weight?.toString()
        volumeValue = draftInfo.vol?.toString()

        lengthUnit = draftInfo.length_unit
        widthUnit = draftInfo.width_unit
        heightUnit = draftInfo.height_unit
        weightUnit = draftInfo.weight_unit
        volumeUnit = draftInfo.vol_unit

        image1 = draftInfo.image_1?.let { BuildConfig.BASE_API_URL + it }
        image2 = draftInfo.image_2?.let { BuildConfig.BASE_API_URL + it }
        image3 = draftInfo.image_3?.let { BuildConfig.BASE_API_URL + it }
        image4 = draftInfo.image_4?.let { BuildConfig.BASE_API_URL + it }
        image5 = draftInfo.image_5?.let { BuildConfig.BASE_API_URL + it }

        youtubeUrl = draftInfo.video_url

        descriptionEn = draftInfo.des_en
        descriptionKn = draftInfo.des_kn
    }

    fun parseData(productDetails: ProductDetails?) = apply {
        productId = productDetails?.id
        categoryId = productDetails?.categoryId
        subCategoryId = productDetails?.subcategoryId
        materialId = productDetails?.material_id?.toInt()
        templateId = productDetails?.template_id
        price = productDetails?.price
        normal_price = productDetails?.normal_price
        quantity = productDetails?.qty
        localNameEn = productDetails?.localname_en
        localNameKn = productDetails?.localname_kn

        lengthValue = productDetails?.length
        widthValue = productDetails?.width
        heightValue = productDetails?.height
        weightValue = productDetails?.weight
        volumeValue = productDetails?.vol

        lengthUnit = productDetails?.length_unit
        widthUnit = productDetails?.width_unit
        heightUnit = productDetails?.height_unit
        weightUnit = productDetails?.weight_unit
        volumeUnit = productDetails?.vol_unit

        image1 = productDetails?.image_1?.let { BuildConfig.BASE_API_URL + it }
        image2 = productDetails?.image_2?.let { BuildConfig.BASE_API_URL + it }
        image3 = productDetails?.image_3?.let { BuildConfig.BASE_API_URL + it }
        image4 = productDetails?.image_4?.let { BuildConfig.BASE_API_URL + it }
        image5 = productDetails?.image_5?.let { BuildConfig.BASE_API_URL + it }

        youtubeUrl = productDetails?.video_url

        descriptionEn = productDetails?.des_en
        descriptionKn = productDetails?.des_kn
    }
}