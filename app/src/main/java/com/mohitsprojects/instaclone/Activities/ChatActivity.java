package com.mohitsprojects.instaclone.Activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohitsprojects.instaclone.Adaptors.MessageAdaptor;
import com.mohitsprojects.instaclone.Models.Message;
import com.mohitsprojects.instaclone.R;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String receiverName,receiverId,receiverUserName,receiverImage;
    CircleImageView profileImage;
    TextView receiverNameTV;
    EditText messageEt;
    CardView sendBtn;
    CardView messageCv;
    RecyclerView recyclerView;
    ArrayList<Message> messageArrayList;
    MessageAdaptor messageAdaptor;

    public static String sImage;
    public static String rImage;


    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference chatReference;
    private String userId,senderRoom,receiverRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        receiverId = getIntent().getStringExtra("receiverId");
        receiverName = getIntent().getStringExtra("receiverName");
        receiverUserName = getIntent().getStringExtra("receiverUserName");
        receiverImage = getIntent().getStringExtra("receiverImage");
        rImage = receiverImage;

        initFun();
    }

    private void initFun() {



        profileImage = findViewById(R.id.profileImageIV);
        receiverNameTV = findViewById(R.id.receiverNameTV);
        sendBtn = findViewById(R.id.sendBtnCardView);
        messageEt = findViewById(R.id.editTextMssg);
        recyclerView = findViewById(R.id.recyclerViewChat);
        messageCv = findViewById(R.id.messageCardView);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userId = mAuth.getUid();
        reference = database.getReference("Users").child(userId);

        messageArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        messageAdaptor = new MessageAdaptor(ChatActivity.this,messageArrayList);
        recyclerView.setAdapter(messageAdaptor);
        recyclerView.scrollToPosition(messageArrayList.size()-1);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage = snapshot.child("profileImage").getValue().toString();
                rImage = receiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        senderRoom = userId+receiverId;
        receiverRoom = receiverId+userId;

        chatReference = database.getReference("Chats").child(senderRoom).child("Messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Message mssg = ds.getValue(Message.class);
                    Log.d("MssgData",mssg.getMessage());
                    messageArrayList.add(mssg);
                }
                messageAdaptor.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageArrayList.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Messages Note fetched", Toast.LENGTH_LONG).show();
            }
        });

        receiverNameTV.setText(receiverUserName);
        Glide.with(this).load(receiverImage).error(R.drawable.profile_image).into(profileImage);



        sendBtn.setOnClickListener(i ->{
            recyclerView.scrollToPosition(messageArrayList.size()-1);
            String message = messageEt.getText().toString().trim();
            if(message.isEmpty()){
                Toast.makeText(ChatActivity.this, "Please Enter Valid Message !!", Toast.LENGTH_SHORT).show();
                return;
            }
            messageEt.setText("");
            Date date = new Date();

            Message message1 = new Message(message,userId,date.getTime());
            database.getReference().child("Chats")
                    .child(senderRoom)
                    .child("Messages")
                    .push()
                    .setValue(message1).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    database.getReference().child("Chats")
                            .child(receiverRoom)
                            .child("Messages")
                            .push()
                            .setValue(message1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            });
        });

    }
    public static void showKeyboard(EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) editText.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }


}