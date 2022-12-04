package com.example.loginpage.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loginpage.R;
import com.squareup.picasso.Picasso;

public class UserProfile extends AppCompatActivity {

    TextView name;
    ImageView image;

    String username,userprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        image=findViewById(R.id.personalProfile);
        name=findViewById(R.id.personalName);

        username=getIntent().getStringExtra("name");
        userprofile=getIntent().getStringExtra("profile");

        name.setText(username);
        Picasso.get().load(userprofile).into(image);

        

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserProfile.this,PostDonloadActivity.class);
               // intent.putExtra("posturl",image);
                intent.putExtra("posturl",userprofile);
                startActivity(intent);

            }
        });
    }
}