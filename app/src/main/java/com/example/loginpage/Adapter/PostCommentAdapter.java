package com.example.loginpage.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginpage.ModelClass.Commentmodel;
import com.example.loginpage.ModelClass.MemberReels;
import com.example.loginpage.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.Viewholder> {

    Context context;
    ArrayList<Commentmodel> commentmodelArrayList;

    public PostCommentAdapter(Context context, ArrayList<Commentmodel> commentmodelArrayList) {
        this.context=context;
        this.commentmodelArrayList=commentmodelArrayList;

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_comment,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Commentmodel commentmodel=commentmodelArrayList.get(position);

        holder.name.setText(commentmodel.getUsername());
        Glide.with(context).load(commentmodelArrayList.get(position).getProfile()).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return commentmodelArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name,comment;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.UserImagecomment);
            name=itemView.findViewById(R.id.name1);
            comment=itemView.findViewById(R.id.time1
            );

        }
    }
}
