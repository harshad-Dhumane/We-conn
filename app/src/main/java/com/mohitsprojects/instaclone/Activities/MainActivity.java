package com.mohitsprojects.instaclone.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mohitsprojects.instaclone.AndroidConstants.MyDB;
import com.mohitsprojects.instaclone.AndroidConstants.MyProperty;
import com.mohitsprojects.instaclone.AndroidConstants.SaveSharedPreference;
import com.mohitsprojects.instaclone.AndroidConstants.noInternetDialog;
import com.mohitsprojects.instaclone.Fragmets.FragmentFavorite;
import com.mohitsprojects.instaclone.Fragmets.FragmentHome;
import com.mohitsprojects.instaclone.Fragmets.FragmentProfile;
import com.mohitsprojects.instaclone.Fragmets.FragmentReel;
import com.mohitsprojects.instaclone.Fragmets.FragmentSearch;
import com.mohitsprojects.instaclone.R;

import java.io.IOException;

public class MainActivity extends BaseActivity {

    private static final String ROOT_FRAGMENT_TAG = "1234";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(SaveSharedPreference.getUserName(MainActivity.this).length() == 0)
        {
            startActivity(new Intent(MainActivity.this,AskLoginCreate.class));
            finish();
            // call Login Activity
        }
        //initializing databse in singleton class...
        MyProperty.getInstance().dataBase = new MyDB(this.getApplicationContext());

        try {
            initFun();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initFun() throws IOException, InterruptedException {
        //internet popup
        noInternetDialog noInternet = new noInternetDialog(this);
        if(!noInternet.isConnected()){
            noInternet.alertDailog();
        }

//        MyPermission.checkAndRequestPermissions(this);
        BottomNavigationView bottomNav = findViewById(R.id.idBotNavi);
        bottomNav.setOnItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.idFragContainer,
                new FragmentHome()).commit();
    }

    private BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.idBtmNaviHome:
                        selectedFragment = new FragmentHome();
                        break;
                    case R.id.idBtmNaviSearch:
                        selectedFragment = new FragmentSearch();
                        break;
                    case R.id.idBtmNaviReel:
                        selectedFragment = new FragmentReel();
                        break;
                    case R.id.idBtmNaviFav:
                        selectedFragment = new FragmentFavorite();
                        break;
                    case R.id.idBtmNaviProfile:
                        selectedFragment = new FragmentProfile();
                        break;
                    default:
                        return super.onOptionsItemSelected(item);

                }

//                if(item.getItemId() == R.id.idBtmNaviHome){
//                    loadFragment(selectedFragment, 0);
//                }else{
//                    loadFragment(selectedFragment, 1);
//                }
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.idFragContainer, selectedFragment)
                        .addToBackStack(null).commit();

                return true;
            };

            public void loadFragment(Fragment fragment,int flag){
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
                    if(flag == 0){
                        ft.add(R.id.idFragContainer, fragment);
                        fm.popBackStack(ROOT_FRAGMENT_TAG,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        ft.addToBackStack(ROOT_FRAGMENT_TAG);
                    }else{
                        ft.replace(R.id.idFragContainer, fragment);
                        ft.addToBackStack(null);
                    }
                    ft.commit();
            }

}