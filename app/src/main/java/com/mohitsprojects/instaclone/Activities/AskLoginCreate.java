package com.mohitsprojects.instaclone.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mohitsprojects.instaclone.R;


public class AskLoginCreate extends AppCompatActivity {

    Button createAcc,loginBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_login_create);
        initFun();
    }

    private void initFun() {
        createAcc = findViewById(R.id.gotocreatAccountBt);
        loginBt = findViewById(R.id.gotoLoginBt);

        createAcc.setOnClickListener(view ->{
            startActivity(new Intent(AskLoginCreate.this,CreateAccountActivity.class));
        });
        loginBt.setOnClickListener(view ->{
            startActivity(new Intent(AskLoginCreate.this,LoginActivity.class));
        });
    }
}