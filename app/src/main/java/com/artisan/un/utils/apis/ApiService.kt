package com.artisan.un.utils.apis

import com.artisan.un.apiModel.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

interface ApiService {
    companion object {
        const val PRIORITY_HIGH = 1
        const val PRIORITY_LOW = 3
    }

    @FormUrlEncoded
    @POST("api/registration")
    fun registration(@FieldMap map: HashMap<String, String>): Observable<BaseResponse<OTP>>

    @FormUrlEncoded
    @POST("api/verifyotp")
    fun verifyOTP(@FieldMap map: HashMap<String, String>): Observable<BaseResponse<UserResponse>>

    @FormUrlEncoded
    @POST("api/resendotp")
    fun resendOTP(@FieldMap map: HashMap<String, Any>): Observable<BaseResponse<OTP>>

    @FormUrlEncoded
    @POST("api/forgetpassword")
    fun forgotPassword(@FieldMap map: HashMap<String, String>): Observable<BaseResponse<OTP>>

    @FormUrlEncoded
    @POST("api/changepassword")
    fun changePassword(@FieldMap map: HashMap<String, Any>): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("api/login")
    fun login(@FieldMap map: HashMap<String, String>): Observable<BaseResponse<UserResponse>>

    @GET("api/getprofile")
    fun getProfile(): Observable<BaseResponse<UserResponse>>

    @GET("api/get_countries")
    fun getCountries(): Observable<BaseResponse<CountryModel>>

    @FormUrlEncoded
    @POST("api/get_state")
    fun getState(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<StateModel>>

    @FormUrlEncoded
    @POST("api/get_city")
    fun getCity(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<CityModel>>

    @FormUrlEncoded
    @POST("api/get-tehsil")
    fun getTehsil(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<TehsilModel>>

    @Multipart
    @POST("api/adddocumentandaddress")
    fun addDocumentAndAddress(
        @PartMap fields: HashMap<String, RequestBody>,
        @Part files: ArrayList<MultipartBody.Part> = ArrayList()
    ): Observable<BaseResponse<UserResponse>>

    @Multipart
    @POST("api/updatedocument")
    fun reuploadDocument(
        @PartMap fields: HashMap<String, RequestBody>,
        @Part files: ArrayList<MultipartBody.Part> = ArrayList()
    ): Observable<BaseResponse<UserResponse>>

    @GET("api/getcategory")
    fun getCategory(): Observable<BaseResponse<CategoryResponse>>

    @FormUrlEncoded
    @POST("api/getsubcategory")
    fun getSubCategory(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<CategoryResponse>>

    @FormUrlEncoded
    @POST("api/getmaterial")
    fun getMaterial(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<MaterialResponse>>

    @FormUrlEncoded
    @POST("api/gettemplate")
    fun getTemplate(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<TemplateResponse>>

    @Multipart
    @POST("api/addproduct")
    fun addProduct(
        @PartMap fields: HashMap<String, RequestBody?>,
        @Part files: ArrayList<MultipartBody.Part> = ArrayList()
    ): Observable<BaseResponse<Any>>

    @Multipart
    @POST("api/updatedraftproduct")
    fun updateDraft(
        @PartMap fields: HashMap<String, RequestBody?>,
        @Part files: ArrayList<MultipartBody.Part> = ArrayList()
    ): Observable<BaseResponse<Any>>

    @GET("api/getdraftproduct")
    fun getDraftProduct(): Observable<BaseResponse<ProductDraftResponse>>

    @FormUrlEncoded
    @POST("api/removedraftproduct")
    fun removeDraftProduct(@FieldMap body: HashMap<String, Any?>): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("api/viewproduct")
    fun getProductDetails(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<ProductDetailsModel>>

    @FormUrlEncoded
    @POST("api/getshgartisanhome")
    fun getHomeData(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<HomeDataModel>>

    @FormUrlEncoded
    @POST("api/viewartisanshgproduct")
    fun getArtisanProducts(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<ArtisanProductsModel>>

    @FormUrlEncoded
    @POST("api/viewsartisancategoryproduct")
    fun getSubCategoryProducts(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<SubCategoryWiseProductListModel>>

    @FormUrlEncoded
    @POST("api/searchshg")
    fun searchProduct(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<SearchProductModel>>

    @Multipart
    @POST("api/updateprofileimage")
    fun uploadProfileImage(@Part file: MultipartBody.Part): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("api/getnotification")
    fun getNotification(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<NotificationModel>>

    @FormUrlEncoded
    @POST("api/updateuserprofile")
    fun updateUserProfile(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("api/update-product-qty")
    fun updateProductQuantity(@Field("product_id") productId: Int, @Field("qty") quantity: Int): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("api/updatemobile")
    fun updateMobile(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("api/updateaddress")
    fun updateAddress(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("api/deleteproduct")
    fun deleteProduct(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("api/deleteprofile")
    fun deleteProfile(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("api/updatepassword")
    fun updatePassword(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("api/seller-order-details")
    fun getUserOrderDetails(@Field("order_id") order_id: Int): Observable<BaseResponse<OrderDetailsModel>>

    @FormUrlEncoded
    @POST("api/seller-order-list")
    fun getPendingOrderList(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<MyOrderModel>>

    @FormUrlEncoded
    @POST("api/seller-order-delivered-list")
    fun getDeliveredOrderList(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<MyOrderModel>>

    @FormUrlEncoded
    @POST("api/seller-order-shipped-list")
    fun getShippedOrderList(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<MyOrderModel>>

    @FormUrlEncoded
    @POST("api/seller-order-picked-list")
    fun getPickedOrderList(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<MyOrderModel>>

    @FormUrlEncoded
    @POST("api/seller-package-handover")
    fun markPackageHandover(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("api/seller-order-shipping-request")
    fun markPackageShipped(@FieldMap body: HashMap<String, Any>): Observable<BaseResponse<Any>>
}