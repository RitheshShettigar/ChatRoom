package com.example.loginpage.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginpage.ModelClass.FriendHomemodel;
import com.example.loginpage.ModelClass.PostModel;
import com.example.loginpage.ModelClass.modelUser;
import com.example.loginpage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {
    Context context;
    ArrayList<PostModel>postModelArrayList;

    DatabaseReference LikeRef;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    FirebaseDatabase database;



    public PostAdapter(Context context, ArrayList<PostModel> postModelArrayList) {
        this.context=context;
        this.postModelArrayList=postModelArrayList;

    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_post,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
     PostModel postModel=postModelArrayList.get(position);



//        mUser=mAuth.getCurrentUser();
       // mAuth=FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser =mAuth.getInstance().getCurrentUser();


       //LikeRef= FirebaseDatabase.getInstance().getReference().child("Like");
     //
        //   String postkey=getRef(position).getKey();

       // String postKey= postModel.getGetkey().getKey();
        String timeAgo=calculateTimeAgo(postModel.getDate());
        holder.time.setText(timeAgo);
        holder.name.setText(postModel.getUserName());
        holder.about.setText(postModel.getPostDesc());
        Glide.with(context).load(postModelArrayList.get(position).getPostImage()).into(holder.post);
        Glide.with(context).load(postModelArrayList.get(position).getUserProfile()).into(holder.UserImage);

      //islike(postModel.getPostid(),holder.like);
      //nulike(holder.likeCount,postModel.getPostid());

     holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Toast.makeText(mcontext.getApplicationContext(), "clike",Toast.LENGTH_SHORT).show();
              //  if (holder.like.getTag().equals("likes")){
              //      FirebaseDatabase.getInstance().getReference("PostLike").child(postModel.getPostid()).child(mUser.getUid()).setValue(true);

              //  }else {
              //      FirebaseDatabase.getInstance().getReference("PostLike").child(postModel.getPostid()).removeValue();
              //  }


                //holder.like.setColorFilter(Color.BLUE);
          //LikeRef.child(postKey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
          //    @Override
          //    public void onDataChange(@NonNull DataSnapshot snapshot) {
          //        if(snapshot.exists())
          //        {
          //            LikeRef.child(postKey).child(mUser.getUid()).removeValue();
          //            holder.like.setColorFilter(Color.GRAY);
          //            notifyDataSetChanged();
          //        }else
          //        {
          //            LikeRef.child(postKey).child(mUser.getUid()).setValue("Like");
          //            holder.like.setColorFilter(Color.BLUE);
          //            notifyDataSetChanged();
          //        }

          //    }


          //    @Override
          //    public void onCancelled(@NonNull DatabaseError error) {
          //        Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

          //    }
          //});

            }
        });

   holder.comment.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           holder.lin5.setVisibility(View.VISIBLE);
       }
   });

  }



    private String calculateTimeAgo(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(date).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return  ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return postModelArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        ImageView post,like,comment;
        CircleImageView UserImage;
        TextView name,time,about,likeCount,commentCount;
        LinearLayout lin5;



        public Viewholder(@NonNull View itemView) {
            super(itemView);

            post=itemView.findViewById(R.id.post);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            UserImage=itemView.findViewById(R.id.UserImage);
            name=itemView.findViewById(R.id.name);
            time=itemView.findViewById(R.id.time);
            about=itemView.findViewById(R.id.about);
            likeCount=itemView.findViewById(R.id.likeCount);
            commentCount=itemView.findViewById(R.id.commentCount);
            lin5=itemView.findViewById(R.id.lin5);
        }
    }
    private  void  islike(String postid,ImageView imageView){
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("PostLike").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.like);
                    imageView.setTag("like");

                }else {
                    imageView.setImageResource(R.drawable.like1);
                    imageView.setTag("likes");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void  nulike(final TextView likecount, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("PostLike").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               likecount .setText(snapshot.getChildrenCount()+"likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
