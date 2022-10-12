package com.example.loginpage.Adapter;

import static com.example.loginpage.Activity.ChatActivity.rimage;
import static com.example.loginpage.Activity.ChatActivity.simage;

import android.content.Context;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.Activity.ChatActivity;
import com.example.loginpage.ModelClass.MessageModel;
import com.example.loginpage.R;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MessageModel>messageModelArrayList;
    public  static  final int ITEM_SEND=1;
    public  static  final int ITEM_RECIVE=2;

    String senderRoom;
    String receiverRoom;

    public MessageAdapter(Context context, ArrayList<MessageModel> messageModelArrayList,String senderRoom,String receiverRoom) {
        this.context = context;
        this.messageModelArrayList = messageModelArrayList;
        this.senderRoom=senderRoom;
        this.receiverRoom=receiverRoom;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==ITEM_SEND)
        {
            View view=LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return new SenderViewHolder(view);
        }else  {
            View view=LayoutInflater.from(context).inflate(R.layout.reciver_layout,parent,false);
            return new ReciverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel=messageModelArrayList.get(position);




        //reaction
        int reaction[]=new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry

        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reaction)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {

            if(pos < 0)
                return false;

            if(holder.getClass()==SenderViewHolder.class){
                SenderViewHolder viewHolder=(SenderViewHolder)holder;
                viewHolder.felling.setImageResource(reaction[pos]);
                viewHolder.felling.setVisibility(View.VISIBLE);
            }
            else
            {
                ReciverViewHolder viewHolder=(ReciverViewHolder) holder;
                viewHolder.felling.setImageResource(reaction[pos]);
                viewHolder.felling.setVisibility(View.VISIBLE);
            }
            messageModel.setFelling(pos);



          // FirebaseDatabase.getInstance().getReference()
          //         .child("chats")
          //         .child(senderRoom)
          //         .child("messages")
          //         .child(messageModel.getMessageId()).setValue(messageModel);


          // FirebaseDatabase.getInstance().getReference()
          //          .child("chats")
          //          .child(receiverRoom)
          //          .child("messages")
          //          .child(messageModel.getMessageId()).setValue(messageModel);
            return true; // true is closing popup, false is requesting a new selection
        });
        //end reaction


        if(holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder viewHolder=(SenderViewHolder)holder;
            viewHolder.stxtmessage.setText(messageModel.getMessage());
            Picasso.get().load(simage).into(viewHolder.scircleImageView);

            //time

           /* String senderId=FirebaseAuth.getInstance().getUid();
            String senderRoom=senderId+messageModel.getId() ;
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                  .child("messages")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                               // String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                                long time = snapshot.child("timestamp").getValue(Long.class);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                ( (SenderViewHolder)holder).stime.setText(dateFormat.format(new Date(time)));

                            }
                            else {
                                ((SenderViewHolder) holder).stime.setText("00:00");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });*/

            //end time

            //reaction
        if(messageModel.getFelling()>=0) {
            viewHolder.felling.setImageResource(reaction[(int) messageModel.getFelling()]);
            viewHolder.felling.setVisibility(View.VISIBLE);
        }else
        {
            viewHolder.felling.setVisibility(View.GONE);
        }

            viewHolder.stxtmessage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                  // Toast.makeText(context,messageModel.getMessageId(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            //end reaction

        }else{

            ReciverViewHolder viewHolder=(ReciverViewHolder) holder;
            viewHolder.rtxtmessage.setText(messageModel.getMessage());
            Picasso.get().load(rimage).into(viewHolder.rcircleImageView);


            //time

           /* String senderId=FirebaseAuth.getInstance().getUid();
            String senderRoom=senderId+messageModel.getId() ;
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                // String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                                long time = snapshot.child("timestamp").getValue(Long.class);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                ((ReciverViewHolder)holder).rtime.setText(dateFormat.format(new Date(time)));


                            }
                            else {
                                ((ReciverViewHolder) holder).rtime.setText("00:00");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });*/

            //end time

            //reaction
        if(messageModel.getFelling()>=0) {
            //messageModel.setFelling(reaction[(int) messageModel.getFelling()]);
            viewHolder.felling.setImageResource(reaction[(int) messageModel.getFelling()]);
            viewHolder.felling.setVisibility(View.VISIBLE);
        }else
        {
            viewHolder.felling.setVisibility(View.GONE);
        }


            viewHolder.rtxtmessage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });
            //end reaction

        }

    }

    @Override
    public int getItemCount() {
        return messageModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel messageModel=messageModelArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messageModel.getSenderId()))
        {
            return ITEM_SEND;
        }else {
            return ITEM_RECIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder{
        CircleImageView scircleImageView;
        TextView stxtmessage,stime;
        ImageView felling;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            scircleImageView=itemView.findViewById(R.id.simage);
            stxtmessage=itemView.findViewById(R.id.stxtMessage);
            felling=itemView.findViewById(R.id.feeling);
            stime=itemView.findViewById(R.id.time);


        }
    }

    class ReciverViewHolder extends RecyclerView.ViewHolder{
        CircleImageView rcircleImageView;
        TextView rtxtmessage,rtime;
        ImageView felling;


        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            rcircleImageView=itemView.findViewById(R.id.rimage);
            rtxtmessage=itemView.findViewById(R.id.rtxtMessage);
            felling=itemView.findViewById(R.id.feeling);
            rtime=itemView.findViewById(R.id.time);


        }
    }
}
