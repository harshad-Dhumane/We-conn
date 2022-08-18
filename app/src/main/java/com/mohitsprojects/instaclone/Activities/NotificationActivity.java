package com.mohitsprojects.instaclone.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.mohitsprojects.instaclone.Adaptors.NotifAdapter;
import com.mohitsprojects.instaclone.AndroidConstants.MyProperty;
import com.mohitsprojects.instaclone.Models.NotificationModel;
import com.mohitsprojects.instaclone.R;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
    private ListView listView;
    private NotifAdapter notifAdapter;
    private ArrayList<NotificationModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initFun();

    }

    private void initFun() {

        FirebaseMessaging
                .getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("tokenTag", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
                    Log.d("tokenTag", "Token "+token);
                });

        listView = findViewById(R.id.list_view_notif);
        arrayList = getData();
        if(arrayList.isEmpty()){
            LinearLayout linearLayout = findViewById(R.id.emptyElement);
            linearLayout.setVisibility(View.VISIBLE);
        }
        notifAdapter = new NotifAdapter(this, arrayList);
        listView.setAdapter(notifAdapter);

//        if(MyProperty.getInstance().dataBase!=null){
//            Cursor cursorM = MyProperty.getInstance().dataBase.selectRecords();
//            Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursorM));
//        }

    }

    private ArrayList<NotificationModel> getData() {
        ArrayList<NotificationModel> arrayList = new ArrayList<>();
        if(MyProperty.getInstance().dataBase == null)return arrayList;
        Cursor cursor = MyProperty.getInstance().dataBase.selectRecords();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setTitle(cursor.getString(1));
            notificationModel.setBody(cursor.getString(2));
            arrayList.add(notificationModel);
        }
//        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
//        for(NotificationModel n :MyProperty.getInstance().arrayList){
//            arrayList.add(new ContactModel(n.getTitle(), n.getBody()));
//        }
        return arrayList;
    }
}