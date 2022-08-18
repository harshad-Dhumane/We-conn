package com.mohitsprojects.instaclone.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mohitsprojects.instaclone.AndroidConstants.noInternetDialog;
import com.mohitsprojects.instaclone.R;

import java.io.IOException;


public class ForgotPassword extends AppCompatActivity {

    private Button resetPass;
    private EditText emailEditText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    private Dialog mDialog;
    TextView yes;
    TextView no;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initFun();
    }

    private void initFun() {
        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.logout_popup);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        no = mDialog.findViewById(R.id.noTV);
        yes = mDialog.findViewById(R.id.yesTV);
        text = mDialog.findViewById(R.id.textTV);
        no.setOnClickListener(view ->{
            mDialog.dismiss();
        });

        mAuth = FirebaseAuth.getInstance();
        resetPass = findViewById(R.id.resetPassBT);
        progressBar = findViewById(R.id.progressBarfp);
        emailEditText = findViewById(R.id.emailfpET);

        resetPass.setOnClickListener(view ->{
            try {
                resetPassword();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void resetPassword() throws IOException, InterruptedException {
        noInternetDialog noInternet = new noInternetDialog(this);
        if(!noInternet.isConnected()){
            noInternet.alertDailog();
            return;
        }

        String email = emailEditText.getText().toString().trim();

        if(email.isEmpty()){
            emailEditText.setError("Email is required!!");
            emailEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please provide valid email!!");
            emailEditText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    text.setText("Please Check Your Email \n"+"To Reset Password.");
                    no.setText("Ok");
                    no.setOnClickListener(view -> {
                        startActivity(new Intent(ForgotPassword.this,LoginActivity.class));
                    });
                    no.setVisibility(View.VISIBLE);
                    yes.setVisibility(View.GONE);
                    mDialog.show();

                }else{
                    text.setText("Try Again!\n"+"Something Wrong happended.");
                    no.setText("Cancel");
                    no.setVisibility(View.VISIBLE);
                    yes.setVisibility(View.GONE);
                    mDialog.show();
                }
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }
}