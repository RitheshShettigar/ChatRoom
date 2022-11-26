package com.example.loginpage.Activity;

import static android.media.CamcorderProfile.get;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.loginpage.Adapter.PostAdapter;
import com.example.loginpage.Adapter.PostCommentAdapter;
import com.example.loginpage.Adapter.ReelsAdapter;
import com.example.loginpage.Adapter.UserAdapter;
import com.example.loginpage.ModelClass.Commentmodel;
import com.example.loginpage.ModelClass.MemberReels;
import com.example.loginpage.ModelClass.PostModel;
import com.example.loginpage.ModelClass.modelUser;
import com.example.loginpage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    RecyclerView Recyclerview;
    ImageView selectPost,sendPost;
    EditText writePost;
    Uri imageUri;
    ProgressDialog prog2;
    String username,userProfile;

    DatabaseReference PostRef;
    StorageReference postImageRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase database;

    PostAdapter postAdapter;
    ArrayList<PostModel> postModelArrayList;

    PostCommentAdapter postCommentAdapterr;
    ArrayList<Commentmodel> commentmodelArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postModelArrayList= new ArrayList<>();

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();


        prog2=new ProgressDialog(this);
        prog2.setMessage("Adding Post....");
        prog2.setCancelable(false);

        selectPost=findViewById(R.id.selectPost);
        sendPost=findViewById(R.id.sendPost);
        writePost=findViewById(R.id.writePost);

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




        PostRef= FirebaseDatabase.getInstance().getReference().child("Post");
        postImageRef= FirebaseStorage.getInstance().getReference().child("PostImage");

        DatabaseReference root= FirebaseDatabase.getInstance().getReference().child("Post");



        Recyclerview=findViewById(R.id.RecyclerView);
        Recyclerview.setHasFixedSize(true);
        Recyclerview.setLayoutManager(new LinearLayoutManager(this));
        postModelArrayList= new ArrayList<>();
        postAdapter=new PostAdapter(this,postModelArrayList);
        Recyclerview.setAdapter(postAdapter);

       root.addValueEventListener(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                                          postModelArrayList.clear();
                                          for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                              PostModel postModel=dataSnapshot.getValue(PostModel.class);
                                              postModelArrayList.add(postModel);
                                          }postAdapter.notifyDataSetChanged();
                                      }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {
           }
       });




       //comment
        DatabaseReference root1= FirebaseDatabase.getInstance().getReference().child("PostComment");

     //  Recyclerview=findViewById(R.id.commentRecycler);
     //  Recyclerview.setHasFixedSize(true);
     //  Recyclerview.setLayoutManager(new LinearLayoutManager(this));
     //  commentmodelArrayList= new ArrayList<>();
     //  postCommentAdapterr=new PostCommentAdapter(this,commentmodelArrayList);
     //  Recyclerview.setAdapter(postAdapter);

    // root1.addValueEventListener(new ValueEventListener() {
    //     @Override
    //     public void onDataChange(@NonNull DataSnapshot snapshot) {
    //         commentmodelArrayList.clear();
    //         for(DataSnapshot dataSnapshot:snapshot.getChildren()){
    //             Commentmodel commentmodel=dataSnapshot.getValue(Commentmodel .class);
    //             commentmodelArrayList.add(commentmodel);
    //         }
    //         postCommentAdapterr.notifyDataSetChanged();
    //     }
    //     @Override
    //     public void onCancelled(@NonNull DatabaseError error) {
    //     }
    // });

        //end comment




//Bottom navigation
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.post);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.status:
                        return true;

                    case R.id.userlist:
                        startActivity(new Intent(getApplicationContext(),UserListActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.calls:
                        startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.realse:
                        startActivity(new Intent(getApplicationContext(),ReelsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
        //Bottom navigation end

        selectPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 100);
            }
        });

        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPost();
            }
        });


    }



    //photo add
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&data!=null&&data.getData()!=null){

            imageUri=data.getData();
            selectPost.setImageURI(imageUri);
        }
    }
    //photo add end

    private void AddPost() {
        String postDes=writePost.getText().toString();
        if(postDes.length()<3)
        {
            Toast.makeText(this, "Please write somthing post", Toast.LENGTH_SHORT).show();
        }else if(imageUri==null)
        {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
        else
        {
         prog2.show();

            Date date=new Date();
            SimpleDateFormat format=new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String strDate=format.format(date);


            postImageRef.child(mUser.getUid()+strDate).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                     postImageRef.child(mUser.getUid()+strDate).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {

                             HashMap hashMap=new HashMap();
                             hashMap.put("date",strDate);
                             hashMap.put("postImage",uri.toString());
                             hashMap.put("postDesc",postDes);
                             hashMap.put("userProfile",userProfile);
                             hashMap.put("userName",username);
                             hashMap.put("postid",mUser.getUid()+strDate);
                             hashMap.put("userid",mUser.getUid());


                            PostRef.child(mUser.getUid()+strDate).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                 @Override
                                 public void onComplete(@NonNull Task task) {
                                     if(task.isSuccessful())
                                     {
                                         prog2.dismiss();
                                         Toast.makeText(PostActivity.this, "Post Add", Toast.LENGTH_SHORT).show();
                                         selectPost.setImageResource(R.drawable.image_placeholder);
                                         writePost.setText("");
                                     }
                                     else
                                     {
                                         prog2.dismiss();
                                         Toast.makeText(PostActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                     }
                                 }
                             });


                         }
                     }) ;
                    }
                    else {
                        prog2.dismiss();
                        Toast.makeText(PostActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
    //status online offline
    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }
    //status online offline end
}