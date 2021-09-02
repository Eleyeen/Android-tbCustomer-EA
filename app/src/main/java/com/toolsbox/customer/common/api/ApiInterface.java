package com.toolsbox.customer.common.api;


import com.google.gson.JsonObject;
import com.toolsbox.customer.common.model.api.AttachData;
import com.toolsbox.customer.common.model.api.ContractorSearchData;
import com.toolsbox.customer.common.model.api.GeneralData;
import com.toolsbox.customer.common.model.api.JobDetailData;
import com.toolsbox.customer.common.model.api.JobDetailInfo;
import com.toolsbox.customer.common.model.api.JobHistoryData;
import com.toolsbox.customer.common.model.api.JobProposalData;
import com.toolsbox.customer.common.model.api.LoginData;
import com.toolsbox.customer.common.model.api.ReviewData;
import com.toolsbox.customer.common.model.api.TwilioAccessTokenData;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.view.activity.main.jobs.JobDetailActivity;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    String version = "v8";

    @POST("/api/" + version +"/customer/register")
    Call<LoginData> signup(@Body RequestBody bean);

    @POST("/api/" + version +"/customer/login")
    Call<LoginData> login(@Body RequestBody bean);

    @POST("/api/" + version +"/customer/loginwithtoken")
    Call<LoginData> loginWithToken(@Body RequestBody bean);

    @GET("/api/" + version +"/customer/logout")
    Call<GeneralData> logout(@Query("token") String token);

    @Multipart
    @POST("/api/" + version + "/jobs/attach")
    Call<AttachData> uploadAttachment(@Part MultipartBody.Part file);

    @Multipart
    @POST("/api/" + version +"/customer/update")
    Call<LoginData> updateProfile( @Query("token") String token, @Part MultipartBody.Part file, @Part("name") RequestBody name, @Part("email") RequestBody email,
                                    @Part("phone") RequestBody phone);

    @Multipart
    @POST("/api/" + version +"/jobs/post")
    Call<GeneralData> postJob(@Part("name") RequestBody name, @Part("industry") RequestBody industry,
                              @Part("area") RequestBody area, @Part("area_lati") RequestBody area_lati,
                              @Part("area_longi") RequestBody area_longi, @Part("description") RequestBody description,
                              @Part("poster_type") RequestBody poster_type, @Part("poster_id") RequestBody poster_id,
                              @Part("poster_name") RequestBody poster_name, @Part("availability_dates") RequestBody availability_dates,
                              @Part("attachment") RequestBody attachment);

    @Multipart
    @POST("/api/" + version +"/jobs/update")
    Call<GeneralData> editJob(@Part("job_id") RequestBody job_id, @Part("token") RequestBody token,
                              @Part("name") RequestBody name, @Part("industry") RequestBody industry,
                              @Part("area") RequestBody area, @Part("area_lati") RequestBody area_lati,
                              @Part("area_longi") RequestBody area_longi, @Part("description") RequestBody description,
                              @Part("availability_dates") RequestBody availability_dates, @Part("attachment") RequestBody attachment);

    @Multipart
    @POST("/api/" + version +"/jobs/hire")
    Call<GeneralData> hireJob(@Part("name") RequestBody name, @Part("industry") RequestBody industry,
                              @Part("area") RequestBody area, @Part("area_lati") RequestBody area_lati,
                              @Part("area_longi") RequestBody area_longi, @Part("description") RequestBody description,
                              @Part("poster_type") RequestBody poster_type, @Part("poster_id") RequestBody poster_id,
                              @Part("poster_name") RequestBody poster_name, @Part("availability_dates") RequestBody availability_dates,
                              @Part("contractor_id") RequestBody contractor_id, @Part("attachment") RequestBody attachment);

    @GET("/api/" + version +"/contractor/search")
    Call<ContractorSearchData> searchContractor(@Query("page") int page, @Query("per_page") int per_page, @Query("industry")int industry,
                                               @Query("lati") String lati, @Query("longi") String longi, @Query("radius") int radius);

    @GET("/api/" + version +"/jobs/myjob/customer/all")
    Call<JobHistoryData> fetchAllJobHistory(@Query("page") int page, @Query("per_page") int per_page, @Query("token")String token);

    @GET("/api/" + version +"/jobs/myjob/customer")
    Call<JobHistoryData> fetchJobHistory(@Query("page") int page, @Query("per_page") int per_page, @Query("status") int status, @Query("token")String token);

    @GET("/api/" + version +"/jobs/bidders")
    Call<JobProposalData> fetchProposal(@Query("job_id") int job_id, @Query("type") int type, @Query("contractor_id") int contractor_id);

    @GET("/api/" + version +"/review/history")
    Call<ReviewData> fetchReviews(@Query("page") int page, @Query("per_page") int per_page, @Query("contractor_id") int contractor_id);

    @POST("/api/" + version +"/review/provide")
    Call<GeneralData> addReview(@Body RequestBody bean);

    @POST("/api/" + version +"/jobs/remove/customer")
    Call<GeneralData> removeJob(@Body RequestBody bean, @Query("token") String token);

    @POST("/api/" + version +"/jobs/complete")
    Call<GeneralData> completeJob(@Body RequestBody bean);

    @POST("/api/" + version +"/jobs/bidders/award")
    Call<GeneralData> acceptBidder(@Body RequestBody bean);

    @POST("/api/" + version +"/jobs/bidders/reject")
    Call<GeneralData> declineBidder(@Body RequestBody bean);

    @GET("/api/" + version +"/customer/payment/getcards")
    Call<JsonObject> fetchCard(@Query("token") String token);

    @POST("/api/" + version +"/customer/payment/add")
    Call<GeneralData> addCreditCard(@Body RequestBody bean, @Query("token") String token);

    @POST("/api/" + version +"/customer/payment/defaultcard")
    Call<GeneralData> updateDefaultCreditCard(@Body RequestBody bean, @Query("token") String token);

    @POST("/api/" + version +"/customer/chat/access_token")
    Call<TwilioAccessTokenData> fetchTwilioAccessToken(@Body RequestBody bean);

    @GET("/api/" + version + "/jobs/details")
    Call<JobDetailData> fetchJobDetails(@Query("job_id") int jobId);

    @POST("/api/" + version +"/customer/phone_verify/request")
    Call<GeneralData> customerPhoneVerify(@Body RequestBody bean, @Query("token") String token);

    @POST("/api/" + version +"/customer/phone_verify/confirm")
    Call<LoginData> customerPhoneVerifyConfirm(@Body RequestBody bean, @Query("token") String token);

    @POST("/api/" + version +"/customer/forgot/email_verify/request")
    Call<GeneralData> forgotEmailVerify(@Body RequestBody bean);

    @POST("/api/" + version +"/customer/forgot/email_verify/confirm")
    Call<LoginData> forgotEmailVerifyConfirm(@Body RequestBody bean);

    @POST("/api/" + version +"/customer/reset_password_confirm")
    Call<GeneralData> updatePassword(@Body RequestBody bean, @Query("token") String token);

    @POST("/api/" + version +"/customer/forgot/phone_verify/request")
    Call<GeneralData> forgotPhoneVerify(@Body RequestBody bean);

    @POST("/api/" + version +"/customer/forgot/phone_verify/confirm")
    Call<LoginData> forgotPhoneVerifyConfirm(@Body RequestBody bean);
}