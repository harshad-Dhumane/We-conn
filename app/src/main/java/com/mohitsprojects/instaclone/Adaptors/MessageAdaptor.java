package com.mohitsprojects.instaclone.Adaptors;

import static com.mohitsprojects.instaclone.Activities.ChatActivity.rImage;
import static com.mohitsprojects.instaclone.Activities.ChatActivity.sImage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.mohitsprojects.instaclone.Models.Message;
import com.mohitsprojects.instaclone.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdaptor extends RecyclerView.Adapter {

    Context mContext;
    ArrayList<Message> messageArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;

    public MessageAdaptor(Context mContext, ArrayList<Message> messageArrayList) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.sender_layout_item, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.receiver_layout_item, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);

        Log.d("sIMage", sImage);
        Log.d("rIMage", rImage);
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.message.setText(message.getMessage());
            Glide.with(mContext).load(sImage).error(R.drawable.profile_image).into(viewHolder.circleImageView1);

        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.message.setText(message.getMessage());
            Glide.with(mContext).load(rImage).error(R.drawable.profile_image).into(viewHolder.circleImageView2);
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }


    class SenderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView1;
        TextView message;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView1 = itemView.findViewById(R.id.senderCIV);
            message = itemView.findViewById(R.id.chatMessageBoxTV);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView2;
        TextView message;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView2 = itemView.findViewById(R.id.profileImageIV);
            message = itemView.findViewById(R.id.chatMessageBoxTV);
        }
    }
}
