package com.toolsbox.customer.common.utils;

import android.content.Context;

import com.google.gson.JsonObject;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.api.ApiClient;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.ContractorInfo;
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

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiUtils {
    private static String TAG = "ApiUtils";

    public static void doSignup(final Context context, final int signupType, final String email,
                                final String phone, final String password,
                                final String name, final String image_url, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            switch (signupType){
                case Constant.SIGN_TYPE_EMAIL:
                    jsonObject.put("name", name);
                    jsonObject.put("email", email);
                    jsonObject.put("password", password);
                    break;
                case Constant.SIGN_TYPE_SOCIAL:
                    jsonObject.put("email", email);
                    jsonObject.put("name", name);
                    jsonObject.put("image_url", image_url);
            }

            jsonObject.put("signup_type", signupType);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<LoginData> call = ApiClient.getApiClient().signup(body);
            call.enqueue(new Callback<LoginData>() {
                @Override
                public void onResponse(Call<LoginData> call, Response<LoginData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<LoginData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLogin(final Context context, final int loginType, final String email, final String phone, final String password, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            switch (loginType){
                case Constant.SIGN_TYPE_EMAIL:
                    jsonObject.put("email", email);
                    break;
                case Constant.SIGN_TYPE_PHONE:
                    jsonObject.put("phone", phone);
                    break;
            }
            jsonObject.put("login_type", loginType);
            jsonObject.put("password", password);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<LoginData> call = ApiClient.getApiClient().login(body);
            call.enqueue(new Callback<LoginData>() {
                @Override
                public void onResponse(Call<LoginData> call, Response<LoginData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<LoginData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoginWithToken(final Context context, final String token,  final String fcmToken, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", token);
            jsonObject.put("fcm_token", fcmToken);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<LoginData> call = ApiClient.getApiClient().loginWithToken(body);
            call.enqueue(new Callback<LoginData>() {
                @Override
                public void onResponse(Call<LoginData> call, Response<LoginData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<LoginData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logout(final Context context, final String token, final Notify notify) {
        Call<GeneralData> call = ApiClient.getApiClient().logout(token);
        call.enqueue(new Callback<GeneralData>() {
            @Override
            public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {
                if (response.isSuccessful()) {
                    if (notify != null)
                        notify.onSuccess(response.body());

                } else {
                    if (notify != null)
                        notify.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Call<GeneralData> call, Throwable t) {
                if (notify != null)
                    notify.onFail();
            }
        });
    }

    public static void updateProfile(final Context context, final String name, final String email, final String phone, final File photo, final String token, final Notify notify) {

        try {
            MultipartBody.Part fileToUpload = null;
            if (photo != null){
                RequestBody mFile = RequestBody.create(null, photo);
                fileToUpload = MultipartBody.Part.createFormData("photo", photo.getName(), mFile);
            }
            RequestBody requestName = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody requestEmail = RequestBody.create(MediaType.parse("text/plain"), email);
            RequestBody requestPhone = RequestBody.create(MediaType.parse("text/plain"), phone);

            Call<LoginData> call = ApiClient.getApiClient().updateProfile(token, fileToUpload, requestName, requestEmail, requestPhone);

            call.enqueue(new Callback<LoginData>() {
                @Override
                public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());

                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }

                @Override
                public void onFailure(Call<LoginData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uploadAttachment(final Context context,  final File photo, final Notify notify) {

        try {
            MultipartBody.Part fileToUpload = null;
            if (photo != null){
                RequestBody mFile = RequestBody.create(null, photo);
                fileToUpload = MultipartBody.Part.createFormData("photo", photo.getName(), mFile);
            }

            Call<AttachData> call = ApiClient.getApiClient().uploadAttachment(fileToUpload);

            call.enqueue(new Callback<AttachData>() {
                @Override
                public void onResponse(Call<AttachData> call, Response<AttachData> response) {
                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());

                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }

                @Override
                public void onFailure(Call<AttachData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void postJob(final Context context, final String name, final int industry, final String area, final String area_lati,
                               final String area_longi, final String description, final int poster_type, final int poster_id,
                               final String poster_name, final String availability_dates, final String attachment, final Notify notify) {
        try {
            RequestBody requestName = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody requestIndustry = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(industry));
            RequestBody requestArea = RequestBody.create(MediaType.parse("text/plain"), area);
            RequestBody requestAreaLati = RequestBody.create(MediaType.parse("text/plain"), area_lati);
            RequestBody requestAreaLongi = RequestBody.create(MediaType.parse("text/plain"), area_longi);
            RequestBody requestDescription = RequestBody.create(MediaType.parse("text/plain"), description);
            RequestBody requestPosterType = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(poster_type));
            RequestBody requestPosterId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(poster_id));
            RequestBody requestPosterName = RequestBody.create(MediaType.parse("text/plain"), poster_name);
            RequestBody requestAttachment = RequestBody.create(MediaType.parse("text/plain"), attachment);
            RequestBody requestAvailability = RequestBody.create(MediaType.parse("text/plain"), availability_dates);

            Call<GeneralData> call = ApiClient.getApiClient().postJob(requestName, requestIndustry, requestArea, requestAreaLati,
                    requestAreaLongi, requestDescription, requestPosterType, requestPosterId, requestPosterName, requestAvailability, requestAttachment);

            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {
                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());

                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }

                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void editJob(final Context context, final int job_id, final String name, final int industry, final String area, final String area_lati,
                               final String area_longi, final String description,  final String availability_dates, final String attachment, final String token,  final Notify notify) {
        try {
            RequestBody requestJobId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(job_id));
            RequestBody requestName = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody requestIndustry = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(industry));
            RequestBody requestArea = RequestBody.create(MediaType.parse("text/plain"), area);
            RequestBody requestAreaLati = RequestBody.create(MediaType.parse("text/plain"), area_lati);
            RequestBody requestAreaLongi = RequestBody.create(MediaType.parse("text/plain"), area_longi);
            RequestBody requestDescription = RequestBody.create(MediaType.parse("text/plain"), description);
            RequestBody requestAvailability = RequestBody.create(MediaType.parse("text/plain"), availability_dates);
            RequestBody requestAttachment = RequestBody.create(MediaType.parse("text/plain"), attachment);
            RequestBody requestToken = RequestBody.create(MediaType.parse("text/plain"), token);

            Call<GeneralData> call = ApiClient.getApiClient().editJob(requestJobId, requestToken,  requestName, requestIndustry,
                    requestArea, requestAreaLati, requestAreaLongi, requestDescription, requestAvailability, requestAttachment);

            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {
                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());

                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }

                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hireJob(final Context context, final String name, final int industry, final String area, final String area_lati,
                               final String area_longi, final String description, final int poster_type, final int poster_id,
                               final String poster_name,  final String availability_dates, final String attachment, final int contractor_id, final Notify notify) {
        try {
            RequestBody requestName = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody requestIndustry = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(industry));
            RequestBody requestArea = RequestBody.create(MediaType.parse("text/plain"), area);
            RequestBody requestAreaLati = RequestBody.create(MediaType.parse("text/plain"), area_lati);
            RequestBody requestAreaLongi = RequestBody.create(MediaType.parse("text/plain"), area_longi);
            RequestBody requestDescription = RequestBody.create(MediaType.parse("text/plain"), description);
            RequestBody requestPosterType = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(poster_type));
            RequestBody requestPosterId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(poster_id));
            RequestBody requestPosterName = RequestBody.create(MediaType.parse("text/plain"), poster_name);
            RequestBody requestAvailability = RequestBody.create(MediaType.parse("text/plain"), availability_dates);
            RequestBody requestAttachment = RequestBody.create(MediaType.parse("text/plain"), attachment);
            RequestBody requestContractorId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(contractor_id));

            Call<GeneralData> call = ApiClient.getApiClient().hireJob(requestName, requestIndustry, requestArea, requestAreaLati,
                    requestAreaLongi, requestDescription, requestPosterType, requestPosterId, requestPosterName, requestAvailability, requestContractorId, requestAttachment);

            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {
                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());

                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }

                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchContractor(final Context context, final int page, final int per_page,  final int industry, final String lati,
                                        final String longi, final int radius, final Notify notify) {
        Call<ContractorSearchData> call = ApiClient.getApiClient().searchContractor(page, per_page, industry, lati, longi, radius);
        call.enqueue(new Callback<ContractorSearchData>() {
            @Override
            public void onResponse(Call<ContractorSearchData> call, Response<ContractorSearchData> response) {
                if (response.isSuccessful()) {
                    if (notify != null)
                        notify.onSuccess(response.body());

                } else {
                    if (notify != null)
                        notify.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Call<ContractorSearchData> call, Throwable t) {
                if (notify != null)
                    notify.onFail();
            }
        });
    }

    public static void fetchJobHistory(final Context context, final int page, final int per_page, final int status, final String token, final Notify notify) {
        Call<JobHistoryData> call = ApiClient.getApiClient().fetchJobHistory(page, per_page, status, token);
        call.enqueue(new Callback<JobHistoryData>() {
            @Override
            public void onResponse(Call<JobHistoryData> call, Response<JobHistoryData> response) {
                if (response.isSuccessful()) {
                    if (notify != null)
                        notify.onSuccess(response.body());

                } else {
                    if (notify != null)
                        notify.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Call<JobHistoryData> call, Throwable t) {
                if (notify != null)
                    notify.onFail();
            }
        });
    }

    public static void fetchAllJobHistory(final Context context, final int page, final int per_page, final String token, final Notify notify) {
        Call<JobHistoryData> call = ApiClient.getApiClient().fetchAllJobHistory(page, per_page, token);
        call.enqueue(new Callback<JobHistoryData>() {
            @Override
            public void onResponse(Call<JobHistoryData> call, Response<JobHistoryData> response) {
                if (response.isSuccessful()) {
                    if (notify != null)
                        notify.onSuccess(response.body());

                } else {
                    if (notify != null)
                        notify.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Call<JobHistoryData> call, Throwable t) {
                if (notify != null)
                    notify.onFail();
            }
        });
    }

    public static void fetchProposal(final Context context, final int job_id, final int type, final int contractor_id, final Notify notify){
        Call<JobProposalData> call = ApiClient.getApiClient().fetchProposal(job_id, type, contractor_id);
        call.enqueue(new Callback<JobProposalData>() {
            @Override
            public void onResponse(Call<JobProposalData> call, Response<JobProposalData> response) {
                if (response.isSuccessful()) {
                    if (notify != null)
                        notify.onSuccess(response.body());

                } else {
                    if (notify != null)
                        notify.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Call<JobProposalData> call, Throwable t) {
                if (notify != null)
                    notify.onFail();
            }
        });
    }


    public static void fetchReiew(final Context context, final int page, final int per_page, final int contractor_id, final Notify notify) {
        Call<ReviewData> call = ApiClient.getApiClient().fetchReviews(page, per_page, contractor_id);
        call.enqueue(new Callback<ReviewData>() {
            @Override
            public void onResponse(Call<ReviewData> call, Response<ReviewData> response) {
                if (response.isSuccessful()) {
                    if (notify != null)
                        notify.onSuccess(response.body());

                } else {
                    if (notify != null)
                        notify.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Call<ReviewData> call, Throwable t) {
                if (notify != null)
                    notify.onFail();
            }
        });
    }


    public static void addReview(final Context context, final int contractor_id, final int  provider_type, final int provider_id,
                                 final float overall_rate, final float quality_rate, final float time_rate, final String comment, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("contractor_id", contractor_id);
            jsonObject.put("provider_type", provider_type);
            jsonObject.put("provider_id", provider_id);
            jsonObject.put("overall_rate", overall_rate);
            jsonObject.put("quality_rate", quality_rate);
            jsonObject.put("time_rate", time_rate);
            jsonObject.put("comment", comment);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<GeneralData> call = ApiClient.getApiClient().addReview(body);
            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fetchJobDetails(final Context context, final int job_id, final Notify notify) {
        Call<JobDetailData> call = ApiClient.getApiClient().fetchJobDetails(job_id);
        call.enqueue(new Callback<JobDetailData>() {
            @Override
            public void onResponse(Call<JobDetailData> call, Response<JobDetailData> response) {

                if (response.isSuccessful()) {
                    if (notify != null)
                        notify.onSuccess(response.body());
                } else {
                    if (notify != null)
                        notify.onSuccess(null);
                }
            }
            @Override
            public void onFailure(Call<JobDetailData> call, Throwable t) {
                if (notify != null)
                    notify.onFail();
            }
        });
    }

    public static void removeJob(final Context context, final int job_id,  final String token, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("job_id", job_id);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<GeneralData> call = ApiClient.getApiClient().removeJob(body, token);
            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void completeJob(final Context context, final int poster_type, final int job_id, final String token, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("poster_type", poster_type);
            jsonObject.put("job_id", job_id);
            jsonObject.put("token", token);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<GeneralData> call = ApiClient.getApiClient().completeJob(body);
            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void acceptBidder(final Context context, final int bid_id, final String token, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bid_id", bid_id);
            jsonObject.put("token", token);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<GeneralData> call = ApiClient.getApiClient().acceptBidder(body);
            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void declineBidder(final Context context, final int bid_id, final String token, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bid_id", bid_id);
            jsonObject.put("token", token);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<GeneralData> call = ApiClient.getApiClient().declineBidder(body);
            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void fetchCards(final Context context, final String token, final Notify notify) {
        Call<JsonObject> call = ApiClient.getApiClient().fetchCard(token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (notify != null)
                        notify.onSuccess(response.body());

                } else {
                    if (notify != null)
                        notify.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (notify != null)
                    notify.onFail();
            }
        });
    }

    public static void addCreditCard(final Context context, final String card_token, final String token, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("card_token", card_token);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<GeneralData> call = ApiClient.getApiClient().addCreditCard(body, token);
            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void updateDefaultCreditCard(final Context context, final String default_cc, final String token, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("default_cc", default_cc);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<GeneralData> call = ApiClient.getApiClient().updateDefaultCreditCard(body, token);
            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void customerPhoneVerify(final Context context, final String phone, final String token, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<GeneralData> call = ApiClient.getApiClient().customerPhoneVerify(body, token);
            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void customerPhoneVerifyConfirm(final Context context, final String phone, final String code,  final String token, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
            jsonObject.put("code", code);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<LoginData> call = ApiClient.getApiClient().customerPhoneVerifyConfirm(body, token);
            call.enqueue(new Callback<LoginData>() {
                @Override
                public void onResponse(Call<LoginData> call, Response<LoginData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<LoginData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void forgotEmailVerify(final Context context, final String email, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<GeneralData> call = ApiClient.getApiClient().forgotEmailVerify(body);
            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void forgotPhoneVerify(final Context context, final String phone, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<GeneralData> call = ApiClient.getApiClient().forgotPhoneVerify(body);
            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void forgotEmailVerifyConfirm(final Context context, final String email, final String code,  final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("code", code);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<LoginData> call = ApiClient.getApiClient().forgotEmailVerifyConfirm(body);
            call.enqueue(new Callback<LoginData>() {
                @Override
                public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<LoginData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void forgotPhoneVerifyConfirm(final Context context, final String phone, final String code,  final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
            jsonObject.put("code", code);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<LoginData> call = ApiClient.getApiClient().forgotPhoneVerifyConfirm(body);
            call.enqueue(new Callback<LoginData>() {
                @Override
                public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<LoginData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updatePassword(final Context context, final String password, final String token,  final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", password);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<GeneralData> call = ApiClient.getApiClient().updatePassword(body, token);
            call.enqueue(new Callback<GeneralData>() {
                @Override
                public void onResponse(Call<GeneralData> call, Response<GeneralData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<GeneralData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Twilio Access Token
    public static void fetchTwilioAccessToken(final Context context, final String identity, final String device, final Notify notify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("identity", identity);
            jsonObject.put("device", device);
            jsonObject.put("flag", 0);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            Call<TwilioAccessTokenData> call = ApiClient.getApiClient().fetchTwilioAccessToken(body);
            call.enqueue(new Callback<TwilioAccessTokenData>() {
                @Override
                public void onResponse(Call<TwilioAccessTokenData> call, Response<TwilioAccessTokenData> response) {

                    if (response.isSuccessful()) {
                        if (notify != null)
                            notify.onSuccess(response.body());
                    } else {
                        if (notify != null)
                            notify.onSuccess(null);
                    }
                }
                @Override
                public void onFailure(Call<TwilioAccessTokenData> call, Throwable t) {
                    if (notify != null)
                        notify.onFail();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
