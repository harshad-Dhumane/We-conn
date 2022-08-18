package com.mohitsprojects.instaclone.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mohitsprojects.instaclone.Models.RandomSearchPostModel;
import com.mohitsprojects.instaclone.R;

import java.util.List;

public class RandomPostAdaptor extends RecyclerView.Adapter<RandomPostAdaptor.myRandomPostAdaptor> {
    private final List<RandomSearchPostModel> randomSearchPostModelList;
    private final Context context;

    public RandomPostAdaptor(List<RandomSearchPostModel> randomSearchPostModelList, Context context) {
        this.randomSearchPostModelList = randomSearchPostModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public myRandomPostAdaptor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_random_post_item,parent,false);
        myRandomPostAdaptor viewHolder = new myRandomPostAdaptor(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull myRandomPostAdaptor holder, int position) {
        RandomSearchPostModel randomSearchPostModel = randomSearchPostModelList.get(position);
        Glide.with(context).load(randomSearchPostModel.getImageUrl()).error(R.drawable.myprofilepic).into(holder.randomPost);
    }

    @Override
    public int getItemCount() {
        return randomSearchPostModelList.size();
    }

    public class myRandomPostAdaptor extends RecyclerView.ViewHolder {
        ImageView randomPost;
//        VideoView randomVideo;
        public myRandomPostAdaptor(@NonNull View itemView) {
            super(itemView);
            randomPost = itemView.findViewById(R.id.idRandomPostIV);
        }
    }
}
