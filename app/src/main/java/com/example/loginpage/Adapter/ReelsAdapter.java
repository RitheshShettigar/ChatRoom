package com.example.loginpage.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginpage.Activity.UserProfile;
import com.example.loginpage.ModelClass.MemberReels;
import com.example.loginpage.ModelClass.PostModel;
import com.example.loginpage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.Viewholder>{
    Context context;
    ArrayList<MemberReels> memberReelsArrayList;

    DatabaseReference LikeRef;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    public ReelsAdapter(Context context, ArrayList<MemberReels> memberReelslArrayList) {
        this.context=context;
        this.memberReelsArrayList=memberReelslArrayList;

    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_reels_row,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        MemberReels memberReelsl=memberReelsArrayList.get(position);

        holder.UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, UserProfile.class);
                context.startActivity(intent);

            }
        });

        mAuth= FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser =mAuth.getInstance().getCurrentUser();

        LikeRef= FirebaseDatabase.getInstance().getReference().child("ReelsLike");
        String reelskey=memberReelsArrayList.get(position).getReelsid();

        holder.username.setText(memberReelsl.getUserName());
        holder.title.setText(memberReelsl.getTitle());
        Glide.with(context).load(memberReelsArrayList.get(position).getUserProfile()).into(holder.UserImage);

        holder.reelsvideo.setVideoPath(memberReelsl.getVideouri());

       holder. reelsvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
               holder.progressBar.setVisibility(View.GONE);
                mediaPlayer.start();
            }
        });
       holder. reelsvideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        //count post
        holder.likeCount(reelskey,mUser.getUid(),LikeRef);
        //count post end


        //like post
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LikeRef.child(reelskey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            LikeRef.child(reelskey).child(mUser.getUid()).removeValue();
                            holder.like.setColorFilter(Color.GRAY);
                            notifyDataSetChanged();
                        }
                        else {
                            LikeRef.child(reelskey).child(mUser.getUid()).setValue("like");
                            holder.like.setColorFilter(Color.BLUE);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });
        //like post end

        //spinner
        if(mUser.getUid().equals(memberReelsl.getUserid())){
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context.getApplicationContext(),R.array.menus, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner1.setAdapter(adapter);
        }else {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context.getApplicationContext(),R.array.menus2, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner1.setAdapter(adapter);
        }

        holder.spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // String choic = parent.getItemAtPosition(position).toString();
                holder.spinner1.clearFocus();

                if (position==0){

                }else if (position==1){
                    if(mUser.getUid().equals(memberReelsl.getUserid())){
                        //Toast.makeText(mcontext.getApplicationContext(), "b",Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference("Reels").child(memberReelsl.getReelsid()).removeValue();
                        Toast.makeText(context.getApplicationContext(), "Reels Deleted",Toast.LENGTH_SHORT).show();

                    }


                }else if (position==2){
                    //Toast.makeText(context.getApplicationContext(), "c",Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //spinner




    }

    @Override
    public int getItemCount() {
        return memberReelsArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        CircleImageView UserImage;
        VideoView reelsvideo;
        TextView username,title,likeCount;
        ProgressBar progressBar;
        ImageView like;
        Spinner spinner1;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            reelsvideo=itemView.findViewById(R.id.video);
            UserImage=itemView.findViewById(R.id.UserImage);
            username=itemView.findViewById(R.id.username);
            title=itemView.findViewById(R.id.videotitle);
            progressBar=itemView.findViewById(R.id.videoProgress);
            like=itemView.findViewById(R.id.like);
            likeCount=itemView.findViewById(R.id.likecount);
            spinner1 = itemView.findViewById(R.id.spiner1);
        }

        //count reels like
        public void likeCount(String postkey, String uid, DatabaseReference likeRef) {
            likeRef.child(postkey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        int totalLikes=(int)snapshot.getChildrenCount();
                        likeCount.setText(totalLikes+"");
                    }else
                    {
                        likeCount.setText("0");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            likeRef.child(postkey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(uid).exists())
                    {
                        like.setColorFilter(Color.RED
                        );
                    }else
                    {
                        like.setColorFilter(Color.GRAY);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //count post end

    }
}
