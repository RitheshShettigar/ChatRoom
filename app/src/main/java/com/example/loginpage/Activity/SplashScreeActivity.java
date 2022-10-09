package com.example.loginpage.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.loginpage.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreeActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_scree);

        auth= FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null){
            startActivity(new Intent(SplashScreeActivity.this,RegisterActivity.class));
        }


        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreeActivity.this, HomeActivity.class));
                    finish();

                }
            }, 2000);

    }
}