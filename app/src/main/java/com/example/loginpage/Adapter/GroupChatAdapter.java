package com.example.loginpage.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.Activity.GroupChatActivity;
import com.example.loginpage.ModelClass.GroupChatModel;
import com.example.loginpage.ModelClass.MemberReels;
import com.example.loginpage.R;
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

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.Viewholder>{

    Context context;
    ArrayList<GroupChatModel> groupChatModelArrayList;

    public GroupChatAdapter(Context context, ArrayList<GroupChatModel> groupChatModelArrayList) {
        this.context=context;
        this.groupChatModelArrayList=groupChatModelArrayList;

    }


    @NonNull
    @Override
    public GroupChatAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_group_list,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatAdapter.Viewholder holder, int position) {
        GroupChatModel groupChatModel=groupChatModelArrayList.get(position);

        String groupId=groupChatModel.getGroupId();
        String groupIcon=groupChatModel.getGroupIcon();
        String GroupTitle=groupChatModel.getGroupTitle();

        holder.nametv.setText("");
        holder.timetv.setText("");
        holder.messagetv.setText("");



        //load last msg-time
        loadLstMessage(groupChatModel,holder);

        holder.groupTitle.setText(GroupTitle);
        try{
            Picasso.get().load(groupIcon).placeholder(R.drawable.user).into(holder.groupIcon);
        }catch (Exception e){
            holder.groupIcon.setImageResource(R.drawable.user);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId",groupId);
                context.startActivity(intent);

            }
        });
    }

    private void loadLstMessage(GroupChatModel groupChatModel, Viewholder holder) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupChatModel.getGroupId()).child("Message").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String message=""+ds.child("message").getValue();
                            String timestamp=""+ds.child("timestamp").getValue();
                            String sender=""+ds.child("sender").getValue();
                            String messagetype=""+ds.child("type").getValue();

                            //convert time stamp
                            Calendar cal=Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(timestamp));
                            String dateTime= DateFormat.format("hh:mm:ss aa",cal).toString();

                            if(messagetype.equals("image")){
                                holder.messagetv.setText("Sent Photo");

                            }
                            else
                            {
                                holder.messagetv.setText(message);
                            }


                            holder.timetv.setText(dateTime);

                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("User");
                            ref.orderByChild("id").equalTo(sender)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot ds:snapshot.getChildren()){
                                                String name=""+ds.child("username").getValue();

                                                holder.nametv.setText(name);
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
    public int getItemCount() {
        return groupChatModelArrayList.size();
    }
     class Viewholder extends RecyclerView.ViewHolder {

        CircleImageView groupIcon;
        TextView groupTitle,nametv,messagetv,timetv;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            groupIcon=itemView.findViewById(R.id.groupIcon);
            groupTitle=itemView.findViewById(R.id. groupTitle);
            nametv=itemView.findViewById(R.id.nametv);
            messagetv=itemView.findViewById(R.id.messagetv);
            timetv=itemView.findViewById(R.id.timetv);
        }
    }
}
