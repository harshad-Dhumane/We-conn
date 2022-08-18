package com.mohitsprojects.instaclone.AndroidConstants;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.mohitsprojects.instaclone.Models.NotificationModel;
import com.mohitsprojects.instaclone.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("MSG_RECEIVED", "mssg here");
        String title = "";
        String text = "";

        if(remoteMessage.getData().size() > 0){
            title= remoteMessage.getData().get("key_1");
            text= remoteMessage.getData().get("key_2");
        }else{
            title=remoteMessage.getNotification().getTitle();
            text= remoteMessage.getNotification().getBody();
        }


        final  String CHANNEL_ID = "HEADS_UP_NOTIFICATION";

        ////////NEW CODE///////////////
//        int m = 1;
//        Intent intent = new Intent(this, LauncherActivity.class);
//        PendingIntent pendingIntent =PendingIntent.getActivity(this,
//                m,intent, PendingIntent.FLAG_ONE_SHOT);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder;
//        notificationBuilder =
//                new NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.nekonya)
//                        .setContentTitle(title)
//                        .setContentText(text)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//        notificationManager.notify(m, notificationBuilder.build());
        /////////////NEW CODE END///////////



// OLD CODE

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNEL_ID,
                    "heads up notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getSystemService(NotificationManager.class).createNotificationChannel(channel);
            }
        }
        Notification.Builder notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this,CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.drawable.ic_we_conn)
                    .setAutoCancel(true);
        }
        NotificationManagerCompat.from(this).notify(1,notification.build());
//OLD CODE END


        Log.d("message_received", "Message Received");
        MyProperty.getInstance().dataBase.createRecords(title,text);
        Log.d("message_added", "notification added");
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setBody(text);
        notificationModel.setTitle(title);
        MyProperty.getInstance().arrayList.add(notificationModel);

//
//
//
//        Cursor cursor = MyProperty.getInstance().dataBase.selectRecords();
//        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));



    }



    @Override
    public void handleIntent(@NonNull Intent intent) {
        super.handleIntent(intent);
        Bundle bundle= intent.getExtras();
        if(bundle != null){

            String title = (String) bundle.get("gcm.notification.title");
            String text = (String) bundle.get("gcm.notification.body");
            Log.v("Cursor Obje","Title : "+title + "  |||| "+ text);


//            adding in database
//            MyProperty.getInstance().dataBase.createRecords(title,text);
//            Log.d("message_added", "notification added");
//            NotificationModel notificationModel = new NotificationModel();
//            notificationModel.setBody(text);
//            notificationModel.setTitle(title);
//            MyProperty.getInstance().arrayList.add(notificationModel);
        }

    }

}

