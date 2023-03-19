package com.example.loginpage.Adapter;

import static android.media.CamcorderProfile.get;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginpage.Activity.PostDonloadActivity;
import com.example.loginpage.Activity.UserProfile;
import com.example.loginpage.ModelClass.FriendHomemodel;
import com.example.loginpage.ModelClass.PostModel;
import com.example.loginpage.ModelClass.modelUser;
import com.example.loginpage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.BiMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {
    Context context;
    ArrayList<PostModel>postModelArrayList;



    DatabaseReference LikeRef,CommentRef;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    String username,userProfile;





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



        holder.UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, UserProfile.class);
                context.startActivity(intent);

            }
        });


        mAuth=FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser =mAuth.getInstance().getCurrentUser();

//retrieve username image
        DatabaseReference reference=database.getReference().child("User").child(mAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username=snapshot.child("username").getValue().toString();
                userProfile=snapshot.child("imageuri").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });





     CommentRef= FirebaseDatabase.getInstance().getReference().child("PostComment");
     LikeRef= FirebaseDatabase.getInstance().getReference().child("Like");
     String postkey=postModelArrayList.get(position).getPostid();



     String timeAgo=calculateTimeAgo(postModel.getDate());
     holder.time.setText(timeAgo);
     holder.name.setText(postModel.getUserName());
     holder.about.setText(postModel.getPostDesc());
     Glide.with(context).load(postModelArrayList.get(position).getPostImage()).into(holder.post);
     Glide.with(context).load(postModelArrayList.get(position).getUserProfile()).into(holder.UserImage);

        //count post
        holder.likeCount(postkey,mUser.getUid(),LikeRef);
        //count post end


    //like post
     holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LikeRef.child(postkey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            LikeRef.child(postkey).child(mUser.getUid()).removeValue();
                            holder.like.setColorFilter(Color.GRAY);
                            notifyDataSetChanged();
                        }
                        else {
                            LikeRef.child(postkey).child(mUser.getUid()).setValue("like");
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


     //post download
     holder.post.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Intent intent=new Intent(context, PostDonloadActivity.class);
             intent.putExtra("posturl",postModel.getPostImage());
             context.startActivity(intent);

         }
     });
        //post download end


  //send comment
        holder.sendcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment=holder.inputComment.getText().toString();
                if(comment.isEmpty())
                {
                    Toast.makeText(context, "Please write comment", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AddComment(holder,postkey,CommentRef,mUser.getUid(),comment);
                }
            }
        });
        //send comment end



        //spinner
        if(mUser.getUid().equals(postModel.getUserid())){
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context.getApplicationContext(),R.array.menus, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner.setAdapter(adapter);
        }else {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context.getApplicationContext(),R.array.menus2, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner.setAdapter(adapter);
        }

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choic = parent.getItemAtPosition(position).toString();
                holder.spinner.clearFocus();

                if (position==0){
                   Toast.makeText(context.getApplicationContext(), "menu",Toast.LENGTH_SHORT).show();


                }else if (position==1){
                    if(mUser.getUid().equals(postModel.getUserid())){
                        //Toast.makeText(mcontext.getApplicationContext(), "b",Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference("Post").child(postModel.getPostid()).removeValue();
                        Toast.makeText(context.getApplicationContext(), "Post Deleted",Toast.LENGTH_SHORT).show();

                    }


                }else if (position==2){
                    BitmapDrawable bitmapDrawable=(BitmapDrawable)holder.post.getDrawable();
                    if(bitmapDrawable==null){
                        //post without image
                       // shareTextonly();
                    }else
                    {
                        //post with image
                        Bitmap bitmap=bitmapDrawable.getBitmap();
                        shareImageAndText(bitmap);
                    }
                    Toast.makeText(context.getApplicationContext(), "share",Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //spinner

  }

    private void shareImageAndText(Bitmap bitmap) {
        Uri uri=saveImagetoShare(bitmap);

        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        intent.setType("image/png");
        context.startActivity(Intent.createChooser(intent,"Share Via"));
    }

    private Uri saveImagetoShare(Bitmap bitmap) {
        File imageFloader=new File(context.getCacheDir(),"image");
        Uri uri=null;
        try{
            imageFloader.mkdirs();
            File file=new File(imageFloader,"shared_image.png");

            FileOutputStream stream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
            stream.flush();
            stream.close();
            uri= FileProvider.getUriForFile(context,"com.example.loginpage.MyFirebaseService.fileprovider",file);

        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return uri;
    }

    private void shareTextonly() {

        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
       // intent.putExtra(Intent.EXTRA_TEXT)
        //context.startActivities(Intent.createChooser(intent,"Share Via"));
    }


    //send comment
    private void AddComment(Viewholder holder, String postkey, DatabaseReference commentRef, String uid, String comment) {
        HashMap hashMap = new HashMap();
        hashMap.put("comment", comment);
        hashMap.put("username", username);
        hashMap.put("profile", userProfile);
        //hashMap.put("Postkey", postkey);
        commentRef.child(postkey).child(uid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Comment Add", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    holder.inputComment.setText(null);
                } else {
                    Toast.makeText(context, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        //send comment end

    }


    private String calculateTimeAgo(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
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

        ImageView post,like,comment,sendcomment;
        CircleImageView UserImage;
        TextView name,time,about,likeCount,commentCount;
       EditText inputComment;
       Spinner spinner;



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
           commentCount=itemView.findViewById(R.id.commentCount);
           inputComment=itemView.findViewById(R.id.commentAdd);
            sendcomment=itemView.findViewById(R.id.sendComment);
            spinner = itemView.findViewById(R.id.spinner);
        }

//count post
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
                        like.setColorFilter(Color.BLUE);
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
