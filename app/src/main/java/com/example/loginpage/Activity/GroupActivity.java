package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.loginpage.Adapter.GroupChatAdapter;
import com.example.loginpage.Adapter.UserAdapter;
import com.example.loginpage.ModelClass.GroupChatModel;
import com.example.loginpage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    ImageView group;
    RecyclerView messageadapter1;
    ArrayList<GroupChatModel>groupChatModelArrayList;
    GroupChatAdapter groupChatAdapter;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        group=findViewById(R.id.group);
        //messageadapter1=findViewById(R.id.messageadapter);

        mAuth=FirebaseAuth.getInstance();


        messageadapter1=findViewById(R.id.messageadapter);
       // messageadapter1.setHasFixedSize(true);
        messageadapter1.setLayoutManager(new LinearLayoutManager(this));
        groupChatModelArrayList=new ArrayList<>();
        groupChatAdapter=new GroupChatAdapter(this,groupChatModelArrayList);

        messageadapter1.setAdapter(groupChatAdapter);


        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatModelArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.child("Participants").child(mAuth.getUid()).exists()) {
                        GroupChatModel groupChatModel = ds.getValue(GroupChatModel.class);
                        groupChatModelArrayList.add(groupChatModel);
                    }
                }
                groupChatAdapter=new GroupChatAdapter(GroupActivity.this,groupChatModelArrayList);
                messageadapter1.setAdapter(groupChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GroupActivity.this,CreateGroupActivity.class);
                startActivity(intent);
            }
        });
    }


}