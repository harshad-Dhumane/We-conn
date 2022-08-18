package com.mohitsprojects.instaclone.AndroidConstants;

import com.mohitsprojects.instaclone.Models.NotificationModel;

import java.util.ArrayList;

public class MyProperty {
    private static MyProperty mInstance = null;

    public int CONST_ID = 0;
    public MyDB dataBase = null;
    public ArrayList<NotificationModel> arrayList = new ArrayList<>();

    protected MyProperty() {
    }

    public static synchronized MyProperty getInstance() {
        if (null == mInstance) {
            mInstance = new MyProperty();
        }
        return mInstance;
    }
}