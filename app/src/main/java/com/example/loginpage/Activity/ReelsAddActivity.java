package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.loginpage.Adapter.PostAdapter;
import com.example.loginpage.ModelClass.MemberReels;
import com.example.loginpage.ModelClass.PostModel;
import com.example.loginpage.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Member;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ReelsAddActivity extends AppCompatActivity {

    VideoView video;
    TextView chose_video,show_video;
    EditText title;
    Button button_upload_main;
    ProgressDialog prog2;

    String username,userProfile;

    private static final int PICK_VIDEO=1;
    private Uri videoUri;
    MediaController mediaControllerl;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase database;
    UploadTask uploadTask;
   MemberReels memberReels;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prog2=new ProgressDialog(this);
        prog2.setMessage("Adding Reels....");
        prog2.setCancelable(false);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();





        memberReels=new MemberReels();


        video=findViewById(R.id.video);
        chose_video=findViewById(R.id.chose_video);

        title=findViewById(R.id.add_title);
        button_upload_main=findViewById(R.id.button_upload_main);
        mediaControllerl=new MediaController(this);
        video.setMediaController(mediaControllerl);
        video.start();

        storageReference= FirebaseStorage.getInstance().getReference("Reels");
        databaseReference=FirebaseDatabase.getInstance().getReference("Reels");


       DatabaseReference reference=database.getReference().child("User").child(mAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username=snapshot.child("username").getValue().toString();
                userProfile=snapshot.child("imageuri").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });


        chose_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_VIDEO);

            }
        });

      
        button_upload_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVideo();

            }
        });
    }

    private void sendVideo() {

        String videoname=title.getText().toString();
        String  search=title.getText().toString();
        if(videoUri!=null){
            prog2.show();


            Date date=new Date();
            SimpleDateFormat format=new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String strDate=format.format(date);

            final StorageReference reference=storageReference.child(mUser.getUid()+System.currentTimeMillis()+"."+getExt(videoUri));
            uploadTask=reference.putFile(videoUri);

            Task<Uri>uritask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return  reference.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        prog2.dismiss();
                        Toast.makeText(ReelsAddActivity.this, "upload success", Toast.LENGTH_SHORT).show();

                        memberReels.setVideouri(downloadUri.toString());
                        memberReels.setSearch(search);
                        memberReels.setUserName(username);
                        memberReels.setUserProfile(userProfile);
                        memberReels.setTitle(videoname);
                        memberReels.setDate(strDate);
                        memberReels.setUserid(mUser.getUid());
                        memberReels.setReelsid(mAuth.getUid()+strDate);
                        databaseReference.child(mUser.getUid()+strDate).setValue(memberReels);


                    }
                    else {
                        Toast.makeText(ReelsAddActivity.this, "failed;", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_VIDEO||resultCode==RESULT_OK||data!=null||data.getData()!=null){
            videoUri=data.getData();
            video.setVideoURI(videoUri);

        }

        }

        private String getExt(Uri uri){
            ContentResolver contentResolver=getContentResolver();
            MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        }


}