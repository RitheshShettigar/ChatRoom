package com.example.loginpage.Adapter;

import static com.example.loginpage.Activity.ChatActivity.simage;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginpage.ModelClass.GroupChatSendModel;
import com.example.loginpage.R;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatSendAdapter extends RecyclerView.Adapter<GroupChatSendAdapter.Viewholder> {

    private static final int MSG_TYPE_left=0;
    private static final int MSG_TYPE_right=1;

    Context context;
    ArrayList<GroupChatSendModel> groupChatSendModellArrayList;

    FirebaseAuth mAuth;


    public GroupChatSendAdapter(Context context, ArrayList<GroupChatSendModel> groupChatSendModellArrayList) {
        this.context=context;
        this.groupChatSendModellArrayList=groupChatSendModellArrayList;

        mAuth=FirebaseAuth.getInstance();

    }





    @NonNull
    @Override
    public GroupChatSendAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_right){
            View view= LayoutInflater.from(context).inflate(R.layout.row_group_right,parent,false);
            return  new Viewholder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.row_group_left,parent,false);
            return  new Viewholder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatSendAdapter.Viewholder holder, int position) {
        GroupChatSendModel model=groupChatSendModellArrayList.get(position);

       String messagetype=model.getType();
        String senderId=model.getSender();
        String timetap=model.getTimestamp();
        String message=model.getMessage();



        if(messagetype.equals("text")){
            //text message
            holder.simageTv.setVisibility(View.GONE);
            holder.messageTv.setVisibility(View.VISIBLE);
            holder.messageTv.setText(message);
        }
        else {
            //image
            holder.simageTv.setVisibility(View.VISIBLE);
            holder.messageTv.setVisibility(View.GONE);
            try {
                Picasso.get().load(message).placeholder(R.drawable.avatar).into(holder.simageTv);
            }catch (Exception e){
                holder.simageTv.setImageResource(R.drawable.avatar);
            }
        }


        //convert time stamp
        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timetap));
        String dateTime= DateFormat.format("dd/M/yyyy hh:mm aa",cal).toString();



        holder.messageTv.setText(model.getMessage());
        holder.timeTv.setText(dateTime);

        setUsername(model,holder);

    }

    private void setUsername(GroupChatSendModel model, Viewholder holder) {
        //get sender into from uid in model
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("User");
        ref.orderByChild("id").equalTo(model.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String name=""+ds.child("username").getValue();
                            String image=""+ds.child("imageuri").getValue();

                            holder.nameTv.setText(name);


                            holder.nameTv.setVisibility(View.VISIBLE);


                            try {
                                Picasso.get().load(image).into(holder.imageTv);

                            }catch (Exception e){
                              holder.imageTv.setImageResource(R.drawable.avatar);

                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return groupChatSendModellArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(groupChatSendModellArrayList.get(position).getSender().equals(mAuth.getUid())){
            return  MSG_TYPE_right;
        }
        else
        {
            return  MSG_TYPE_left;
        }

    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView nameTv,messageTv,timeTv;
        CircleImageView imageTv;
        ImageView simageTv;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            nameTv=itemView.findViewById(R.id.snameTv);
            messageTv=itemView.findViewById(R.id.smessaheTv);
            timeTv=itemView.findViewById(R.id.sTimeTv);
            imageTv=itemView.findViewById(R.id.grimage);
            simageTv=itemView.findViewById(R.id.image);

        }
    }
}
