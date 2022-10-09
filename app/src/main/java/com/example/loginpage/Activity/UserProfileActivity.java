package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.loginpage.ModelClass.modelRegister;
import com.example.loginpage.R;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    CircleImageView setting_profile;
    EditText setting_name,setting_status;
    FirebaseAuth Auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ImageView save,back;
    Uri setimageURI;
    String email;
    ProgressDialog prog3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        setting_name=findViewById(R.id.setting_name);
        setting_profile=findViewById(R.id.setting_profile);
        save=findViewById(R.id.save);
        back=findViewById(R.id.back);

        prog3=new ProgressDialog(this);
        prog3.setMessage("Please wait....");
        prog3.setCancelable(false);


        DatabaseReference reference=database.getReference().child("User").child(Auth.getUid());
        StorageReference storageReference=storage.getReference().child("upload").child(Auth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email=snapshot.child("email").getValue().toString();
                String name=snapshot.child("username").getValue().toString();
                String image=snapshot.child("imageuri").getValue().toString();

                setting_name.setText(name);
                Picasso.get().load(image).into(setting_profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserProfileActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });


        setting_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(UserProfileActivity.this)
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
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.cancelPermissionRequest();
                            }
                        }).check();

            }
        });



        save.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
              String username= setting_name.getText().toString();

                 if(setimageURI!=null)
                 {
                     storageReference.putFile(setimageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                             storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {
                                     String imageuri=uri.toString();
                                     modelRegister modelRegister = new modelRegister( username, email,imageuri,Auth.getUid());

                                     reference.setValue(modelRegister).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if(task.isSuccessful())
                                             {

                                                 prog3.show();
                                                 Toast.makeText(UserProfileActivity.this," Update Successfully ", Toast.LENGTH_SHORT).show();
                                               //  Intent intent=new Intent(UserProfileActivity.this,HomeActivity.class);
                                                // startActivity(intent);
                                             }
                                             else {
                                                 prog3.dismiss();
                                                 Toast.makeText(UserProfileActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                             }

                                         }
                                     });

                                 }
                             });
                         }
                     });
                 }
                 else {
                     storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                             String imageuri=uri.toString();
                             modelRegister modelRegister = new modelRegister( username, email,imageuri,Auth.getUid());

                             reference.setValue(modelRegister).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful())
                                     {
                                         prog3.show();
                                         Toast.makeText(UserProfileActivity.this,"Data Successfully Updated", Toast.LENGTH_SHORT).show();
                                         Intent intent=new Intent(UserProfileActivity.this,HomeActivity.class);
                                         startActivity(intent);
                                     }
                                     else {
                                         prog3.dismiss();
                                         Toast.makeText(UserProfileActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                     }

                                 }
                             });

                         }
                     });

                 }


                 }
        });


    }

    //photo add
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&data!=null&&data.getData()!=null){

            setimageURI=data.getData();
            setting_profile.setImageURI(setimageURI);
        }
    }
    //photo add end
}