package com.mohitsprojects.instaclone.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mohitsprojects.instaclone.AndroidConstants.noInternetDialog;
import com.mohitsprojects.instaclone.R;

import java.io.IOException;


public class CreateAccountActivity extends AppCompatActivity {

    private TextView alreadyLoggedIn;
    private Button createAccount;
    private FirebaseAuth mAuth;
    private EditText nameEt,passwordEt,emailEt;
    private ProgressBar progressBar;

    private Dialog mDialog;
    TextView yes;
    TextView no;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
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
        nameEt = findViewById(R.id.name);
        passwordEt = findViewById(R.id.passwordEt);
        emailEt = findViewById(R.id.emailEt);
        progressBar = findViewById(R.id.progressBar);
        alreadyLoggedIn = findViewById(R.id.alreadyLoggedIn);
        createAccount = findViewById(R.id.creatAccountBt);

        alreadyLoggedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class));
                finish();
            }
        });

        createAccount.setOnClickListener(view ->{
            try {
                registerUser();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    private void registerUser() throws IOException, InterruptedException {
        noInternetDialog noInternet = new noInternetDialog(this);
        if(!noInternet.isConnected()){
            noInternet.alertDailog();
            return;
        }

        String name = nameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        if(name.isEmpty()){
            nameEt.setError("Full Name is required!!");
            nameEt.requestFocus();
            return;
        }
        if(email.isEmpty()){
            emailEt.setError("Email is required!!");
            emailEt.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEt.setError("Please provide valid email!!");
            emailEt.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordEt.setError("Password is required!!");
            passwordEt.requestFocus();
            return;
        }
        if(password.length() < 6){
            passwordEt.setError("Min Password Length should be at least 6 characters!!");
            passwordEt.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Log.d("reachedDatabase", "Reached database !!");
                            User user = new User(name, email);
                            user.userName = name;
                            user.Id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> taska) {
                                    Log.d("failedData", "Failed!!");
                                    if(taska.isSuccessful()){
                                        text.setText("Account Created\n"+"Successfully.");
                                        no.setText("Ok");
                                        no.setVisibility(View.VISIBLE);
                                        yes.setVisibility(View.GONE);
                                        no.setOnClickListener(view -> {
                                            startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class));
                                        });
                                        mDialog.show();

                                    }else{
                                        text.setText(task.getException().getMessage());
                                        no.setText("Try Again");
                                        no.setVisibility(View.VISIBLE);
                                        yes.setVisibility(View.GONE);
                                        mDialog.show();
                                    }
                                }
                            });

                        }else{
                            text.setText(task.getException().getMessage());
                            no.setText("Try Again");
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