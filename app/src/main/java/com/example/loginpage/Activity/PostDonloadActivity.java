package com.example.loginpage.Activity;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.loginpage.R;
import com.squareup.picasso.Picasso;

public class PostDonloadActivity extends AppCompatActivity {
    ImageView postimage,download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_donload);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postimage=findViewById(R.id.postimage);
        download=findViewById(R.id.download);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String url=getIntent().getStringExtra("posturl");
        Picasso.get().load(url).into(postimage);

       // String userurl=getIntent().getStringExtra(" ReciverImage");
       // Picasso.get().load(userurl).into(postimage);



        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadImage(url);
            }
        });


    }

    private void DownloadImage( String url) {
        Uri uri=Uri.parse(url);
        DownloadManager downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(this,DIRECTORY_DOWNLOADS,".jpg");
        downloadManager.enqueue(request);
        Toast.makeText(this, "Download Completed. See the image Download Manager", Toast.LENGTH_SHORT).show();
    }
}