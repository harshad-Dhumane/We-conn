package com.mohitsprojects.instaclone.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mohitsprojects.instaclone.AndroidConstants.SaveSharedPreference;
import com.mohitsprojects.instaclone.R;


public class EditName extends AppCompatActivity {
    ImageView cross, check;
    EditText editname;
    TextView nameTB,name;
    String value;
    public static int TEXT_CHANGE_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        cross=findViewById(R.id.ic_cross);
        nameTB = findViewById(R.id.nameTVToolbar);
        name = findViewById(R.id.id_tv_name);

        editname=findViewById(R.id.edt_nameedit);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             value = extras.getString("key");
            //The key argument here must match that used in the other activity
            name.setText(value);
            nameTB.setText(value);
            if(value.equals("Name")){
                editname.setText(SaveSharedPreference.getName(this));
            }else if(value.equals("Username")){
                editname.setText(SaveSharedPreference.getUser(this));
            }else if (value.equals("Bio")){
                editname.setText(SaveSharedPreference.getBio(this));
            }

        }


        cross.setOnClickListener(i->{
            finish();
        });

        check=findViewById(R.id.ic_check);
        check.setOnClickListener(i->{
            if(editname.getText().equals("")){
                editname.setError("Password is required!!");
                editname.requestFocus();
                return;
            }
            Intent intent= new Intent(EditName.this, ChangeProfileDetails.class);

            intent.putExtra("RESULT_NAME", editname.getText().toString());
            intent.putExtra("TEXT_VIEW_NAME",name.getText());
            setResult(TEXT_CHANGE_CODE, intent);
            finish();

            Toast.makeText(EditName.this,"Changes applied", Toast.LENGTH_LONG).show();
        });
    }

}