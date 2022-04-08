package com.artisan.un.ui.product.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artisan.un.baseClasses.BaseViewModel
import com.artisan.un.utils.ApplicationData
import com.artisan.un.utils.ProductMediaType
import com.artisan.un.utils.apis.*
import com.artisan.un.utils.toMultiPartFile
import com.artisan.un.utils.validateNewImage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProductAddDescViewModel(private val apis: ApiService) : BaseViewModel() {
    private val _requestStatus = MutableLiveData<Boolean>()
    val requestStatus: LiveData<Boolean> = _requestStatus

    fun doSubmitForm(isDraft: Int, productId: Int?) {
        ApplicationData.productBundle?.apply {
            val fields = HashMap<String, RequestBody?>()
            val files = ArrayList<MultipartBody.Part>()

            fields["categoryId"] = categoryId.toString().toRequestBody()
            fields["subcategoryId"] = subCategoryId.toString().toRequestBody()
            fields["material_id"] = materialId.toString().toRequestBody()
            fields["template_id"] = templateId.toString().toRequestBody()
            fields["price"] = price.toString().toRequestBody()
            fields["normal_price"] = normal_price.toString().toRequestBody()
            fields["qty"] = quantity.toString().toRequestBody()
            fields["localname_en"] = (localNameEn ?: "").toRequestBody()
            fields["localname_kn"] = (localNameKn ?: "").toRequestBody()

            fields["video_url"] = (youtubeUrl ?: "").toRequestBody()
            fields["is_draft"] = isDraft.toString().toRequestBody()
            fields["des_en"] = (descriptionEn ?: "").toRequestBody()
            fields["des_kn"] = (descriptionKn ?: "").toRequestBody()

            lengthValue?.let { fields["length"] = it.toRequestBody() }
            lengthUnit?.let { fields["length_unit"] = it.toRequestBody() }
            widthValue?.let { fields["width"] = it.toRequestBody() }
            widthUnit?.let { fields["width_unit"] = it.toRequestBody() }
            heightValue?.let { fields["height"] = it.toRequestBody() }
            heightUnit?.let { fields["height_unit"] = it.toRequestBody() }
            weightValue?.let { fields["weight"] = it.toRequestBody() }
            weightUnit?.let { fields["weight_unit"] = it.toRequestBody() }
            volumeValue?.let { fields["vol"] = it.toRequestBody() }
            volumeUnit?.let { fields["vol_unit"] = it.toRequestBody() }

            image1.validateNewImage("image_1", fields, files)
            image2.validateNewImage("image_2", fields, files)
            image3.validateNewImage("image_3", fields, files)
            image4.validateNewImage("image_4", fields, files)
            image5.validateNewImage("image_5", fields, files)

            productId?.let { id ->
                fields["productId"] = id.toString().toRequestBody()
                fields["is_active"] = 1.toString().toRequestBody()

                requestData(apis.updateDraft(fields, files), { _requestStatus.postValue(it.status) }, priority = ApiService.PRIORITY_HIGH)
            } ?: requestData(apis.addProduct(fields, files), { _requestStatus.postValue(it.status) }, priority = ApiService.PRIORITY_HIGH)
        }
    }
}