package com.mohitsprojects.instaclone.Fragmets;

import static com.mohitsprojects.instaclone.Retrotfit.RetrofitClient.BASE_URL;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mohitsprojects.instaclone.Activities.ChoosePostActivity;
import com.mohitsprojects.instaclone.Activities.SearchUserProfile;
import com.mohitsprojects.instaclone.Activities.VideoShootActivity;
import com.mohitsprojects.instaclone.Adaptors.PostAdaptor;
import com.mohitsprojects.instaclone.HideProgrssBarInterface;
import com.mohitsprojects.instaclone.Models.PostModel;
import com.mohitsprojects.instaclone.R;
import com.mohitsprojects.instaclone.Retrotfit.Interface;
import com.mohitsprojects.instaclone.Retrotfit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHome extends Fragment  {
    private final LinkedList<PostModel> postList = new LinkedList<>();
    private RecyclerView postRecyclerView;
    private ImageView addPost,chatsBox;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private PostAdaptor postAdaptor;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        postRecyclerView = view.findViewById(R.id.verticalPostRV);
        addPost = view.findViewById(R.id.idAddPost);
        chatsBox = view.findViewById(R.id.idchatbox);
        swipeRefreshLayout = view.findViewById(R.id.idSrl);
        shimmerFrameLayout =view.findViewById(R.id.shimmerL);
        shimmerFrameLayout.startShimmer();
        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            postList.clear();
            getData();
        });



        addPost.setOnClickListener(view1 -> addPostOption());
        chatsBox.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), SearchUserProfile.class)));
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //getting data from API
        getData();
        return view;
    }

    private void addPostOption() {
            PopupMenu pm = new PopupMenu(getActivity(),addPost);
            pm.getMenuInflater().inflate(R.menu.plus_option, pm.getMenu());
            pm.setOnMenuItemClickListener(item1 -> {
                switch (item1.getItemId())   {

                    case R.id.idAddPosts:
                        startActivity(new Intent(getActivity(), ChoosePostActivity.class));

                        return true;
                    case R.id.idAddReel:
                        Dexter.withActivity(getActivity())
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
                                            startActivity(new Intent( getActivity(), VideoShootActivity.class));
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
                                withErrorListener(error -> Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                                .onSameThread()
                                .check();

                        break;

//                        case R.id.idAddStory:
//                            break;
//                        case R.id.idGoLive:
//                            break;

                    default:
                        break;
                }
                return true;
            }); pm.show();
        }


    private void getData() {
        Interface api = RetrofitClient.getRetrofit(BASE_URL).create(Interface.class);
        Call<ResponseBody> result = api.POSTBidList("3","4");

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String output;
                try {
                    assert response.body() != null;
                    output = response.body().string();
                    JSONObject jsonObject = new JSONObject(output);
                    JSONArray jsonArray = jsonObject.getJSONArray("statusList");
                    String path = jsonObject.getString("status_path");
                    Log.i("pathtag","status path"+path);
                    for(int i =0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        String title = object.getString("title");
                        String description = object.getString("description");
                        String imagename = object.getString("image");

                        String userImage =path+imagename;
                        String image = path+imagename;

                        PostModel postModel = new PostModel(userImage,title,image,description,"25345 Comments","3436 likes",false);
                        postList.addFirst(postModel);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        postRecyclerView.setVisibility(View.VISIBLE);
                        postAdaptor = new PostAdaptor(postList, getActivity());
//                        ((SimpleItemAnimator) Objects.requireNonNull(postRecyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
                        swipeRefreshLayout.setRefreshing (false);
                        postRecyclerView.setAdapter(postAdaptor);


                    }
                    Log.d("my_tag", "onResponse: " + output);
                } catch(IOException | JSONException i) {
                    i.printStackTrace();
                }
            }
            @Override
            public void onFailure (@NonNull Call < ResponseBody > call, @NonNull Throwable t){
                Log.d("Issue", "Technical Error");
            }
        });

    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}
