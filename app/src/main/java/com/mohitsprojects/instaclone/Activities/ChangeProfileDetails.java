package com.mohitsprojects.instaclone.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohitsprojects.instaclone.AndroidConstants.SaveSharedPreference;
import com.mohitsprojects.instaclone.Models.ImageModel;
import com.mohitsprojects.instaclone.R;

public class ChangeProfileDetails extends AppCompatActivity {
    public static final String EXTRA_USER = "com.mohitsprojects.retrofitrecyclerview.EXTRA_USER";
    TextView logoutBt;
    private Dialog mDialog;
    TextView text;
    TextView yes;
    TextView no;
    TextView changePic;
    ImageView img;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private StorageReference firebaseStorage;
    private String userId;
    private Uri imageUri = null;

    private ProgressBar progressBar;

    TextInputEditText name,username,bio;
    ImageView cross,check;

    SharedPreferences sharedPreferences;

    private static final String SHARED_PREF_NAME= "mypref";
    private static final String KEY_NAME="name";

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() ==  EditName.TEXT_CHANGE_CODE) {
                        // There are no request codes
                        String string = result.getData().getStringExtra("RESULT_NAME");
                        String tv = result.getData().getStringExtra("TEXT_VIEW_NAME");
                        if(tv.equals("Name")){
                            name.setText(string);
                            SaveSharedPreference.setName(ChangeProfileDetails.this, string);
                        }else if (tv.equals("Username")){
                            username.setText(string);
                            SaveSharedPreference.setUser(ChangeProfileDetails.this, string);
                        }else if(tv.equals("Bio")){
                            bio.setText(string);
                            SaveSharedPreference.setBio(ChangeProfileDetails.this, string);
                        }

                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_details);
        initFun();


    }

    private void initFun() {

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        progressBar = findViewById(R.id.progressBarEditProf);
        progressBar.setVisibility(View.INVISIBLE);


        logoutBt = findViewById(R.id.logoutButton);

        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.logout_popup);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        yes = mDialog.findViewById(R.id.yesTV);
        no = mDialog.findViewById(R.id.noTV);
        text = mDialog.findViewById(R.id.textTV);
        img=findViewById(R.id.idIV_profile);
        check = findViewById(R.id.checkIV);


        changePic=findViewById(R.id.idTV_profilepic);


        //for editing profile.
        name=findViewById(R.id.idtv_name);
        username=findViewById(R.id.idtv_user);
        bio=findViewById(R.id.idtv_bio);

        handleDataFromFirebaseLogin();

        name.setOnClickListener(i->{
            Intent intent = new Intent(this, EditName.class);
            intent.putExtra("key", "Name");
            someActivityResultLauncher.launch(intent);
        });

        check.setOnClickListener(i->{
            yes.setOnClickListener(j->{
                mDialog.dismiss();
                SaveSharedPreference.setName(this,name.getText().toString());
                SaveSharedPreference.setUser(this,username.getText().toString());
                SaveSharedPreference.setBio(this,bio.getText().toString());

               uploadToFirebase(imageUri);


            });
            no.setOnClickListener(j->{
                //set shared pref to old values
                mDialog.dismiss();
            });
            text.setText("Are You Sure\n" +
                    "Your Want To Make Changes?");
            yes.setText("Yes");
            no.setText("Cancel");
            yes.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);
            mDialog.show();
//            submitData();
        });

        username.setOnClickListener(i->{
            Intent intent = new Intent(this, EditName.class);
            intent.putExtra("key", "Username");
            someActivityResultLauncher.launch(intent);
        });

        bio.setOnClickListener(i->{
            Intent intent = new Intent(this, EditName.class);
            intent.putExtra("key", "Bio");
            someActivityResultLauncher.launch(intent);
        });



        cross=findViewById(R.id.ic_cross);

        cross.setOnClickListener(i->{
            finish();
        });



        logoutBt.setOnClickListener(i ->{
            yes.setOnClickListener(j->{
                logOut();
            });
            no.setOnClickListener(j->{
                mDialog.dismiss();
            });
            text.setText("Log out of\n" +
                    "Instagram?");
            yes.setText("Log out");
            no.setText("Cancel");
            yes.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);
            mDialog.show();
        });

        changePic.setOnClickListener(i -> {
            ImagePicker.with(this)
                    .crop()                                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)                        //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)        //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });
        img.setOnClickListener(i -> {
            ImagePicker.with(this)
                    .crop()                                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)                        //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)        //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });


    }



    private void submitData() {
        Intent data = new Intent();
        finish();
    }

    private void logOut(){
        mAuth.signOut();
        SaveSharedPreference.setUserName(ChangeProfileDetails.this, "");
        Intent intents = new Intent(ChangeProfileDetails.this,AskLoginCreate.class);
        intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intents);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            if (data != null) {
                Uri uri = data.getData();
                Log.d("IMage", uri.toString());
                // Use Uri object instead of File to avoid storage permissions

                imageUri = uri;

                img.setImageURI(uri);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToFirebase(Uri uri){

        User curr = new User();

        curr.userName = username.getText().toString();
        curr.name = name.getText().toString();
        curr.bio = bio.getText().toString();
        curr.email = user.getEmail();
        curr.Id = user.getUid();

        final StorageReference fileRef = firebaseStorage.child("ProfileImages").child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        ImageModel model = new ImageModel(uri.toString());
                        String modelId = reference.push().getKey();
                        progressBar.setVisibility(View.INVISIBLE);
                        curr.profileImage = uri.toString();
                        Toast.makeText(ChangeProfileDetails.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();

                        reference.child(userId).setValue(curr).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ChangeProfileDetails.this, "Successful " , Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(ChangeProfileDetails.this, "Something went wrong :: " + task.getException(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ChangeProfileDetails.this, "Uploading Failed !!" + e.getMessage(), Toast.LENGTH_SHORT).show();

                reference.child(userId).setValue(curr).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ChangeProfileDetails.this, "Successful " , Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ChangeProfileDetails.this, "Something went wrong :: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });



    }

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

    private void handleDataFromFirebaseLogin() {
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProf = snapshot.getValue(User.class);

                if (userProf != null) {
                    if(!userProf.profileImage.equals("")){
                        Glide.with(ChangeProfileDetails.this).load(userProf.profileImage).error(R.drawable.myprofilepic).into(img);
                    }
                    if(!userProf.name.equals("")){
                        name.setText(userProf.name);
                        SaveSharedPreference.setName(ChangeProfileDetails.this, userProf.name);
                    }
                    if(!userProf.userName.equals("")){
                        username.setText(userProf.userName);
                        SaveSharedPreference.setUser(ChangeProfileDetails.this, userProf.userName);
                    }
                    if(!userProf.bio.equals("")){
                        bio.setText(userProf.bio);
                        SaveSharedPreference.setBio(ChangeProfileDetails.this, userProf.bio);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChangeProfileDetails.this, "Something Wrong Happened!!"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



}