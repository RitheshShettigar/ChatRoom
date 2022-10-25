package com.example.loginpage.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loginpage.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalDetails extends AppCompatActivity {

    TextView personalName;
    CircleImageView personalImage;

    String name,image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        personalImage=findViewById(R.id.personalProfile);
        personalName=findViewById(R.id.personalName);



        Intent intent=getIntent();
        name=intent.getStringExtra("name");
        image=intent.getStringExtra("image");

        Picasso.get().load(image).into(personalImage);
        personalName.setText(name);




    }
}