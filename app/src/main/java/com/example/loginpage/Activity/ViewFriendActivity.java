package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.ModelClass.modelUser;
import com.example.loginpage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFriendActivity extends AppCompatActivity {

    String RImage,RName,SUid;
    CircleImageView friend_profile;
    TextView friend_name;
    Button btnRequest,btnDecline;
    String CurrentState="nothing_happen";
    FirebaseDatabase database;



    DatabaseReference requestRef,friendRef,mUserRef, friendRef1;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String RUid;
   // FirebaseDatabase firebaseDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


       RUid=getIntent().getStringExtra("uid");
        Toast.makeText(this, RUid, Toast.LENGTH_SHORT).show();

       // Intent intent=new Intent();
      //  intent.putExtra("uid1",RUid);
       // startActivity(intent);


        mUserRef=FirebaseDatabase.getInstance().getReference().child("User").child(RUid);
        requestRef=FirebaseDatabase.getInstance().getReference().child("Request");
        friendRef=FirebaseDatabase.getInstance().getReference().child("Friends");

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        friend_name=findViewById(R.id.friend_name);
        friend_profile=findViewById(R.id.friend_profile);
        btnDecline=findViewById(R.id.btnDecline);
        btnRequest=findViewById(R.id.btnRequest);

        LoadUser();



        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAction(RUid);
            }
        });

        CheckUserexistance(RUid);

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Unfriend(RUid);
            }
        });
    }

    private void Unfriend(String RUid) {
        //UnFriend
        if(CurrentState.equals("friend"))
        {
            friendRef.child(mUser.getUid()).child(RUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        friendRef.child(RUid).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(ViewFriendActivity.this, "You are Unfriend", Toast.LENGTH_SHORT).show();
                                    CurrentState="nothing_happen";
                                    btnRequest.setText("Send Friend Request");
                                    btnDecline.setVisibility(
                                            View.GONE);
                                }

                            }
                        });
                    }


                }
            });
        }
        //UnFriend end
        if(CurrentState.equals("he_sent_pending"))
        {
            HashMap hashMap=new HashMap();
            hashMap.put("status","decline");
            requestRef.child(RUid).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this, "You have Decline friend", Toast.LENGTH_SHORT).show();
                        CurrentState="he_sent_decline";
                        btnDecline.setVisibility(View.GONE);
                        btnRequest.setVisibility(View.GONE);
                    }

                }
            });
        }

    }
    //friend request

    private void CheckUserexistance(String RUid) {
        friendRef.child(mUser.getUid()).child(RUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    CurrentState="friend";
                    btnRequest.setText("Send SMS");
                    btnDecline.setText("UnFriend");
                    btnDecline.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendRef.child(RUid).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    CurrentState="friend";
                    btnRequest.setText("Send SMS");
                    btnDecline.setText("UnFriend");
                    btnDecline.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(mUser.getUid()).child(RUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.child("status").getValue().toString().equals("pending"))
                    {
                        CurrentState="I_sent_pending";
                        btnRequest.setText("cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                    if(snapshot.child("status").getValue().toString().equals("decline"))
                    {
                        CurrentState="I_sent_decline";
                        btnRequest.setText("cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(RUid).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").getValue().toString().equals("pending"))
                    {
                        CurrentState="he_sent_pending";
                        btnRequest.setText("Accept Friend Request");
                        btnDecline.setText("Decline Friend");
                        btnDecline.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(CurrentState.equals("nothing_happen"))
        {
            CurrentState="nothing_happen";
            btnRequest.setText("Send Friend Request");
            btnDecline.setVisibility(View.GONE);
        }
    }
    //friend request end

    private void PerformAction(String RUid) {
        if(CurrentState.equals("nothing_happen"))
        //send friend request
        {
            HashMap hashMap=new HashMap();
            hashMap.put("status","pending");
            requestRef.child(mUser.getUid()).child(RUid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this, "You have send Friend Request", Toast.LENGTH_SHORT).show();
                        btnDecline.setVisibility(View.GONE);
                        CurrentState="I_sent_pending";
                        btnRequest.setText("Cancel Friend Request");
                    }
                    else
                    {
                        Toast.makeText(ViewFriendActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //send friend request end

        //cancel friend request
        if(CurrentState.equals("I_sent_pending")||CurrentState.equals("I_send_decline"))
        {
            requestRef.child(mUser.getUid()).child(RUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ViewFriendActivity.this, "You Have cancelled Friend Request", Toast.LENGTH_SHORT).show();
                        CurrentState="nothing_happen";
                        btnRequest.setText("Send Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                    else {
                        Toast.makeText(ViewFriendActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //cancel friend request end

        //except friend or cancelled
        if(CurrentState.equals("he_sent_pending"))
        {
            requestRef.child(RUid).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        final HashMap hashMap=new HashMap();
                        hashMap.put("status","friend");
                        hashMap.put("username",RName);
                        hashMap.put("profileImageUrl",RImage);
                        friendRef.child(mUser.getUid()).child(RUid).updateChildren(hashMap).addOnCompleteListener((task1) -> {
                           // @Override
                         //   public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {
                                    friendRef.child(RUid).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener((task2) -> {
                                     //   @Override
                                     //   public void onComplete(@NonNull Task task) {
                                            Toast.makeText(ViewFriendActivity.this, "You added Friend", Toast.LENGTH_SHORT).show();
                                            CurrentState="friend";
                                            btnRequest.setText("Send SMS");
                                            btnDecline.setText("UnFriend");
                                            btnDecline.setVisibility(View.VISIBLE);


                                    });
                                }

                        });
                    }
                }
            });
        }
        //except friend or cancelled
        if(CurrentState.equals("friend"))
        {

            Intent intent=new Intent(ViewFriendActivity.this,ChatActivity.class);
            intent.putExtra("OtherUserId",RUid);
            startActivity(intent);
        }

    }


    private void LoadUser() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                   RImage=snapshot.child("imageuri").getValue().toString();
                    RName=snapshot.child("username").getValue().toString();

                    Picasso.get().load(RImage).into(friend_profile);
                    friend_name.setText(RName);
                }else {
                    Toast.makeText(ViewFriendActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, ""+error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }


}