package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.Adapter.FriendHomeAdapter;
import com.example.loginpage.Adapter.TopStatusAdapter;
import com.example.loginpage.ModelClass.FriendHomemodel;
import com.example.loginpage.ModelClass.Status;
import com.example.loginpage.ModelClass.UserStatus;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import meow.bottomnavigation.MeowBottomNavigation;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView recyclerView;
    ArrayList<UserStatus>userStatuses;

    TopStatusAdapter statusadapter;
    ProgressDialog prog3;
    RecyclerView statuslist;
    modelUser User;
    FirebaseDatabase database;


    private List<String>friendList1;
    FriendHomeAdapter friendHomeAdapter;
    ArrayList<FriendHomemodel> friendHomemodelArrayList;

    FirebaseAuth mAuth;
   FirebaseUser mUser;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        statuslist=findViewById(R.id.statusList);
        database=FirebaseDatabase.getInstance();



        //notification code
        database=FirebaseDatabase.getInstance();
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                      HashMap<String, Object> map = new HashMap<>();
                      map.put("token", token);
                      database.getReference()
                              .child("User")
                              .child(FirebaseAuth.getInstance().getUid())
                              .updateChildren(map);
                        //Toast.makeText(HomeActivity.this, token, Toast.LENGTH_SHORT).show();

                    }
                });
        //notification code end

        prog3=new ProgressDialog(this);
        prog3.setMessage("Uploading image....");
        prog3.setCancelable(false);

      friendHomemodelArrayList=new ArrayList<>();


//Status
     //statusadapter=new TopStatusAdapter(this,userStatuses);
     //userStatuses=new ArrayList<>();
     //statuslist.setAdapter(statusadapter);
     //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
     //layoutManager.setOrientation(RecyclerView.HORIZONTAL);
     //statuslist.setLayoutManager(layoutManager);
     //userStatuses= new ArrayList<>();


     //   recyclerView.setHasFixedSize(true);
      //
       // friendHomemodelArrayList= new ArrayList<>();
       // friendHomeAdapter =new FriendHomeAdapter(this,friendHomemodelArrayList);
      //  recyclerView.setAdapter( friendHomeAdapter );


       recyclerView=findViewById(R.id.recyclerView);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
       mAuth=FirebaseAuth.getInstance();
       mUser=mAuth.getCurrentUser();


       checkfriend();

       recyclerView=findViewById(R.id.statusList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userStatuses= new ArrayList<>();
        statusadapter=new TopStatusAdapter(this,userStatuses);
        recyclerView.setAdapter(statusadapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);

        database.getReference().child("User").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User=snapshot.getValue(modelUser.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userStatuses.clear();
                    for(DataSnapshot storySnapshot : snapshot.getChildren()) {
                        UserStatus status = new UserStatus();
                        status.setUsername(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdate(storySnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();

                        for(DataSnapshot statusSnapshot : storySnapshot.child("status").getChildren()) {
                           Status sampleStatus = statusSnapshot.getValue(Status.class);
                           statuses.add(sampleStatus);
                        }

                       status.setStatuses(statuses);
                        userStatuses.add(status);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Status end
        //bottom navigation


       BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
       bottomNavigationView.setSelectedItemId(R.id.home);
       bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()) {
                   case R.id.post:
                       startActivity(new Intent(getApplicationContext(), PostActivity.class));
                    //  Intent intent=new Intent();
                    //  intent.setType("image/*");
                    //  intent.setAction(Intent.ACTION_GET_CONTENT);
                    //  startActivityForResult(intent,75);
                       overridePendingTransition(0,0);
                       return true;

                   case R.id.userlist:
                       startActivity(new Intent(getApplicationContext(),UserListActivity.class));
                       overridePendingTransition(0,0);
                       return true;

                   case R.id.home:
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



        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null){
            startActivity(new Intent(HomeActivity.this,RegisterActivity.class));
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


    private void checkfriend() {

        friendList1=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Friends").child(mUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    friendList1.add(snapshot.getKey());
                   // Toast.makeText(HomeActivity.this, friendList1.toString(), Toast.LENGTH_SHORT).show();
                }
              readuser2();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readuser2() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("User");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendHomemodelArrayList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                   FriendHomemodel modelUser=dataSnapshot.getValue(FriendHomemodel.class);
                    for (String  id : friendList1){
                        if(modelUser.getId().equals(id)){
                            friendHomemodelArrayList.add(modelUser);

                        }
                    }

                }
                friendHomeAdapter= new FriendHomeAdapter(HomeActivity.this,friendHomemodelArrayList);
                recyclerView.setAdapter(friendHomeAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //Status
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(data!=null){
           if(data.getData()!=null){
               prog3.show();
               FirebaseStorage storage=FirebaseStorage.getInstance();
               Date date=new Date();
               StorageReference reference=storage.getReference().child("status").child(date.getTime()+"");
               reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                       if(task.isSuccessful()){
                           reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   UserStatus userStatus=new UserStatus();
                                   userStatus.setUsername(User.getUsername());
                                   userStatus.setProfileImage(User.getImageuri());
                                   userStatus.setLastUpdate(date.getTime());

                                   HashMap<String, Object> obj = new HashMap<>();
                                   obj.put("name", userStatus.getUsername());
                                   obj.put("profileImage", userStatus.getProfileImage());
                                   obj.put("lastUpdated", userStatus.getLastUpdate());


                                   String imageUrl = uri.toString();
                                   Status status = new Status(imageUrl, userStatus.getLastUpdate());

                                   database.getReference()
                                           .child("stories")
                                           .child(FirebaseAuth.getInstance().getUid())
                                           .updateChildren(obj);

                                   database.getReference().child("stories")
                                           .child(FirebaseAuth.getInstance().getUid())
                                           .child("statuses")
                                           .push()
                                           .setValue(status);
                                   prog3.dismiss();

                               }
                           });
                       }

                   }
               });
           }
       }
    }
    //Status end


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        menu.findItem(R.id.action_add_participants).setVisible(false);
        menu.findItem(R.id.action_chat_ingo).setVisible(false);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:

                Toast.makeText(HomeActivity.this,"logout done",Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;

            case R.id.setting:
                Intent intent1 = new Intent(HomeActivity.this, UserProfileActivity.class);
                startActivity(intent1);
                finish();


            case R.id.group:
                Intent intent2 = new Intent(HomeActivity.this,GroupActivity.class);
                startActivity(intent2);
                finish();


        }
        return false;
    }


}