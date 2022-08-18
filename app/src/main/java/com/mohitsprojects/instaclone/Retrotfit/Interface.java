package com.mohitsprojects.instaclone.Retrotfit;

import static com.mohitsprojects.instaclone.Retrotfit.RetrofitClient.BASE_URL;

import androidx.annotation.Nullable;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Interface {
    @FormUrlEncoded
    @POST(RetrofitClient.URL_STATUS_LIST)
    Call<ResponseBody> POSTBidList(
            @Field("user_id") String user_id,
            @Field("incidence_id") String incidence_id
    );

    @Multipart
    @POST(BASE_URL + "/app_create_Incidence_status")
    Call<ResponseBody> upload(
            @Part("incidence_id") @Nullable RequestBody incidence_id,
            @Part("title") @Nullable RequestBody title,
            @Part("description") @Nullable RequestBody description,
            @Part("status") @Nullable RequestBody status,
            @Part("user_id") @Nullable RequestBody user_id,
            @Part @Nullable MultipartBody.Part file);

}

