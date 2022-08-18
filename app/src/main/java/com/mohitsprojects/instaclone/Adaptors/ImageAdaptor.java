package com.mohitsprojects.instaclone.Adaptors;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mohitsprojects.instaclone.R;

import java.util.List;

public class ImageAdaptor extends BaseAdapter
{
    private Context mContext;
    List<String> list;
    public ImageAdaptor(Context c,List<String> l)
    {
        mContext = c;
        list = l;
    }

    //---returns the number of images---
    public int getCount() {
        return list.size();
    }

    //---returns the ID of an item---
    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    //---returns an ImageView view---
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(340, 350));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    imageView.setImageBitmap(list.get(position));
//                }
//            }).start();
        Glide.with(mContext).load(list.get(position)).error(R.drawable.myprofilepic).into(imageView);
//        imageView.setImageURI(list.get(position));
//            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.mem));
        return imageView;
    }
}