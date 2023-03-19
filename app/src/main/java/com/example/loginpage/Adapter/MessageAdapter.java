package com.example.loginpage.Adapter;

import static com.example.loginpage.Activity.ChatActivity.rimage;
import static com.example.loginpage.Activity.ChatActivity.simage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.SmsMessage;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.Activity.ChatActivity;
import com.example.loginpage.Activity.UserProfileActivity;
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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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



           FirebaseDatabase.getInstance().getReference()
                   .child("chats")
                   .child(senderRoom)
                   .child("messages")
                   .child(messageModel.getMessageId()).setValue(messageModel);


           FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(messageModel.getMessageId()).setValue(messageModel);
            return true; // true is closing popup, false is requesting a new selection
        });
        //end reaction


        if(holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder viewHolder=(SenderViewHolder)holder;

            //send image
            if(messageModel.getMessage().equals("Photo")){
                viewHolder.ssimage.setVisibility(View.VISIBLE);
                viewHolder.stxtmessage.setVisibility(View.GONE);
                Picasso.get().load(messageModel.getImageUrl()).placeholder(R.drawable.placeholder).into(viewHolder.ssimage);
            }
            //send image end

            viewHolder.stxtmessage.setText(messageModel.getMessage());
            Picasso.get().load(simage).into(viewHolder.scircleImageView);


            //convert time stamp
            long timetap=messageModel.getTimestamp();

            Calendar cal=Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(String.valueOf(timetap)));
            String dateTime= DateFormat.format("dd/M/yyyy hh:mm aa",cal).toString();

            viewHolder.date.setText(dateTime);


//delete msg
            viewHolder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure delete message");
                    builder.setPositiveButton("Delete for Me", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(messageModel.getMessageId()).setValue(null);
                            dialogInterface.dismiss();
                        }
                    });

                    builder.setNeutralButton("Delete for everyone", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            messageModel.setMessage("This message is removed");
                           // messageModel.setFeeling(-1);
                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(messageModel.getMessageId()).setValue(messageModel);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(messageModel.getMessageId()).setValue(messageModel);
                            dialogInterface.dismiss();

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                    return false;
                }

            });

            //delete msg


            //reaction
        if(messageModel.getFelling()>=0) {
            viewHolder.felling.setImageResource(reaction[(int) messageModel.getFelling()]);
            viewHolder.felling.setVisibility(View.VISIBLE);
        }else
        {
            viewHolder.felling.setVisibility(View.GONE);
        }


            viewHolder.scircleImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                  // Toast.makeText(context,messageModel.getMessageId(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            //end reaction


            if (position == messageModelArrayList.size()-1){
                if (messageModel.isIsseen()) {
                    viewHolder.seen.setText("Seen");

                }else {
                    viewHolder.seen.setText("Delivered");
                   // viewHolder.seen.setTextColor(R.color.red);
                }
            }else {
                viewHolder.seen.setVisibility(View.GONE);
            }

        }
        else



//..........................................................................


        {

            ReciverViewHolder viewHolder=(ReciverViewHolder) holder;
            //send image
            if(messageModel.getMessage().equals("Photo")){
                viewHolder.rrimage.setVisibility(View.VISIBLE);
                viewHolder.rtxtmessage.setVisibility(View.GONE);
                Picasso.get().load(messageModel.getImageUrl()).placeholder(R.drawable.placeholder).into(viewHolder.rrimage);

            }
            //send image end

            viewHolder.rtxtmessage.setText(messageModel.getMessage());
            Picasso.get().load(rimage).into(viewHolder.rcircleImageView);



            //delete msg
            viewHolder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure delete message");
                    builder.setPositiveButton("Delete For Me", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(messageModel.getMessageId()).setValue(null);
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                    return false;
                }

            });


            //delete msg end



            //reaction
        if(messageModel.getFelling()>=0) {
            //messageModel.setFelling(reaction[(int) messageModel.getFelling()]);
            viewHolder.felling.setImageResource(reaction[(int) messageModel.getFelling()]);
            viewHolder.felling.setVisibility(View.VISIBLE);
        }else
        {
            viewHolder.felling.setVisibility(View.GONE);
        }


            viewHolder.rcircleImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                  //  Toast.makeText(context,messageModel.getImageUrl(), Toast.LENGTH_SHORT).show();
                    return false;

                }
            });
            //end reaction

            //convert time stamp
            long timetap=messageModel.getTimestamp();

            Calendar cal=Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(String.valueOf(timetap)));
            String dateTime= DateFormat.format("dd/mm/yyyy hh:mm aa",cal).toString();

            viewHolder.rtime.setText(dateTime);


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
        TextView stxtmessage,seen,date;
        ImageView felling,ssimage;
        LinearLayout linearLayout;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            scircleImageView=itemView.findViewById(R.id.simage);
            stxtmessage=itemView.findViewById(R.id.stxtMessage);
            felling=itemView.findViewById(R.id.feeling);
            date=itemView.findViewById(R.id.date);
            ssimage=itemView.findViewById(R.id.ssimage);
            seen=itemView.findViewById(R.id.seen);
            linearLayout=itemView.findViewById(R.id.slinearLayout);


        }
    }

    class ReciverViewHolder extends RecyclerView.ViewHolder{
        CircleImageView rcircleImageView;
        TextView rtxtmessage,rtime,seen;
        ImageView felling,rrimage;
        LinearLayout linearLayout;



        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            rcircleImageView=itemView.findViewById(R.id.rimage);
            rtxtmessage=itemView.findViewById(R.id.rtxtMessage);
            felling=itemView.findViewById(R.id.feeling);
            rtime=itemView.findViewById(R.id.date);
            rrimage=itemView.findViewById(R.id.rrimage);
            seen=itemView.findViewById(R.id.seen);
            linearLayout=itemView.findViewById(R.id.rlinearLayout);



        }
    }
}
