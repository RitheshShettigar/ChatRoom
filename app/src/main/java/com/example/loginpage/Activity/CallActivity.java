package com.example.loginpage.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.loginpage.R;

public class CallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}