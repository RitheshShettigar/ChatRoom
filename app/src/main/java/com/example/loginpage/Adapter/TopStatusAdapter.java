package com.example.loginpage.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.example.loginpage.Activity.HomeActivity;
import com.example.loginpage.ModelClass.Status;
import com.example.loginpage.ModelClass.UserStatus;
import com.example.loginpage.R;




import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;

public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder> {
    Context context;
    ArrayList<UserStatus>userStatuses;

    public TopStatusAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {

        UserStatus userStatus=userStatuses.get(position);

        holder.circular_status_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            //    ArrayList<StoryModel> uris = new ArrayList<>();


            }
        });

    }

    @Override
    public int getItemCount() {
       return  userStatuses.size();
    }

    public  class  TopStatusViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        CircularStatusView circular_status_view;


        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            circular_status_view=itemView.findViewById(R.id.circular_status_view);
        }
    }
}
