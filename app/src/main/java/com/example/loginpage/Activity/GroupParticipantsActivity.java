package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.loginpage.Adapter.AdapterParticipantsAdd;
import com.example.loginpage.Adapter.GroupChatAdapter;
import com.example.loginpage.Adapter.GroupChatSendAdapter;
import com.example.loginpage.ModelClass.modelUser;
import com.example.loginpage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupParticipantsActivity extends AppCompatActivity {

    RecyclerView userRv;
    ActionBar actionBar;

    FirebaseAuth mAuth;

    String groupId;
    String myGroupRole;

    ArrayList<modelUser>modelUserArrayList;
    AdapterParticipantsAdd adapterParticipantsAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participants);

        actionBar=getSupportActionBar();
        actionBar.setTitle("Add Participants");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth=FirebaseAuth.getInstance();

        userRv=findViewById(R.id.userRv);
        groupId=getIntent().getStringExtra("groupId");


        userRv.setLayoutManager(new LinearLayoutManager(this));
        modelUserArrayList=new ArrayList<>();
        adapterParticipantsAdd=new AdapterParticipantsAdd(this,modelUserArrayList);
        userRv.setAdapter(adapterParticipantsAdd);

        loadGroupInfo();



    }

    private void getAllUser() {
        modelUserArrayList=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("User");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelUserArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    modelUser modelUser=ds.getValue(modelUser.class);

                    if(!mAuth.getUid().equals(modelUser.getId())){
                        modelUserArrayList.add(modelUser);
                    }
                }
                adapterParticipantsAdd=new AdapterParticipantsAdd(GroupParticipantsActivity.this,modelUserArrayList,""+groupId,""+myGroupRole);
                userRv.setAdapter(adapterParticipantsAdd);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadGroupInfo() {
        DatabaseReference ref1= FirebaseDatabase.getInstance().getReference("Groups");

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()) {
                    String groupId = "" + ds.child("groupId").getValue();
                    String groupTitle = "" + ds.child("groupTitle").getValue();
                    String groupDescription = "" + ds.child("groupDescription").getValue();
                    String groupIcon= "" + ds.child("groupIcon").getValue();
                    String createdBy= "" + ds.child("createdBy").getValue();
                    String timestamp= "" + ds.child("timestamp").getValue();
                    actionBar.setTitle("Add Participants");

                    ref1.child(groupId).child("Participants").child(mAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        myGroupRole=""+snapshot.child("role").getValue();
                                        actionBar.setTitle(groupTitle+"("+myGroupRole+")");

                                        getAllUser();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}