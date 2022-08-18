package com.mohitsprojects.instaclone.AndroidConstants;

import static com.mohitsprojects.instaclone.Retrotfit.RetrofitClient.BASE_URL;
import static com.mohitsprojects.instaclone.Retrotfit.RetrofitClient.getRetrofit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.mohitsprojects.instaclone.Retrotfit.Interface;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadImageToApi {
    Context context;
    Activity activity;
    ProgressDialog progressDialog;
    String path;
    String User_Id;
    String username;
    EditText et_desc;
    String Status;
    String incidenceId;

    public UploadImageToApi(Context context, ProgressDialog progressDialog, String path, String user_Id, String username, EditText et_desc, String status, String incidenceId) {
        this.context = context;
        activity = (Activity)context;
        this.progressDialog = progressDialog;
        this.path = path;
        User_Id = user_Id;
        this.username = username;
        this.et_desc = et_desc;
        Status = status;
        this.incidenceId = incidenceId;
    }

    public void uploadFile(String fileUri, String filename) {

        progressDialog.show();
        Interface service = getRetrofit(BASE_URL).create(Interface.class);
        MultipartBody.Part body = null;
        if (!path.equalsIgnoreCase("")) {
            File file = new File(fileUri);
            if(path.endsWith(".jpg") || path.endsWith(".jpeg")){
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            }
            else{
                RequestBody requestFile = RequestBody.create(MediaType.parse("video/*"), file);
                body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            }
        } else {
            Toast.makeText(context, "Path is empty", Toast.LENGTH_SHORT).show();
        }
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"),User_Id);
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"),username);
        RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), et_desc.getText().toString());
        RequestBody status = RequestBody.create(MediaType.parse("text/plain"), Status);
        RequestBody incidenceID = RequestBody.create(MediaType.parse("text/plain"), incidenceId);
        Call<ResponseBody> call;
        RequestBody file_name = RequestBody.create(MediaType.parse("text/plain"), "" + filename);
        call = service.upload(
                incidenceID,
                title,
                desc,
                status,
                user_id,
                body
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
                String output = "";
                try {
                    output = response.body().string();

                    Log.d("Response", "response=> " + output);
                    JSONObject i = new JSONObject(output);
                    boolean res = Boolean.parseBoolean(i.getString("result"));
                    String reason = i.getString("reason");

                    if (res) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Post " + reason, Toast.LENGTH_SHORT).show();
                        activity.finish();
                    } else
                        Toast.makeText(context, "" + reason, Toast.LENGTH_SHORT).show();
                    //  Log.v("Upload", "" + response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Upload error:", "");
                progressDialog.dismiss();
            }
        });
    }
}
