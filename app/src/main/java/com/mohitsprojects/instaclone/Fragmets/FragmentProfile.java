package com.mohitsprojects.instaclone.Fragmets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.mohitsprojects.instaclone.Activities.ChangeProfileDetails;
import com.mohitsprojects.instaclone.Activities.ChoosePostActivity;
import com.mohitsprojects.instaclone.Activities.NotificationActivity;
import com.mohitsprojects.instaclone.Activities.User;
import com.mohitsprojects.instaclone.Activities.VideoShootActivity;
import com.mohitsprojects.instaclone.AndroidConstants.SaveSharedPreference;
import com.mohitsprojects.instaclone.AndroidConstants.Singleton;
import com.mohitsprojects.instaclone.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentProfile extends Fragment {
    private static final int PIC_CROP = 3;
    Button editProfile;
    FrameLayout frameLayoutProf;
    ImageView plusIcon, hamburgerIcon;
    View bottomSheetView;
    FragmentManager fragmentManager;
    GridView gridView;

    public final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImage";
    public final int REQUEST_CAMERA_IMAGE_CODE = 1;
    public final int PICK_IMAGE_MULTIPLE = 2;

    //Bottom Sheet Dailog Clickables
    BottomSheetDialog bottomSheetDialog;
    LinearLayout Post;
    LinearLayout Notification;
    LinearLayout Reel;
    private Dialog mDialog;

    final int REQUEST_EXTERNAL_STORAGE = 100;

    String imagePath;
    private List<Uri> imageUriList = Singleton.getInstance().bitmaps;
    private TextView userName, originalUserName,userBio;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;


    //camera
    private static final int TAKE_PICTURE = 12;
    private Uri imageUri;
    private String imageUrl;

    private CircleImageView circleImageView;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        userName.setText(SaveSharedPreference.getUser(getActivity()));
                        originalUserName.setText(SaveSharedPreference.getName(getActivity()));
                        userBio.setText(SaveSharedPreference.getBio(getActivity()));

//                        curr.name = SaveSharedPreference.getName(getActivity());
//                        curr.userName = SaveSharedPreference.getUser(getActivity());
//                        curr.bio = SaveSharedPreference.getBio(getActivity());
//                        curr.email = user.getEmail();

//                        reference.child(userId).setValue(curr).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful()){
//                                    Toast.makeText(getActivity(), "Successful " , Toast.LENGTH_SHORT).show();
//                                }else{
//                                    Toast.makeText(getActivity(), "Something went wrong :: " + task.getException(), Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        });
                    }
                }
            });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        bottomSheetView = inflater.inflate(R.layout.bottom_sheet_design, null, false);
        initFun(view);
        return view;
    }

    private void initFun(View view) {


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        userId = user.getUid();


        reference = FirebaseDatabase.getInstance().getReference("Users");


        userName = view.findViewById(R.id.userNameProf);
        userBio = view.findViewById(R.id.userBio);
        originalUserName = view.findViewById(R.id.userOriginalNameProf);
        //HERE
        frameLayoutProf = view.findViewById(R.id.frameLayoutProf);
        editProfile = view.findViewById(R.id.editProfBT);
        plusIcon = view.findViewById(R.id.plusIcon);
        hamburgerIcon = view.findViewById(R.id.hamburgerIcon);
        circleImageView = view.findViewById(R.id.idUserimgIV);

        //Bottom Sheet clickables
        Post = bottomSheetView.findViewById(R.id.postLL);
        Notification = bottomSheetView.findViewById(R.id.NotifLinearL);
        Reel = bottomSheetView.findViewById(R.id.reelLinearLayout);

        userName.setText(SaveSharedPreference.getName(getActivity()));
        originalUserName.setText(SaveSharedPreference.getUserName(getActivity()));
        userBio.setText(SaveSharedPreference.getBio(getActivity()));

        handleClicks();
        //HERE
        getChildFragmentManager().beginTransaction().replace(R.id.frameLayoutProf, new MainFragment()).commit();


        handleDataFromFirebaseLogin();


    }

    private void handleClicks() {
        editProfile.setOnClickListener(i -> {
            Intent intent = new Intent(getActivity(), ChangeProfileDetails.class);

            someActivityResultLauncher.launch(intent);
        });


        hamburgerIcon.setOnClickListener(i -> {

        });

        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(bottomSheetView);
        plusIcon.setOnClickListener(i -> {
            bottomSheetDialog.show();
        });

        //Bottom Sheet Dailog Clicks
        Post.setOnClickListener(i -> {
            startActivity(new Intent(getActivity(), ChoosePostActivity.class));
        });
        Notification.setOnClickListener(i->{
            startActivity(new Intent(getActivity(), NotificationActivity.class));
        });
        Reel.setOnClickListener(i ->{
            startActivity(new Intent(getActivity(), VideoShootActivity.class));
        });
    }


    private void handleDataFromFirebaseLogin() {
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProf = snapshot.getValue(User.class);

                if (userProf != null) {
                    if (getActivity() == null) {
                        return;
                    }
                    userName.setText(userProf.name);
                    if(userProf.userName.equals("")){
                        originalUserName.setText(userProf.name);
                    }else{
                        originalUserName.setText(userProf.userName);
                    }

                    if(!userProf.bio.equals("")){
                        userBio.setText(userProf.bio);
                    }

                    if(!userProf.profileImage.equals("")){
                        Glide.with(getActivity()).load(userProf.profileImage).error(R.drawable.myprofilepic).into(circleImageView);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something Wrong Happened!!"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}

