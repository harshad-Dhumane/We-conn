package com.mohitsprojects.instaclone.Fragmets;

import static com.mohitsprojects.instaclone.Retrotfit.RetrofitClient.BASE_URL;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.mohitsprojects.instaclone.Adaptors.ImageAdaptor;
import com.mohitsprojects.instaclone.R;
import com.mohitsprojects.instaclone.Retrotfit.Interface;
import com.mohitsprojects.instaclone.Retrotfit.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstFragment extends Fragment {

    ArrayList<String> myList;
    public GridView gridView;
    private List<String> ImageUrlList = new ArrayList<>();
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_first, container, false);
        init(view);

        return view;

    }

    private void init(View view) {

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        progressBar = view.findViewById(R.id.progressBarGridView);
        progressBar.setVisibility(View.VISIBLE);
        gridView = view.findViewById(R.id.gridview);
        getData();
//        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User userProf = snapshot.getValue(User.class);
//
//                if (userProf != null) {
//                    if (getActivity() == null) {
//                        return;
//                    }
//                    if(!userProf.profileImage.equals("")){
//                        ImageUrlList.add(userProf.profileImage);
//                        gridView.setAdapter(new ImageAdaptor(getActivity(), ImageUrlList));
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(), "Something Wrong Happened!!"+error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });

//


    }

    private void getData() {
        Interface api = RetrofitClient.getRetrofit(BASE_URL).create(Interface.class);
        Call<ResponseBody> result = api.POSTBidList("2","4");

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String output = "";
                try {
                    output = response.body().string();
                    JSONObject jsonObject = new JSONObject(output);
                    JSONArray jsonArray = jsonObject.getJSONArray("statusList");
                    String path = jsonObject.getString("status_path");
                    Log.i("pathtag","status path"+path);
                    for(int i =0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        String imagename = object.getString("image");
                        if(imagename.endsWith(".jpg")){
                            String image = path+imagename;
                            ImageUrlList.add(image);
                        }

                    }
                    gridView.setAdapter(new ImageAdaptor(getActivity(), ImageUrlList));
                    Log.d("my_tag", "onResponse: " + output);
                } catch(IOException | JSONException i){
                }
            }
            @Override
            public void onFailure (Call < ResponseBody > call, Throwable t){
                Log.d("Issue", "Technical Error");
            }
        });
        //move this if commented
        progressBar.setVisibility(View.GONE);


    }
}