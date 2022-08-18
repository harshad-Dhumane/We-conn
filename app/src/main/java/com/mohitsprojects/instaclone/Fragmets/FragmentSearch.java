package com.mohitsprojects.instaclone.Fragmets;

import static com.mohitsprojects.instaclone.Retrotfit.RetrofitClient.BASE_URL;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.mohitsprojects.instaclone.Activities.User;
import com.mohitsprojects.instaclone.Adaptors.RandomPostAdaptor;
import com.mohitsprojects.instaclone.Adaptors.UserAdapter;
import com.mohitsprojects.instaclone.Models.RandomSearchPostModel;
import com.mohitsprojects.instaclone.R;
import com.mohitsprojects.instaclone.Retrotfit.Interface;
import com.mohitsprojects.instaclone.Retrotfit.RetrofitClient;

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

public class FragmentSearch extends Fragment {
    private RecyclerView recyclerView;
    private List<RandomSearchPostModel> RandomPostList= new ArrayList<>();
    ProgressBar progressBar;

    private List<User> mUsers;
    private UserAdapter userAdapter;

    //hereon
    private LinearLayout searchBarLL;
    private ImageView backIV;
//    private TagAdapter tagAdapter;

//    private SearchView searchView;
    private EditText search_bar;
//till here


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search,container,false);
        recyclerView = view.findViewById(R.id.idRandomPostRV);
        progressBar = view.findViewById(R.id.idRP_PB);
        search_bar = view.findViewById(R.id.search);
        searchBarLL = view.findViewById(R.id.linearLayout2);
        backIV = view.findViewById(R.id.backButtonSearch);

        mUsers = new ArrayList<>();

        searchBarLL.setOnClickListener(i -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.idFragContainer, new FragmentSearchNext())
                    .addToBackStack(null)
                    .commit();
        });
        search_bar.setOnClickListener(i -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.idFragContainer, new FragmentSearchNext())
                    .addToBackStack(null)
                    .commit();
        });

        getData();

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
                        String imagename = object.getString("image");
                        if(imagename.endsWith(".jpg")){
                            String image = path+imagename;
                            RandomSearchPostModel randomSearchPostModel = new RandomSearchPostModel(image);
                            RandomPostList.add(randomSearchPostModel);
                            RandomPostAdaptor randomPostAdaptor = new RandomPostAdaptor(RandomPostList,getActivity());
                            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
                            recyclerView.setAdapter(randomPostAdaptor);
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
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
