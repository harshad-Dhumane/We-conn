package com.mohitsprojects.instaclone.Activities;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.mohitsprojects.instaclone.AndroidConstants.UploadImageToApi;
import com.mohitsprojects.instaclone.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mohitsprojects.instaclone.AndroidConstants.UploadImageToApi;
import com.mohitsprojects.instaclone.R;

import java.util.List;

public class ChoosePostActivity extends com.mohitsprojects.instaclone.Activities.BaseActivity {
    private ProgressDialog progressDialog;
    private String image_name = "image.jpg", image_path = "";
    private EditText et_title, et_desc;
    private Button chooseImage, btn_post;
    private ImageView postView;
    private String incidenceId = "4",Status = "2";
    private String User_id="3";
    LinearLayout createPostLL;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    private String username="Username";
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

    }

    private void init() {
        et_desc = findViewById(R.id.idCaptionET);
        chooseImage = findViewById(R.id.idChooseImgBtn);
        btn_post = findViewById(R.id.idPostBtn);
        postView = findViewById(R.id.idSelectedImg);
        createPostLL = findViewById(R.id.CreatePostLL);
        chooseImage = findViewById(R.id.idChooseImgBtn);
        context = ChoosePostActivity.this;

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        //getting username form firebase
        firebaseData();

        progressDialog = new ProgressDialog(ChoosePostActivity.this);
        progressDialog.setMessage("" + getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        chooseImage.setOnClickListener(view -> Dexter.withActivity(ChoosePostActivity.this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            ImagePicker.with(ChoosePostActivity.this)
                                    .crop()	    			//Crop image(Optional), Check Customization for more option
                                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                                    .start();
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check());



        btn_post.setOnClickListener(view -> {
            if(image_path!=null && !image_path.isEmpty()){
                UploadImageToApi uploadImageToApi = new UploadImageToApi(context,progressDialog,image_path,User_id,username,et_desc,Status,incidenceId);
                uploadImageToApi.uploadFile(image_path, image_name);
            }
        });

    }

    public void firebaseData(){
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProf = snapshot.getValue(User.class);
                if (userProf != null) {
                    username=userProf.name;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChoosePostActivity.this, "Something Wrong Happened in Firebase!! "+error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            Uri uri= data.getData();
            // Use Uri object instead of File to avoid storage permissions
            postView.setImageURI(uri);
            createPostLL.setVisibility(View.VISIBLE);
            image_path=uri.getPath();
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(ChoosePostActivity.this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ChoosePostActivity.this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChoosePostActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //overridePendingTransition(R.animator.left_right, R.animator.right_left);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            //overridePendingTransition(R.animator.left_right, R.animator.right_left);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




}