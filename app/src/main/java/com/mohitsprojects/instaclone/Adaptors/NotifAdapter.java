package com.mohitsprojects.instaclone.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohitsprojects.instaclone.Models.NotificationModel;
import com.mohitsprojects.instaclone.R;

import java.util.ArrayList;

public class NotifAdapter extends ArrayAdapter<NotificationModel> {
    Context context;
    public ArrayList<NotificationModel> list = new ArrayList<NotificationModel>();
    public NotifAdapter(Context context, ArrayList<NotificationModel> list) {
        super(context, R.layout.activity_notification,list);
        this.context= context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View singlrItem= convertView;
        ProgramViewHolder holder= null;
        if(singlrItem == null){
            LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singlrItem=layoutInflater.inflate(R.layout.notification_item, parent, false);
            holder= new ProgramViewHolder(singlrItem);
            singlrItem.setTag(holder);

        }
        else{
            holder= (ProgramViewHolder) singlrItem.getTag();
        }
        holder.name.setText(list.get(position).getTitle());
        holder.number.setText(list.get(position).getBody());
        //onclick
//        singlrItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        return singlrItem;
    }


    public class ProgramViewHolder {
        ImageView item;
        TextView name;
        TextView number;
        ProgramViewHolder(View v){
            item=v.findViewById(R.id.idIVNotifImg);
            name= v.findViewById(R.id.idTVNotifName);
            number= v.findViewById(R.id.idTVNotifNumber);
        }
    }

}
