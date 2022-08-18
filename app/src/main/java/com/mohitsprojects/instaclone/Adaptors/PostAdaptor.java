package com.mohitsprojects.instaclone.Adaptors;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mohitsprojects.instaclone.HideProgrssBarInterface;
import com.mohitsprojects.instaclone.Models.PostModel;
import com.mohitsprojects.instaclone.R;

import java.util.List;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.myPostViewHolder> {
    private  List<PostModel> postModelList;
    private  Context context;



    public PostAdaptor(List<PostModel> postModelList, Context context) {
        this.postModelList = postModelList;
        this.context = context;

    }

    @NonNull
    @Override
    public myPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_post_item, parent, false);

        myPostViewHolder viewHolder = new myPostViewHolder(view);
        return viewHolder;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull myPostViewHolder holder, int position) {
        PostModel postModel = postModelList.get(position);
        if (postModel.isLiked()) {
            holder.likebtnIV.setImageResource(R.drawable.ic_heart_filled);
        } else {
            holder.likebtnIV.setImageResource(R.drawable.ic_heart);
        }
        Glide.with(context).load(postModel.getImageurl()).error(R.drawable.myprofilepic).into(holder.userIV);
        holder.userNameTV.setText(postModel.getUsername());
        holder.userNameCaptionTV.setText(postModel.getUsername());
        holder.commentcountTV.setText(postModel.getCommentCount());
        holder.likebtnIV.setOnClickListener(view -> holder.SetLike(postModel));

         final GestureDetector gd = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                holder.SetLike(postModel);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return true;
            }
        });

        holder.relativeLayout.setOnTouchListener((v, event) -> gd.onTouchEvent(event));

        String PUrl =postModel.getPostUrl();
        if (PUrl.endsWith(".mp4") || PUrl.endsWith(".3gp")) {
            holder.postIV.setVisibility(View.GONE);
            holder.postVV.setVisibility(View.VISIBLE);
            setVideoData(PUrl,holder.postVV,holder);
        }
        else {
            holder.postVV.setVisibility(View.GONE);
            holder.postIV.setVisibility(View.VISIBLE);
            Glide.with(context).load(PUrl).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                     return false;
                }
            }).error(R.drawable.imgnotfound).into(holder.postIV);
        }

        Glide.with(context).load(postModel.getImageurl()).error(R.color.black).into(holder.userIV);
        holder.likecountTV.setText(postModel.getLikesCount());

        if(postModel.getCaption().isEmpty()){
            holder.captionTV.setVisibility(View.GONE);
        }else{
            holder.captionTV.setText(postModel.getCaption().trim());
        }
        postMoreOption(holder);
    }

    private void postMoreOption(myPostViewHolder holder) {
        holder.morebtnIV.setOnClickListener(view1 -> {
            PopupMenu pm = new PopupMenu(context, holder.morebtnIV);
            pm.getMenuInflater().inflate(R.menu.post_more_menu, pm.getMenu());
            pm.setOnMenuItemClickListener(item1 -> {
                switch (item1.getItemId())   {
                    case R.id.idDeletePost:
                        Toast.makeText(context, "delete post", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }); pm.show();
        });
    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public class myPostViewHolder extends RecyclerView.ViewHolder {
        ImageView userIV, postIV, likebtnIV, commentbtnIV, sharebtnIV,morebtnIV;
        TextView userNameTV, likecountTV, captionTV, commentcountTV, userNameCaptionTV;
        VideoView postVV;
        ProgressBar progressBar;
        RelativeLayout relativeLayout;

        public myPostViewHolder(@NonNull View itemView) {
            super(itemView);
            userIV = itemView.findViewById(R.id.idUserimgIV);
            postIV = itemView.findViewById(R.id.idPostIV);
            postVV = itemView.findViewById(R.id.idPostVV);
            morebtnIV = itemView.findViewById(R.id.idMoreOption);
            likebtnIV = itemView.findViewById(R.id.idPostLikeIV);
            commentbtnIV = itemView.findViewById(R.id.idPostCommentIV);
            sharebtnIV = itemView.findViewById(R.id.idPostSendIV);
            userNameTV = itemView.findViewById(R.id.idUserName);
            likecountTV = itemView.findViewById(R.id.idLikesCountTV);
            captionTV = itemView.findViewById(R.id.idCaptionTV);
            commentcountTV = itemView.findViewById(R.id.idCommentCountTV);
            userNameCaptionTV = itemView.findViewById(R.id.idUserCaptionName);
            progressBar = itemView.findViewById(R.id.idPostItemPB);
            relativeLayout = itemView.findViewById(R.id.idFLContainer);

        }

        public void SetLike(PostModel postModel){
            if (postModel.isLiked()) {
                likebtnIV.setImageResource(R.drawable.ic_heart);
                postModel.setLiked(false);
            } else {
                likebtnIV.setImageResource(R.drawable.ic_heart_filled);
                postModel.setLiked(true);

            }
        }
    }

    public void setVideoData(String url, VideoView videoView,myPostViewHolder holder) {
        Uri uri = Uri.parse(url);
        videoView.setVideoURI(uri);
        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.e("harshad_video", "onError: " + what);
            return true;
        });
        videoView.setOnPreparedListener(MediaPlayer::start);
        videoView.setOnCompletionListener(MediaPlayer::start);

    }

}




