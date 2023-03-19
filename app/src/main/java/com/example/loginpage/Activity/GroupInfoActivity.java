package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.Adapter.AdapterParticipantsAdd;
import com.example.loginpage.Adapter.GroupChatSendAdapter;
import com.example.loginpage.ModelClass.modelUser;
import com.example.loginpage.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GroupInfoActivity extends AppCompatActivity {

    String groupId;
    String myGrouprole="";

    ActionBar actionBar;
    ImageView groupIconIv;
    TextView descriptionTv,createsByTv,editGroupTv,addVParticipantsTv,leaveGroupTv,ParticipantsTv;
   RecyclerView participantsRv;
    FirebaseAuth mAuth;

    ArrayList<modelUser>modelUserArrayList;
    AdapterParticipantsAdd adapterParticipantsAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);


        groupIconIv=findViewById(R.id.groupIconIv);
        descriptionTv=findViewById(R.id.descriptionTv);
        createsByTv=findViewById(R.id.createsByTv);
        editGroupTv=findViewById(R.id.editGroupTv);
        addVParticipantsTv=findViewById(R.id.addVParticipantsTv);
        leaveGroupTv=findViewById(R.id.leaveGroupTv);
        ParticipantsTv=findViewById(R.id.ParticipantsTv);
        participantsRv=findViewById(R.id.participantsRv);


        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth= FirebaseAuth.getInstance();

        groupId=getIntent().getStringExtra("groupId");


        participantsRv.setLayoutManager(new LinearLayoutManager(this));
        modelUserArrayList=new ArrayList<>();
        adapterParticipantsAdd=new AdapterParticipantsAdd(this,modelUserArrayList,groupId,myGrouprole);
        participantsRv.setAdapter(adapterParticipantsAdd);

        loadGroupInfo();
        loadMyGroupRole();




        addVParticipantsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GroupInfoActivity.this,GroupParticipantsActivity.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
            }
        });

        editGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GroupInfoActivity.this,GroupEditActivity.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
            }
        });

        leaveGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dialogTitle="";
                String dialogDescription="";
                String positiveButtonTitle="";
                if(myGrouprole.equals("creator")){
                    dialogTitle="Delete Group";
                    dialogDescription="Are you sure you want to leave group participants";
                    positiveButtonTitle="DELETE";

                }else {
                    dialogTitle="Leave Group";
                    dialogDescription="Are you sure you want to leave group participants";
                    positiveButtonTitle="LEAVE";
                }
                AlertDialog.Builder builder=new AlertDialog.Builder(GroupInfoActivity.this);
                builder.setTitle(dialogTitle)
                        .setMessage(dialogDescription)
                        .setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(myGrouprole.equals("creator")){
                                    //delete group
                                    deleteGroup();
                                }else {
                                    //leave group
                                    leaveGroup();
                                }

                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                }).show();

            }
        });


    }

    private void leaveGroup() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(mAuth.getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(GroupInfoActivity.this, "Group left successfully...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GroupInfoActivity.this,HomeActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteGroup() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(GroupInfoActivity.this, "Group successfully Deleted...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GroupInfoActivity.this,HomeActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadMyGroupRole() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").orderByChild("uid")
                .equalTo(mAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            myGrouprole=""+ds.child("role").getValue();
                            actionBar.setSubtitle(mAuth.getCurrentUser().getEmail()+"("+myGrouprole+")");

                            if(myGrouprole.equals("Participants")){
                                editGroupTv.setVisibility(View.GONE);
                                addVParticipantsTv.setVisibility(View.GONE);
                                leaveGroupTv.setText("Leave Group");
                            }
                            else if(myGrouprole.equals("admin")){
                                editGroupTv.setVisibility(View.VISIBLE);
                                addVParticipantsTv.setVisibility(View.VISIBLE);
                                leaveGroupTv.setText("Leave Group");

                            }else if(myGrouprole.equals("creator")){
                                editGroupTv.setVisibility(View.VISIBLE);
                                addVParticipantsTv.setVisibility(View.VISIBLE);
                                leaveGroupTv.setText("Delete Group");

                            }
                        }
                        loadParticipants();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadParticipants() {
        modelUserArrayList=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelUserArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    String uid=""+ds.child("uid").getValue();

                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("User");
                    reference.orderByChild("id").equalTo(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds:snapshot.getChildren()){
                                modelUser modelUser=ds.getValue(modelUser.class);

                                modelUserArrayList.add(modelUser);
                            }
                            adapterParticipantsAdd=new AdapterParticipantsAdd(GroupInfoActivity.this,modelUserArrayList,groupId,myGrouprole);
                            participantsRv.setAdapter(adapterParticipantsAdd);
                           ParticipantsTv.setText("Participants("+modelUserArrayList.size()+")");



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

    private void loadGroupInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    String groupId=""+ds.child("groupId").getValue();
                    String groupTitle=""+ds.child("groupTitle").getValue();
                    String groupDescription=""+ds.child("groupDescription").getValue();
                    String groupIcon=""+ds.child("groupIcon").getValue();
                    String timestamp=""+ds.child("timestamp").getValue();
                    String createdBy=""+ds.child("createdBy").getValue();

                    //convert time stamp
                    Calendar cal=Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(Long.parseLong(timestamp));
                    String dateTime= DateFormat.format("dd/M/yyyy hh:mm aa",cal).toString();

                    loadCreatotInfo(dateTime,createdBy);

                    actionBar.setTitle(groupTitle);
                    descriptionTv.setText(groupDescription);

                    try {
                        Picasso.get().load(groupIcon).into(groupIconIv);

                    }catch (Exception e){
                        groupIconIv.setImageResource(R.drawable.image_placeholder);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadCreatotInfo(String dateTime, String createdBy) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("User");
        ref.orderByChild("id").equalTo(createdBy).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    String name=""+ds.child("username");
                    createsByTv.setText("Created By:"+name+"On"+dateTime);
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