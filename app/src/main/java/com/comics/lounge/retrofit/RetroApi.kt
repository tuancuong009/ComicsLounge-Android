package com.comics.lounge.retrofit

import com.comics.lounge.conf.UrlCollection
import com.comics.lounge.modals.*
import com.comics.lounge.modals.memberrshipmeta.MembershipMeta
import com.comics.lounge.modals.user.UserResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface RetroApi {

    companion object {
        fun create(): RetroApi {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory.create(
                                    GsonBuilder()
                                            .setLenient()
                                            .create()
                            )
                    )
                    .baseUrl(UrlCollection.BASE_URL)
                    .client(okHttpClient)
                    .build()
            return retrofit.create(RetroApi::class.java)
        }
    }

    @GET("appversion.php")
    fun checkVersion(@Query("Device") device: String, @Query("Version") version: String): Call<AppVersion>


    @POST("changepassword.php")
    fun changePassword(@Body body: HashMap<String, Any>): Call<GenericResponse>

    /**
     * { "email": "kukadia.jayesh@gmail.com" }
     */
    @POST("forget_password_otp_send.php")
    fun forgotEmailOtpSend(@Body body: HashMap<String, Any>): Call<GenericResponse>

    /**
     * { "user_id": 9020, "otp_code": 4897 }
     */
    @POST("forgetpassword_otp_verify.php")
    fun forgotEmailOtpVerify(@Body body: HashMap<String, Any>): Call<GenericResponse>

    /**
     * Both
     * { "user_id": 9020, "country_phone_code": 61, "phone_number": 8000337566, "otp_type": "both" }
     *
     * Email Only
     * { "user_id": 9020, "otp_type": "email" }
     *
     * Mobile Only
     * { "user_id": 9020, "country_phone_code": 61, "phone_number": 8000337566, "otp_type": "phone" }
     */
    @POST("otp_send.php")
    fun otpSendAll(@Body body: HashMap<String, Any>): Call<GenericResponse>

    /**
     * { "user_id": 9020, "otp_code": 5701 }
     */
    @POST("email_otp_verify.php")
    fun emailOtpVerify(@Body body: HashMap<String, Any>): Call<GenericResponse>

    @POST("otp_verify.php")
    fun otpVerify(@Body body: HashMap<String, Any>): Call<GenericResponse>

    /**
     * { "customer_id": 9020, "new_password": "tseting" }
     */
    @POST("updatepassword.php")
    fun resetPassword(@Body body: HashMap<String, Any>): Call<GenericResponse>

    /**
     * {"email":"reena.kukadia@gmail.com","password":"abcd1234",
     * "device_id":"694d3f6d-7e9a-4c25-8b29-2e4e9def3fc9","device_type":"android"}
     */
    @POST("login.php")
    fun login(@Body body: HashMap<String, Any>): Call<UserResponse>

    @POST()
    fun updateUser(@Url url: String, @Body body: HashMap<String, Any>): Call<JsonObject>

    @GET("cart/generatetoken.php")
    fun generateToken(): Call<TokenResponse>

    @GET("popupstatus.php")
    fun getStatus(@Query("user_id") userId: String): Call<PopupStatus>

    @GET("updatepopupstatus.php")
    fun getUpdateStatus(@Query("user_id") userId: String): Call<PopupStatusUpdate>

    /**
    {
    "user_id": "string",
    "membership_id": "string",
    "amount": "string",
    "coupon_id": 0,
    " paymentMethodNonce": "string",
    "user_ip_address": "string"
    }
     */
    @POST("cart/paypalpayment.php")
    fun payMembershipFree(@Body body: HashMap<String, Any>): Call<MembershipMeta>

    @GET("user.php")
    fun fetchUser(@Query("user_id") userId: String): Call<UserResponse>

    /**
     * {"user_id":"9574","log_text":"Paypal Error: Buyer canceled payment approval.",
     * "app_type":"android","app_version":"1.6"}
     */
    @POST("logs.php")
    fun insertPaymentLog(@Body body: HashMap<String, Any>): Call<UserResponse>

    /**
    {
    "user_id": "920",
    "membership_id": "1",
    "Coupon_code": "funny20"
    }
     */
    @POST("membership/checkcoupon.php")
    fun validateCoupon(@Body body: HashMap<String, Any>): Call<CouponResponse>

    @GET("servertime.php")
    fun fetchServerDate(): Call<ServerDate>

    fun paypalTrackLog(): Call<PaypalTrackLog>

    //https://new.thecomicslounge.com.au/cs2/api/membership/gift_memberships.php?user_id=9579
    //https://new.thecomicslounge.com.au/cs2/api/membership/gift_memberships.php
    @GET("membership/gift_memberships.php")
    fun fetchGiftedMemberShipList(@Query("user_id") userId: String): Call<MembershipMeta>

    @GET("membership/shared_memberships.php")
    fun fetchShareMemberShipList(@Query("user_id") userId: String): Call<MembershipMeta>

    @POST("membership/confirm_membership.php")
    fun fetchConfirmMembership(@Body body: HashMap<String, Any>): Call<GenericResponse>

    @GET("membership/my_memberships.php")
    fun myMembership(@Query("user_id") userId: String): Call<MembershipMeta>
    @GET("event/calendar.php")
    fun getEvent(@Query("user_id") userId: String): Call<JsonObject>
    @GET("event/user_saved_event.php")
    fun getSavedEvent(@Query("user_id") userId: String): Call<JsonArray>
    @GET("event/eventDetail.php")
    fun getEventDetail(@Query("user_id") userId: String, @Query("event_id") eventId: String, @Query("timestamp") timestamp: Long): Call<JsonObject>
    @GET("user.php")
    fun getUserDetail(@Query("user_id") userId: String): Call<JsonObject>
    @GET()
    fun getBookingHistory(@Url url: String, @Query("user_id") userId: String): Call<JsonObject>
    @GET("membership/memberships.php")
    fun getMbShipDes(): Call<JsonObject>
    @POST("changepassword.php")
    fun changePw(@Body body: HashMap<String, Any>): Call<JsonObject>
    @POST("misc/contact.php")
    fun sendMess(@Query("user_id") userId: Int, @Query("message") mess : String): Call<JsonObject>
    @GET()
    fun getCategories(@Url url: String): Call<JsonObject>
    @GET()
    fun getItem(@Url url: String, @Query("cat_id") cartId: Int, @Query("user_id") userId: Int): Call<JsonObject>
    @GET()
    fun getPhotoItem(@Url url: String, @Query("cat_id") cartId: Int, @Query("user_id") userId: Int, @Query("page") page: Int, @Query("limit") limit: Int): Call<JsonObject>
    @GET()
    fun getFavoriteGallery(@Url url: String, @Query("user_id") cartId: Int): Call<JsonObject>
    @GET("user.php")
    fun getUserPf(@Query("user_id") userId: Int): Call<JsonObject>
    @GET("delete_user.php")
    fun deleteAcc(@Query("user_id") userId: String): Call<JsonObject>
    @POST()
    fun addOrRemoveGallery(@Url url: String, @Body body: HashMap<String, Any>): Call<JsonObject>
    @POST()
    fun addMember(@Url url: String, @Body body: HashMap<String, Any>): Call<JsonObject>
    @GET("faq/faq.php")
    fun getQA(@Query("page") page: Int, @Query("limit") limit: Int, @Query("search") search: String): Call<JsonObject>
    @GET()
    fun getOfferItem(@Url url: String, @Query("cat_id") cartId: Int, @Query("page") page: Int, @Query("limit") limit: Int): Call<JsonObject>
    @GET("misc/enquiry.php")
    fun submitFunEvent(@Query("user_id") userId: String, @Query("date") date: String, @Query("type") type: String, @Query("guests") guest: String,
                       @Query("catering") cate: String, @Query("contact_person") name: String, @Query("contact_number") phone: String): Call<JsonObject>
}