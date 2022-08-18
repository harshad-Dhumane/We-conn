package com.mohitsprojects.instaclone.Fragmets;

import static com.mohitsprojects.instaclone.Retrotfit.RetrofitClient.BASE_URL;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager2.widget.ViewPager2;

import com.mohitsprojects.instaclone.Adaptors.ReelAdaptor;
import com.mohitsprojects.instaclone.Models.ReelModel;
import com.mohitsprojects.instaclone.R;
import com.mohitsprojects.instaclone.Retrotfit.Interface;
import com.mohitsprojects.instaclone.Retrotfit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentReel extends Fragment {
    private ViewPager2 viewPager2;
    private RecyclerView recyclerView;
    private ReelAdaptor reelAdaptor;
    private final LinkedList<ReelModel> reelList= new LinkedList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_reel,container,false);
        viewPager2 = view.findViewById(R.id.idviewPager);
//        recyclerView = view.findViewById(R.id.idReelRV);
        getData();

//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        SnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);

        return view;
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
                        String username = object.getString("title");
                        String description = object.getString("description");
                        String imagename = object.getString("image");
                        boolean isLiked = false;
                        String videoUrl="";
                        String userImage =path+imagename;
                        if(imagename.endsWith(".mp4")){
                            videoUrl = path+imagename;
                            ReelModel reelModel = new ReelModel(username,description,userImage,isLiked,videoUrl);
                            reelList.addFirst(reelModel);
                            reelAdaptor = new ReelAdaptor(reelList,getActivity());
                            viewPager2.setAdapter(reelAdaptor);
                            reelAdaptor.notifyDataSetChanged();
                        }
                    }
                    Log.d("my_tag", "onResponse: " + output);
                } catch(IOException | JSONException i){
                }
            }
            @Override
            public void onFailure (Call < ResponseBody > call, Throwable t){
                Log.d("Issue", "Technical Error");
            }
        });

    }
}