package com.mohitsprojects.instaclone.Fragmets;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.mohitsprojects.instaclone.Adaptors.NotifAdapter;
import com.mohitsprojects.instaclone.AndroidConstants.MyProperty;
import com.mohitsprojects.instaclone.Models.NotificationModel;
import com.mohitsprojects.instaclone.R;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class FragmentFavorite extends Fragment {

    private ListView listView;
    private NotifAdapter notifAdapter;
    private ArrayList<NotificationModel> arrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_favorite,container,false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.notifToolbar);
        toolbar.setTitle("Notification");

        initFun(view);
        return view;
    }

    private void initFun(View view) {

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

        listView = view.findViewById(R.id.list_view_notif);
        arrayList = getData();
        if(arrayList.isEmpty()){
            LinearLayout linearLayout = view.findViewById(R.id.emptyElement);
            linearLayout.setVisibility(View.VISIBLE);
        }
        notifAdapter = new NotifAdapter(getContext(), arrayList);
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
