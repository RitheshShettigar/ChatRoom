package com.example.loginpage.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginpage.Activity.ChatActivity;
import com.example.loginpage.Activity.PostDonloadActivity;
import com.example.loginpage.Activity.ViewFriendActivity;
import com.example.loginpage.ModelClass.FriendHomemodel;
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

public class FriendHomeAdapter  extends RecyclerView.Adapter<FriendHomeAdapter.Viewholder>{
    Context context;
    ArrayList<FriendHomemodel>listf;




    public FriendHomeAdapter(Context context, ArrayList<FriendHomemodel>friendHomemodelArrayList) {
        this.context=context;
        this.listf=friendHomemodelArrayList;

    }

    @NonNull
    @Override
    public FriendHomeAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_coversation2,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendHomeAdapter.Viewholder holder, int position) {
        FriendHomemodel friendHomemodel=listf.get(position);

        String senderId= FirebaseAuth.getInstance().getUid();

        String senderRoom=senderId+friendHomemodel.getId();

        FirebaseDatabase.getInstance().getReference()
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
                });
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals( friendHomemodel.getId()))
        { holder.itemView.setVisibility(View.GONE);
        }

        holder.Username.setText(friendHomemodel.getUsername());
        Glide.with(context).load(listf.get(position).getImageuri()).into(holder.Image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("name",friendHomemodel.getUsername());
                intent.putExtra("ReciverImage", friendHomemodel.getImageuri());
                intent.putExtra("uid", friendHomemodel.getId());
                intent.putExtra("token", friendHomemodel.getToken());

                context.startActivity(intent);
                //Toast.makeText(context, friendHomemodel.getUsername(), Toast.LENGTH_SHORT).show();

            }
        });

        holder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PostDonloadActivity.class);
                intent.putExtra("posturl",friendHomemodel.getImageuri());
                context.startActivity(intent);

            }
        });


    /*  if (ischat){
            holder.online_on.setVisibility(View.GONE);
            holder.online_off.setVisibility(View.GONE);
        }else {
            if (friendHomemodel.getStatus().equals("Online")){
                holder.online_on.setVisibility(View.VISIBLE);
                holder.online_off.setVisibility(View.GONE);
               // online=true;
            }else {
                holder.online_on.setVisibility(View.GONE);
                holder.online_off.setVisibility(View.VISIBLE);
               // online=false;
            }
        }*/


    }



    @Override
    public int getItemCount() {
        return listf.size();
    }
    class Viewholder extends RecyclerView.ViewHolder{
        CircleImageView Image,online_on,online_off;
        TextView Username,lastMsg,msgTime;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            Image=itemView.findViewById(R.id.fprofile);
            Username=itemView.findViewById(R.id.fusername1);
            lastMsg=itemView.findViewById(R.id.lastMsg);
            msgTime=itemView.findViewById(R.id.msgTime);
            online_on=itemView.findViewById(R.id.online_on);
            online_off=itemView.findViewById(R.id.online_off);



        }

    }
}

