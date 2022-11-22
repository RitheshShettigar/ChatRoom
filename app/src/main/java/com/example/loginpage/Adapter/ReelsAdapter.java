package com.example.loginpage.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginpage.ModelClass.MemberReels;
import com.example.loginpage.ModelClass.PostModel;
import com.example.loginpage.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.Viewholder>{
    Context context;
    ArrayList<MemberReels> memberReelsArrayList;

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



    }

    @Override
    public int getItemCount() {
        return memberReelsArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        CircleImageView UserImage;
        VideoView reelsvideo;
        TextView username,title;
        ProgressBar progressBar;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            reelsvideo=itemView.findViewById(R.id.video);
            UserImage=itemView.findViewById(R.id.UserImage);
            username=itemView.findViewById(R.id.username);
            title=itemView.findViewById(R.id.videotitle);
            progressBar=itemView.findViewById(R.id.videoProgress);
        }

    }
}
