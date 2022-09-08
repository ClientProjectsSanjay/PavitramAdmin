package com.artisan.un.apiModel

data class ProductDetailsModel(
    var productdetail: ProductDetails? = null
)

data class ProductDetails(
    var id: Int? = null,
    var price: String? = null,
    var normal_price: String? = null,
    var template_id: Int? = null,
    var user_id: String? = null,
    var material_id: String? = null,
    var localname_en: String? = null,
    var localname_kn: String? = null,
    var categoryId: Int? = null,
    var subcategoryId: Int? = null,
    var qty: String? = null,
    var length: String? = null,
    var width: String? = null,
    var height: String? = null,
    var vol: String? = null,
    var weight: String? = null,
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
    var name: String? = null,
    var template_name: String? = null,
    var description: String? = null,
    var materialname: String? = null,
    var artisanid: Int? = null,
    var artisanshgname: String? = null,
    var artisanshgtitle: String? = null,
    var des_en: String? = null,
    var des_kn: String? = null,
)

data class HomeDataModel(
    var categories: PaginationModel? = null,
    var products: ArrayList<CategoryWiseProductModel>? = null,
)

data class ArtisanProductsModel(
    var pagination: PaginationModel? = null,
    var products: ArrayList<CategoryWiseProductModel>? = null,
)

data class CategoryWiseProductModel(
    var categoryId: Int? = null,
    var categoryName: String? = null,
    var subCategories: ArrayList<SubCategoryWiseProductListModel>? = null,
)

data class SubCategoryWiseProductListModel(
    var subCategoryId: Int? = null,
    var subCategoryName: String? = null,
    var pagination: PaginationModel? = null,
    var products: ArrayList<ProductData>? = null
)

data class SearchProductModel(
    var products: SearchProductPageModel? = null
)

data class SearchProductPageModel(
    val current_page: Int? = null,
    var data: ArrayList<ProductData>? = null,
    var last_page: Int? = null
)

data class ProductData(
    var name: String? = null,
    var price: String? = null,
    var id: Int? = null,
    var qty: String? = null,
    var is_active: Int? = null,
    var image_1: String? = null,
    var template: TemplateDataModel? = null,
)

data class NotificationModel(
    var pagination: PaginationModel? = null,
    var getnotification: ArrayList<NotificationData>? = null
)

data class NotificationData(
    var id: Int? = null,
    var title: String? = null,
    var body: String? = null,
    var image: String? = null,
)

data class PaginationModel(
    var current_page: Int? = null,
    var last_page: Int? = null,
)

data class TemplateDataModel(
    var id: Int? = null,
    var name: String? = null,
)