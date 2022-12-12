package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.loginpage.Adapter.AdapterParticipantsAdd;
import com.example.loginpage.Adapter.FriendHomeAdapter;
import com.example.loginpage.Adapter.UserAdapter;
import com.example.loginpage.ModelClass.FriendHomemodel;
import com.example.loginpage.ModelClass.modelUser;
import com.example.loginpage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestFriendShowActivity extends AppCompatActivity {

    RecyclerView friendrequest;

    UserAdapter uadapter;
    ArrayList<modelUser> list;
    private List<String> friendList1;

    FirebaseUser mUser;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_friend_show);friendrequest=findViewById(R.id.friendrequest);// recyclerView=findViewById(R.id.recyclerView);jkfbkjjfjjj
        friendrequest.setHasFixedSize(true);
        friendrequest.setLayoutManager(new LinearLayoutManager(this));
        list=new ArrayList<>();
        uadapter=new UserAdapter(this,list);
        friendrequest.setAdapter(uadapter);


      mAuth=FirebaseAuth.getInstance();
       mUser=mAuth.getCurrentUser();

        checkRequest();
    }

    private void checkRequest() {
        friendList1=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Request");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    friendList1.add(snapshot.getKey());

                }
                readFriend2();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readFriend2() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("User");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    modelUser modelUser=dataSnapshot.getValue(modelUser.class);
                    for (String  id : friendList1){
                        if(modelUser.getId().equals(id)){
                            list.add(modelUser);

                        }
                    }

                }
                uadapter= new UserAdapter(RequestFriendShowActivity.this,list);
                friendrequest.setAdapter(uadapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}