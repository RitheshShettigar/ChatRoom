package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.loginpage.Adapter.PostAdapter;
import com.example.loginpage.Adapter.ReelsAdapter;
import com.example.loginpage.ModelClass.MemberReels;
import com.example.loginpage.ModelClass.PostModel;
import com.example.loginpage.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReelsActivity extends AppCompatActivity {

    RecyclerView Recyclerview;
    ReelsAdapter reelsAdapter;
    ArrayList<MemberReels> memberReelsArrayList;
    DatabaseReference root= FirebaseDatabase.getInstance().getReference().child("Reels");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels);



        Recyclerview=findViewById(R.id.RecyclerViewReels);
        Recyclerview.setHasFixedSize(true);
        Recyclerview.setLayoutManager(new LinearLayoutManager(this));
        memberReelsArrayList= new ArrayList<>();
        reelsAdapter=new ReelsAdapter(this,memberReelsArrayList);
        Recyclerview.setAdapter(reelsAdapter);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberReelsArrayList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MemberReels memberReels=dataSnapshot.getValue(MemberReels.class);
                    memberReelsArrayList.add(memberReels);
                }
                reelsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.realse);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.post:
                        startActivity(new Intent(getApplicationContext(), PostActivity.class));
                        // Intent intent=new Intent();
                        // intent.setType("image/*");
                        // intent.setAction(Intent.ACTION_GET_CONTENT);
                        // startActivityForResult(intent,75);
                        overridePendingTransition(0,0);
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
                        return true;
                }
                return false;
            }
        });
    }
}