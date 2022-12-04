package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.loginpage.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class GroupEditActivity extends AppCompatActivity {


    ImageView groupImage;
    EditText Title,description;
    Button update;
    private ActionBar actionBar;

    ProgressDialog progressDialog;


    FirebaseAuth mAuth;


    String groupId1;

    static final  int CAMERA_REQUEST_CODE=100;
    static final  int STORAGE_REQUEST_CODE=200;

    static final  int IMAGE_PICK_CAMERA_CODE=300;
    static final  int IMAGE_PICK_GALLERY_CODE=400;

    private String[]cameraPermission;
    private String []storagePermission;

    private Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        groupImage=findViewById(R.id.groupImage);
        Title=findViewById(R.id.title);
        description=findViewById(R.id.description);
        update=findViewById(R.id.update);

        actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Edit Group");

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait....");
        progressDialog.setCanceledOnTouchOutside(false);


        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        mAuth=FirebaseAuth.getInstance();

        groupId1=getIntent().getStringExtra("groupId");

        loadGroupInfo();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startcreatingGroup();


            }
        });

        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showImagePick();
                Dexter.withActivity(GroupEditActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent,100);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                                token.cancelPermissionRequest();
                            }
                        }).check();

            }
        });


    }

    private void startcreatingGroup() {
        String groupTitle=Title.getText().toString().trim();
        String groupDesc=description.getText().toString().trim();


        if(TextUtils.isEmpty(groupTitle)){
            Toast.makeText(this, "Please enter Group Title", Toast.LENGTH_SHORT).show();
            return;
        }


        if(image_uri==null){
            progressDialog.setMessage("Updating Group Info......");
            progressDialog.show();

            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("groupDescription",""+groupDesc);
            hashMap.put("groupTitle",""+groupTitle);

            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
            ref.child(groupId1).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(GroupEditActivity.this, "Group info updated....", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(GroupEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            progressDialog.setMessage("Updating Group Info......");
            progressDialog.show();


            String timesstamp=""+System.currentTimeMillis();
            String fillPathAndName="Group_Info/"+"image"+"-"+timesstamp;

            StorageReference storageReference= FirebaseStorage.getInstance().getReference(fillPathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri>p_uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while (!p_uriTask.isSuccessful());
                            Uri p_downloadUri=p_uriTask.getResult();
                            if(p_uriTask.isSuccessful()){
                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("groupDescription",""+groupDesc);
                                hashMap.put("groupTitle",""+groupTitle);
                                hashMap.put("groupIcon",""+p_downloadUri);

                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
                                ref.child(groupId1).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(GroupEditActivity.this, "Group info updated....", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(GroupEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(GroupEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void loadGroupInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    String groupId=""+ds.child("groupId").getValue();
                    String groupTitle=""+ds.child("groupTitle").getValue();
                    String groupDescription=""+ds.child("groupDescription").getValue();
                    String groupIcon=""+ds.child("groupIcon").getValue();
                    String timestamp=""+ds.child("timestamp").getValue();
                    String createdBy=""+ds.child("createdBy").getValue();

                    Title.setText(groupTitle);
                    description.setText(groupDescription);

                    try {
                        Picasso.get().load(groupIcon).into(groupImage);

                    }catch (Exception e){
                        groupImage.setImageResource(R.drawable.image_placeholder);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if(requestCode==RESULT_OK){
           if(requestCode==IMAGE_PICK_GALLERY_CODE){
               //was picked from gallery
               image_uri=data.getData();
               //set to  imageview
               groupImage.setImageURI(image_uri);


              //  if(requestCode==100&&data!=null&&data.getData()!=null){

              //      image_uri=data.getData();
              //      groupImage.setImageURI(image_uri);
            }
            else if(requestCode==IMAGE_PICK_CAMERA_CODE){
                //was picked from camera
                groupImage.setImageURI(image_uri);
            }
        }*/

        if(requestCode==100&&data!=null&&data.getData()!=null) {

            image_uri = data.getData();
            groupImage.setImageURI(image_uri);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}