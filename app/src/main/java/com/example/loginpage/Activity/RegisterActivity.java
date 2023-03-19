package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.ModelClass.modelRegister;
import com.example.loginpage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    EditText reg_username,reg_email,reg_password,confirmpassword;
    TextView log;
    Button btn_register;
    CircleImageView userprofile1;
    ProgressDialog prog2;
    Uri imageUri;
    String imageURI;
    String phone1;


    private FirebaseAuth Auth;
    FirebaseUser firebaseUser;
    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference mDatabase;

    String emailPattern = "[a-zA-Z0-9]+@gmail+\\.+com+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        prog2=new ProgressDialog(this);
        prog2.setMessage("Please wait....");
        prog2.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        Auth = FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();

      /* Auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();*/

        reg_username=findViewById(R.id.name);
        reg_email=findViewById(R.id.email);

        reg_password=findViewById(R.id.password);
        userprofile1=findViewById(R.id.userprofile1);
        confirmpassword=findViewById(R.id.conpassword);
        Auth=FirebaseAuth.getInstance();
        btn_register=findViewById(R.id.register);
        log=findViewById(R.id.log);

        Intent intent=getIntent();
        phone1=intent.getStringExtra("phone1");

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prog2.show();
                String username= reg_username.getText().toString();
                String Email= reg_email.getText().toString();
                String password= reg_password.getText().toString();
                String txtconpassword=confirmpassword.getText().toString();
                //String txtphone=phone.getText().toString();


                if (TextUtils.isEmpty(username)||TextUtils.isEmpty(Email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(txtconpassword)) {
                    Toast.makeText(RegisterActivity.this," all filed are reguired",Toast.LENGTH_SHORT).show();
                    prog2.dismiss();

                }else  if (password.length()  < 6) {
                    Toast.makeText(RegisterActivity.this, "password must at 6 chara", Toast.LENGTH_SHORT).show();
                    prog2.dismiss();

                }
                else if(!password.equals(txtconpassword)){
                    Toast.makeText(RegisterActivity.this, "password does not match", Toast.LENGTH_SHORT).show();
                    prog2.dismiss();
                }
                else if(!Email.matches(emailPattern))
                { reg_email.setError("Please enter valid email");
                    Toast.makeText(RegisterActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                    prog2.dismiss();
                }else{
                    Auth.createUserWithEmailAndPassword(Email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){


                                DatabaseReference reference=database.getReference().child("User").child(Auth.getUid());
                                DatabaseReference reference1=database.getReference().child("UserProfile").child(Auth.getUid());
                                StorageReference storageReference=storage.getReference().child("upload").child(Auth.getUid());
                                if( imageUri!=null){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful())
                                            {

                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                      imageURI=uri.toString();
                                                        modelRegister modelRegister=new modelRegister(username,Email,imageURI,Auth.getUid());
                                                        reference.setValue(modelRegister).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    prog2.show();
                                                                    startActivity(new Intent(RegisterActivity.this,SplashScreeActivity.class));
                                                                }else {
                                                                    Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });
                                                        HashMap<String, Object> map = new HashMap<>();
                                                        map.put("username",username);
                                                        map.put("Nickname","");
                                                        map.put("email",Email);
                                                        map.put("phone",phone1);
                                                        map.put("gender","");
                                                        map.put("qualification","");
                                                        map.put("nationality","");
                                                        map.put("DOB","");
                                                        map.put("id",Auth.getUid());
                                                        map.put("blood","");

                                                        reference1.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });

                                                    }
                                                });
                                            }

                                        }
                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                            prog2.setMessage("Login......:" + (int) percent + "%");
                                        }
                                    });
                                }else{
                                    imageURI="https://firebasestorage.googleapis.com/v0/b/login-page-bac74.appspot.com/o/profile.png?alt=media&token=d6890967-3776-4bdd-afb5-54f8673c1715";
                                    modelRegister modelRegister=new modelRegister(username,Email,imageURI,Auth.getUid());
                                    reference.setValue(modelRegister).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                prog2.show();
                                                startActivity(new Intent(RegisterActivity.this,SplashScreeActivity.class));
                                            }else {
                                                Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("username",username);
                                    map.put("Nickname","");
                                    map.put("email",Email);
                                    map.put("phone","");
                                    map.put("gender","");
                                    map.put("qualification","");
                                    map.put("nationality","");
                                    map.put("DOB","");
                                    map.put("id",Auth.getUid());
                                    map.put("blood","");
                                    reference1.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                }


                            }else{
                                prog2.dismiss();
                                Toast.makeText(RegisterActivity.this, "Email Id Already Used", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });


                }

               //DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("UserProfile").child(Auth.getUid());
               //HashMap<String, Object> map = new HashMap<>();
               //map.put("name",username.toString());


            }

        });


        // select image to gallery
        userprofile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(RegisterActivity.this)
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
        // select image to gallery end

    }




    //photo add
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&data!=null&&data.getData()!=null){

            imageUri=data.getData();
            userprofile1.setImageURI(imageUri);
        }
    }
    //photo add end

}