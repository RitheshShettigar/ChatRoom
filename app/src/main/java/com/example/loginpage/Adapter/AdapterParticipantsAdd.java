package com.example.loginpage.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.Activity.GroupParticipantsActivity;
import com.example.loginpage.ModelClass.FriendHomemodel;
import com.example.loginpage.ModelClass.MemberReels;
import com.example.loginpage.ModelClass.MessageModel;
import com.example.loginpage.ModelClass.modelUser;
import com.example.loginpage.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterParticipantsAdd  extends RecyclerView.Adapter<AdapterParticipantsAdd.Viewholder>{

    Context context;
    ArrayList<modelUser> modelUserArrayList;
    String groupId,myGroupRole;


    public AdapterParticipantsAdd(Context context, ArrayList<modelUser> modelUserArrayList, String groupId, String myGroupRole) {
        this.context = context;
        this.modelUserArrayList = modelUserArrayList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    public AdapterParticipantsAdd(GroupParticipantsActivity context, ArrayList<modelUser> modelUserArrayList) {
        this.context = context;
        this.modelUserArrayList = modelUserArrayList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public AdapterParticipantsAdd.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_participants_add,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterParticipantsAdd.Viewholder holder, int position) {
        modelUser modelUser=modelUserArrayList.get(position);

        String  image=modelUser.getImageuri();
        final String uid=modelUser.getId();

        holder.nameTv.setText(modelUser.getUsername());

        try{
            Picasso.get().load(image).placeholder(R.drawable.user).into(holder.profileTv);
        }catch (Exception e){
            holder.profileTv.setImageResource(R.drawable.user);
        }

        checkAlreadyExists(modelUser,holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
                ref.child(groupId).child("Participants").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    //user exits/not hisParticipants
                                    String hisPreviousRole=""+snapshot.child("role").getValue();

                                    String[]options;

                                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                    builder.setTitle("Choose Option");
                                    if(myGroupRole.equals("creator")){
                                        if(hisPreviousRole.equals("admin")){
                                            options=new String[]{"Remove Admin","Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //handle item click
                                                    if(i==0){
                                                        //Remove admin click
                                                        removeAdmin(modelUser);
                                                    }else
                                                    {
                                                        //Remove user click
                                                        removeParticipants(modelUser);
                                                    }
                                                }
                                            }).show();
                                        }
                                        else if(hisPreviousRole.equals("Participants")){
                                            //he creator ,he is participants
                                            options=new String[]{"Make Admin","Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //handle item click
                                                    if(i==0){
                                                        //Remove admin click
                                                        makeAdmin(modelUser);
                                                    }else
                                                    {
                                                        //Remove user click
                                                        removeParticipants(modelUser);
                                                    }
                                                }
                                            }).show();

                                        }
                                    }
                                    else if(myGroupRole.equals("admin")) {
                                        if(hisPreviousRole.equals("creator")){
                                            Toast.makeText(context, "Creator of Group....", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(hisPreviousRole.equals("admin")){
                                            options=new String[]{"Remove Admin","Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //handle item click
                                                    if(i==0){
                                                        //Remove admin click
                                                        removeAdmin(modelUser);
                                                    }else
                                                    {
                                                        //Remove user click
                                                        removeParticipants(modelUser);
                                                    }
                                                }
                                            }).show();

                                        }
                                        else if(hisPreviousRole.equals("Participants"));
                                        //in admin,he is participante
                                        options=new String[]{"Make Admin","Remove User"};
                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //handle item click
                                                if(i==0){
                                                    //Remove admin click
                                                    makeAdmin(modelUser);
                                                }else
                                                {
                                                    //Remove user click
                                                    removeParticipants(modelUser);
                                                }
                                            }
                                        }).show();
                                    }
                                }
                                else {
                                    //user does not exits/not participants
                                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                    builder.setTitle("Add Participants")
                                            .setMessage("Add this user in this group")
                                            .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    addParticipants(modelUser);

                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();

                                                }
                                            }).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

    }

    private void addParticipants(modelUser modelUser) {
        //setup userdata
        String timestamp=""+System.currentTimeMillis();
        HashMap<String ,String >hashMap=new HashMap<>();
        hashMap.put("uid",modelUser.getId());
        hashMap.put("username",modelUser.getUsername());
        hashMap.put("userimage",modelUser.getImageuri());
        hashMap.put("role","Participants");
        hashMap.put("timestamp",timestamp);

        //add that user in groups
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Add Successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void makeAdmin(modelUser modelUser) {
        //setup userdata

        HashMap<String ,Object >hashMap=new HashMap<>();
        hashMap.put("role","admin");

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "The User is now Admin...", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void removeParticipants(modelUser modelUser) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "The User is now Admin...", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void removeAdmin(modelUser modelUser) {
        //setup userdata-remove admin

        HashMap<String ,Object >hashMap=new HashMap<>();
        hashMap.put("role","Participants");

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "The User is no longer admin...", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkAlreadyExists(modelUser modelUser, Viewholder holder) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String hisrole=""+snapshot.child("role").getValue();
                            holder.statusTv.setText(hisrole);
                        }
                        else
                        {
                            holder.statusTv.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return modelUserArrayList.size();
    }
    class Viewholder extends RecyclerView.ViewHolder{


        CircleImageView profileTv;
        TextView nameTv,statusTv;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            profileTv=itemView.findViewById(R.id.profileTv);
            nameTv=itemView.findViewById(R.id.nameTv);
            statusTv=itemView.findViewById(R.id.statusTv);
        }
    }

}
