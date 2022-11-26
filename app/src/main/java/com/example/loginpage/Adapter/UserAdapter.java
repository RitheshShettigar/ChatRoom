package com.example.loginpage.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginpage.Activity.ChatActivity;
import com.example.loginpage.Activity.PostDonloadActivity;
import com.example.loginpage.Activity.ViewFriendActivity;
import com.example.loginpage.ModelClass.modelUser;
import com.example.loginpage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder>{
    Context context;
    ArrayList<modelUser> userlist;


    public UserAdapter(Context context, ArrayList<modelUser> userlist) {
        this.context=context;
        this.userlist=userlist;

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_conversation,parent,false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        modelUser modelUser=userlist.get(position);

      //  String senderId=FirebaseAuth.getInstance().getUid();

      //  String senderRoom=senderId+modelUser.getId();

      /*  FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            long time = snapshot.child("lastmsgtime").getValue(Long.class);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                            holder.msgTime.setText(dateFormat.format(new Date(time)));

                            holder.lastMsg.setText(lastMsg);


                        }
                        else {
                            holder.lastMsg.setText("Tap to chat");
                            holder.msgTime.setText("00:00");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(modelUser.getId()))
        {
            holder.itemView.setVisibility(View.GONE);
        }

        holder.Username.setText(modelUser.getUsername());
        Glide.with(context).load(userlist.get(position).getImageuri()).into(holder.Image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ViewFriendActivity.class);
                intent.putExtra("name",modelUser.getUsername());
                intent.putExtra("ReciverImage",modelUser.getImageuri());
                intent.putExtra("uid",modelUser.getId());
                context.startActivity(intent);

            }
        });

        holder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PostDonloadActivity.class);
                intent.putExtra("posturl",modelUser.getImageuri());
                context.startActivity(intent);

            }
        });


    }



    @Override
    public int getItemCount() {
        return userlist.size();
    }
    class Viewholder extends RecyclerView.ViewHolder{
        CircleImageView Image;
        TextView Username;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            Image=itemView.findViewById(R.id.profile);
            Username=itemView.findViewById(R.id.username1);




        }

    }
}
