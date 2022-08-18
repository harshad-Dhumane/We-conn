package com.mohitsprojects.instaclone.Fragmets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.mohitsprojects.instaclone.Activities.ChatActivity;
import com.mohitsprojects.instaclone.Activities.User;
import com.mohitsprojects.instaclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfViewerFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;

    private TextView userName, originalUserName,userBio;
    private ImageView plusPerson;
    private ImageView backButton;
    private CircleImageView circleImageView;
    private Button follow;
    private Button message;
    FrameLayout frameLayoutProf;

    String receiverName,receiverId,receiverUserName,receiverImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prof_viewer, container, false);
        initFun(view);
        return view;
    }

    private void initFun(View view) {

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        userName = view.findViewById(R.id.userNameProf);
        userBio = view.findViewById(R.id.userBio);
        originalUserName = view.findViewById(R.id.userOriginalNameProf);
        follow = view.findViewById(R.id.followBT);
        message = view.findViewById(R.id.messageBt);
        plusPerson = view.findViewById(R.id.plusPersonIconIV);
        circleImageView = view.findViewById(R.id.idUserimgIV);
        frameLayoutProf = view.findViewById(R.id.frameLayoutProf);
        backButton = view.findViewById(R.id.backButtonOthersProf);

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");
        if (data.equals("none")) {
            userId = user.getUid();
        } else {
            userId = data;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }

        //framelayout tablayout
        getChildFragmentManager().beginTransaction().replace(R.id.frameLayoutProf, new MainFragment()).commit();

        handleDataFromFirebaseLogin();

        if (userId.equals(user.getUid())){
            //if my own profile
            follow.setVisibility(View.GONE);
            plusPerson.setVisibility(View.GONE);
        }else{
            //else others profile
            isFollowed(userId);
        }

        follow.setOnClickListener(i ->{
            if (follow.getText().toString().equals(("Follow"))){
                FirebaseDatabase.getInstance().getReference().child("Follow").
                        child((user.getUid())).child("following").child(userId).setValue(true);

                FirebaseDatabase.getInstance().getReference().child("Follow").
                        child(userId).child("followers").child(user.getUid()).setValue(true);

                isFollowed(userId);

//                addNotification(userId);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Follow").
                        child((user.getUid())).child("following").child(userId).removeValue();

                FirebaseDatabase.getInstance().getReference().child("Follow").
                        child(userId).child("followers").child(user.getUid()).removeValue();
                isFollowed(userId);
            }
        });

        message.setOnClickListener(i ->{
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("receiverName", receiverName);
            intent.putExtra("receiverUserName", receiverUserName);
            intent.putExtra("receiverId", receiverId);
            intent.putExtra("receiverImage", receiverImage);
            startActivity(intent);
        });

        backButton.setOnClickListener(i ->{
            ((FragmentActivity)getActivity()).getSupportFragmentManager().popBackStackImmediate();
//            ((FragmentActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.idFragContainer, new FragmentSearchNext()).addToBackStack(null).commit();
        });
//


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
                    receiverName = userProf.name;
                    receiverUserName = userProf.userName;
                    receiverId = userProf.Id;
                    receiverImage = userProf.profileImage;

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

    private void isFollowed(final String id) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()) {
                    follow.setText("Following");
                    follow.setBackgroundResource(R.drawable.button_addprof_background);
                    follow.setTextColor(follow.getContext().getResources().getColor(R.color.black));
                }else{
                    follow.setText("Follow");
                    follow.setBackgroundColor(follow.getContext().getResources().getColor(R.color.instaBlue));
                    follow.setTextColor(follow.getContext().getResources().getColor(R.color.white));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}