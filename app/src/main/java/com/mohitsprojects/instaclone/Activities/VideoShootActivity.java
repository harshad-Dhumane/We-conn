package com.mohitsprojects.instaclone.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mohitsprojects.instaclone.AndroidConstants.UploadImageToApi;
import com.mohitsprojects.instaclone.R;
import com.mohitsprojects.instaclone.silicompressor.src.main.java.com.iceteck.silicompressorr.SiliCompressor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohitsprojects.instaclone.silicompressor.src.main.java.com.iceteck.silicompressorr.Util;

import java.io.File;
import java.net.URISyntaxException;

public class VideoShootActivity extends BaseActivity {

    // Initialize variable
    Button btnSelectVideo, btnShootVideo;
    VideoView videoView1;

    private static int Video_Record_Code = 101;
    private static int Select_video_code = 100;


    private ProgressDialog progressDialog;
    private String video_name = "video", video_path = "";
    private EditText et_desc;
    private Button btn_post_video;
    private String incidenceId = "4", Status = "2";
    private String User_Id = "2";
    LinearLayout createPostLL;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    private String username = "Username";
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_shoot);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initFun();
    }

    private void initFun() {
        // Assign variable
        btnSelectVideo = (Button) findViewById(R.id.bt_select);
        btnShootVideo = (Button) findViewById(R.id.bt_takeVideo);
        videoView1 = findViewById(R.id.video_view1);
        et_desc = findViewById(R.id.idCaptionVideoET);
        btn_post_video = findViewById(R.id.bt_UploadVideo);
        createPostLL = findViewById(R.id.idverticalVideoLL);
        context = VideoShootActivity.this;

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        //getting username form firebase
        firebaseData();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("" + getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);


        btnSelectVideo.setOnClickListener(v -> selectVideo());
        btnShootVideo.setOnClickListener(view -> shootVideo());

        btn_post_video.setOnClickListener(view -> {
            if (video_path != null && !video_path.isEmpty()) {
                UploadImageToApi uploadImageToApi = new UploadImageToApi(context, progressDialog, video_path, User_Id, username, et_desc, Status, incidenceId);
                uploadImageToApi.uploadFile(video_path, video_name);
            }
        });
    }

    private void shootVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 3);
        startActivityForResult(intent, Video_Record_Code);

    }

    private void selectVideo() {
        // Initialize intent
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Set type
        intent.setType("video/*");
        // set action
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Start activity result
        startActivityForResult(
                Intent.createChooser(intent, "Select Video"),
                Select_video_code);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode,
                data);
        // Check condition
        if (requestCode == Select_video_code && resultCode == RESULT_OK
                && data != null) {
            // When result is ok
            // Initialize Uri
            Uri uri = data.getData();
            // Initialize file
            File file = new File(
                    Environment.getExternalStorageDirectory()
                            .getAbsolutePath());

            try {
                video_path = Util.getFilePath(context, uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            File file1 = new File(video_path);
            float kb = file1.length() / 1024f;
            if (kb > (1024 * 20)) {
                new CompressVideo().execute(
                        "false", uri.toString(), file.getPath());
            } else {
                videoView1.setVideoURI(uri);
                createPostLL.setVisibility(View.VISIBLE);
                videoView1.start();
                showToast(String.format("Size : %.2f KB", kb));
            }
        }
        if (requestCode == Video_Record_Code) {
            if (resultCode == RESULT_OK) {
                // When result is ok
                // Initialize Uri
                Uri uri = data.getData();
                File file = new File(
                        Environment.getExternalStorageDirectory()
                                .getAbsolutePath());
                try {
                    video_path = Util.getFilePath(context, uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                File file1 = new File(video_path);
                float kb = file1.length() / 1024f;
                if (kb > (1024 * 30)) {
                    new CompressVideo().execute(
                            "false", uri.toString(), file.getPath());
                } else {
                    videoView1.setVideoURI(uri);
                    createPostLL.setVisibility(View.VISIBLE);
                    videoView1.start();
                    showToast(String.format("Size : %.2f KB", kb));
                }
            } else if (requestCode == RESULT_CANCELED) {
                Log.i("Video_Record_Path", "Video is Recording is Cancelled");
            } else {
                Log.i("Video_Record_Path", "Error");
            }
        }

    }


    private class CompressVideo
            extends AsyncTask<String, String, String> {
        // Initialize dialog
        Dialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Display dialog
            dialog = ProgressDialog.show(
                    VideoShootActivity.this, "", "Compressing...");
        }

        @Override
        protected String doInBackground(String... strings) {
            // Initialize video path
            String videoPath = null;

            try {
                // Initialize uri
                Uri uri = Uri.parse(strings[1]);
                // Compress video
                videoPath = SiliCompressor.with(VideoShootActivity.this).compressVideo(uri, strings[2]);
                video_path = videoPath;

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            // Return Video path
            return videoPath;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Dismiss dialog
            dialog.dismiss();
            // Initialize file
            File file = new File(s);
            // Initialize uri
            Uri uri = Uri.fromFile(file);
            // set video uri
            createPostLL.setVisibility(View.VISIBLE);
            videoView1.setVideoURI(uri);

            Toast.makeText(context, "video path " + video_path, Toast.LENGTH_LONG).show();
            // start compress video
            videoView1.setOnPreparedListener(MediaPlayer::start);
            videoView1.setOnCompletionListener(MediaPlayer::start);

            float size = file.length() / 1024f;

        }
    }

    public void firebaseData() {
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProf = snapshot.getValue(User.class);
                if (userProf != null) {
                    username = userProf.name;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Something Wrong Happened in Firebase!! " + error, Toast.LENGTH_LONG).show();
            }
        });
    }


}