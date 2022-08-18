package com.mohitsprojects.instaclone.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mohitsprojects.instaclone.Activities.VideoShootActivity;
import com.mohitsprojects.instaclone.Models.ReelModel;
import com.mohitsprojects.instaclone.R;

import java.util.List;

public class ReelAdaptor extends RecyclerView.Adapter<ReelAdaptor.ReelViewHolder> {
    public List<ReelModel> reelModelsList;
    Context context;

    public ReelAdaptor(List<ReelModel> reelModelsList, Context context) {
        this.reelModelsList = reelModelsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_reel_item, parent, false);
        ReelViewHolder viewHolder = new ReelViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReelViewHolder holder, int position) {
        ReelModel reelModel = reelModelsList.get(position);

        if (reelModel.isLiked()) {
            holder.likebtnIV.setImageResource(R.drawable.ic_heart_filled);
        } else {
            holder.likebtnIV.setImageResource(R.drawable.ic_heart_white);

        }
        holder.setVideoData(reelModel);
        holder.likebtnIV.setOnClickListener(v -> {
            holder.SetLike(reelModel);
        });
        Glide.with(context).load(reelModel.getUserImageUrl()).error(R.drawable.myprofilepic).into(holder.userImage);
        holder.caption.setText(reelModel.getCaption());
        holder.username.setText(reelModel.getUsername());


        final GestureDetector gd = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                holder.SetLike(reelModel);
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

        holder.videoView.setOnTouchListener((v, event) -> gd.onTouchEvent(event));
    }

    @Override
    public int getItemCount() {
        return reelModelsList.size();
    }

    public class ReelViewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private ProgressBar progressBar;
        private ImageView camaraBtn, likebtnIV, userImage;
        private TextView caption, username;
        private static final int Video_Record_Code = 101;
        private Uri VideoPath;

        public ReelViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.idReekVideoView);
            progressBar = itemView.findViewById(R.id.idReelPB);
            camaraBtn = itemView.findViewById(R.id.idCameraBtn);
            likebtnIV = itemView.findViewById(R.id.idReelLikeIV);
            caption = itemView.findViewById(R.id.idCaptionReel);
            username = itemView.findViewById(R.id.idUserNameReel);
            userImage = itemView.findViewById(R.id.idUserImageReel);

            camaraBtn.setOnClickListener(view -> {
                context.startActivity(new Intent(context, VideoShootActivity.class));
            });

        }

        public void SetLike(ReelModel model) {
            if (model.isLiked()) {
                likebtnIV.setImageResource(R.drawable.ic_heart_white);
                model.setLiked(false);
            } else {
                likebtnIV.setImageResource(R.drawable.ic_heart_filled);
                model.setLiked(true);
            }
        }

        public void setVideoData(ReelModel reelModel) {
            Uri uri = Uri.parse(reelModel.getVideoUrl());
            videoView.setVideoURI(uri);
            videoView.setOnErrorListener((mp, what, extra) -> {
                Log.e("harshad_video", "onError: " + what);
                return true;
            });
            videoView.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();

                float videoRatio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
                float screenRatio = videoView.getWidth() / (float) videoView.getHeight();
                float scale = videoRatio / screenRatio;
                if (scale >= 1f) videoView.setScaleX(scale);
                else {
                    videoView.setScaleY(1f / scale);
                }
                if (videoView.isPlaying()) {
                    progressBar.setVisibility(View.GONE);
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                }
            });

            videoView.setOnCompletionListener(mediaPlayer -> mediaPlayer.start());

        }

    }
}
