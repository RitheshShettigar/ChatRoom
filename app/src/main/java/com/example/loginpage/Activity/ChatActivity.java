package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.loginpage.Adapter.MessageAdapter;
import com.example.loginpage.ModelClass.MessageModel;
import com.example.loginpage.ModelClass.modelUser;
import com.example.loginpage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.text.UStringsKt;

public class ChatActivity extends AppCompatActivity {

    String ReciverImage,ReciverUid,ReciverName,SenderUid,sendername;
    CircleImageView Image;
    TextView Name,status;
    ImageView attachment,camera;
   ImageView Sender;
    EditText msgtype1;
    Toolbar tollbar;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser mUser;
    public static String simage;
    public static String rimage;
    private  static final int PERMISSION_CODE=22;
    ValueEventListener seenListener;


    String senderRoom,reciverRoom;

   // RequestQueue requestQueue;

    RecyclerView recyclerView;
    ArrayList<MessageModel>messageModelArrayList;
    MessageAdapter adapter;

    Uri imageUri;
    FirebaseStorage storage;
    ProgressDialog prog4;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        prog4=new ProgressDialog(this);
        prog4.setMessage("Uploading fill....");
        prog4.setCancelable(false);

      //  requestQueue = Volley.newRequestQueue(this);

      //  getSupportActionBar().setDisplayShowTitleEnabled(false);

        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();

        Sender=findViewById(R.id.sender);
        msgtype1=findViewById(R.id.msgtype);
        attachment=findViewById(R.id.attachment);
        camera=findViewById(R.id.camera);
      //  imageView2=findViewById(R.id.imageView2);
        status=findViewById(R.id.status);

        tollbar=findViewById(R.id.toolbar);
        setSupportActionBar(tollbar);


        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        tollbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChatActivity.this,UserProfile.class);
                intent.putExtra("uid",ReciverUid);
                intent.putExtra("name",ReciverName);
                intent.putExtra("profile",ReciverImage);
                startActivity(intent);

            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent();
              //  intent.setType("image/*");
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
              //  startActivityForResult(intent,100);

            }
        });


        Intent intent=getIntent();

        ReciverName=intent.getStringExtra("name");
        ReciverImage=getIntent().getStringExtra("ReciverImage");
        ReciverUid=getIntent().getStringExtra("uid");
        String token=getIntent().getStringExtra("token");
       // Toast.makeText(this, token, Toast.LENGTH_SHORT).show();


        Image=findViewById(R.id.image);
        Name=findViewById(R.id.username4);
        Picasso.get().load(ReciverImage).into(Image);

        Name.setText(ReciverName);

        SenderUid=auth.getUid();

        senderRoom=SenderUid+ReciverUid;
        reciverRoom=ReciverUid+SenderUid;


        recyclerView=findViewById(R.id.messageadapter1);

        recyclerView.setHasFixedSize(true);
      //  recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageModelArrayList= new ArrayList<>();
        adapter=new MessageAdapter(this,messageModelArrayList,senderRoom,reciverRoom);
        recyclerView.setAdapter(adapter);
        //LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
      //  linearLayoutManager.setStackFromEnd(true);




        DatabaseReference reference=database.getReference().child("User").child(auth.getUid());
        DatabaseReference chatreference=database.getReference().child("chats").child(senderRoom).child("messages");


        //status online offline
        database.getReference().child("presence").child(ReciverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String Status=snapshot.getValue(String.class);

                    if(!Status.isEmpty()){
                        if(Status.equals("offline")){
                            status.setVisibility(View.GONE);
                           // Toast.makeText(ChatActivity.this, Status, Toast.LENGTH_SHORT).show();
                        }else {
                            status.setText(Status);
                            status.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //status online offline end

        //typing show
        final Handler handler = new Handler();
        msgtype1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
              database.getReference().child("presence").child(SenderUid).setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping,1000);

            }

            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(SenderUid).setValue("Online");
                }
            };
        });
        //typing show end


        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messageModelArrayList.clear();

                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    MessageModel messageModel=snapshot1.getValue(MessageModel.class);
                    messageModel.setMessageId(snapshot1.getKey());
                    messageModelArrayList.add(messageModel);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              simage= snapshot.child("imageuri").getValue().toString();
                sendername=snapshot.child("username").getValue().toString();
              rimage=ReciverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        Sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=msgtype1.getText().toString();
              // if(message.isEmpty())
              // {
              //     Toast.makeText(ChatActivity.this, "Please enter valid message", Toast.LENGTH_SHORT).show();
              //     return;
              // }
                msgtype1.setText("");
                Date date=new Date();
                MessageModel messageModel=new MessageModel(message,SenderUid,date.getTime());

                String randomkey=database.getReference().push().getKey();

                HashMap<String,Object>lastMagObj=new HashMap<>();
                lastMagObj.put("lastMsg",messageModel.getMessage());
                lastMagObj.put("lastmsgtime",date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMagObj);
                database.getReference().child("chats").child(reciverRoom).updateChildren(lastMagObj);

                database=FirebaseDatabase.getInstance();
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomkey)
                        .setValue(messageModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats")
                                .child(reciverRoom)
                                .child("messages")
                                .child(randomkey)
                                .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                sendNotification(ReciverName, messageModel.getMessage(),token);

                            }
                        });


                    }
                });
            }
        });
        //attachment go to gallery
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent,25);

            }
        });
        //attachment go to gallery end

       // SeenMessage(SenderUid);

    }





    //message seen
  /*  private  void  SeenMessage(final String SenderUi){
        DatabaseReference reference=database.getReference().child("User");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    MessageModel messageModel = snapshot1.getValue(MessageModel.class);
                    if(messageModel.getId().equals(auth.getUid())&& messageModel.getSenderId().equals(SenderUid)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot1.getRef().updateChildren(hashMap);
                        //adapter=new MessageAdapter(ChatActivity.this,messageModel.getSenderId(),messageModel.getId());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }*/

    //message seen end



    //send msg notification
    void sendNotification(String name,String message,String token){
        try {
            RequestQueue queue = Volley.newRequestQueue(this);

            String url="https://fcm.googleapis.com/fcm/send";


            JSONObject data = new JSONObject();
            data.put("title",sendername);
            data.put("body", message);
            JSONObject notificationData = new JSONObject();
            notificationData.put("notification", data);
            notificationData.put("to", token);

            JsonObjectRequest request = new JsonObjectRequest(url, notificationData
                    , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(ChatActivity.this, "Success", Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ChatActivity.this, "error", Toast.LENGTH_SHORT).show();

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    String key = "Key=AAAAdwpeKG0:APA91bGXvDy24ofwDlxH4nTqAoe_mDnXW9OeujYV_kuQkD1FLeWipsqROp4paYBJtxstXj7RyZWM59tLxaYPY_2ZcU9oMT9--v7o1sAsuXo2XRuCrEI_6Cy3gNN7VfbI-ivK48rjdrOK";
                    map.put("Content-Type", "application/json");
                    map.put("Authorization", key);

                    return map;
                }
            };
            queue.add(request);
        }catch (Exception ex){

        }
    }
    //send msg notification end

    //photo add
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==25){
            if(data!=null){
                Uri selectedImage=data.getData();
                Calendar calendar=Calendar.getInstance();
                StorageReference reference=storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                prog4.show();
                reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        prog4.dismiss();
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String fillpath=uri.toString();

                                    String message=msgtype1.getText().toString();
                                 //  if(message.isEmpty())
                                 //  {
                                 //      Toast.makeText(ChatActivity.this, "Please enter valid message", Toast.LENGTH_SHORT).show();
                                 //      return;
                                 //  }

                                    Date date=new Date();
                                    MessageModel messageModel=new MessageModel(message,SenderUid,date.getTime());
                                    messageModel.setMessage("Photo");
                                    messageModel.setImageUrl(fillpath);
                                    msgtype1.setText("");

                                    String randomkey=database.getReference().push().getKey();

                                    HashMap<String,Object>lastMagObj=new HashMap<>();
                                    lastMagObj.put("lastMsg",messageModel.getMessage());
                                    lastMagObj.put("lastmsgtime",date.getTime());

                                    database.getReference().child("chats").child(senderRoom).updateChildren(lastMagObj);
                                    database.getReference().child("chats").child(reciverRoom).updateChildren(lastMagObj);

                                    database=FirebaseDatabase.getInstance();
                                    database.getReference().child("chats")
                                            .child(senderRoom)
                                            .child("messages")
                                            .child(randomkey)
                                            .setValue(messageModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            database.getReference().child("chats")
                                                    .child(reciverRoom)
                                                    .child("messages")
                                                    .child(randomkey)
                                                    .setValue(messageModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                }
                                            });

                                        }
                                    });

                                   // Toast.makeText(ChatActivity.this, fillpath, Toast.LENGTH_SHORT).show();


                                }
                            });
                        }
                    }
                });
            }
        }


    }
    //photo add end

    //status online offline
    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
       // reference.removeEventListener(seenListener);
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }
    //status online offline end



    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId=item.getItemId();

        if(itemId==R.id.profile){
            Toast.makeText(this, "menu", Toast.LENGTH_SHORT).show();

        }

        if(itemId==R.id.unfriend){
            Intent intent=new Intent(ChatActivity.this,ViewFriendActivity.class);
            intent.putExtra("uid",ReciverUid);
           // Toast.makeText(this, mUser.getUid(), Toast.LENGTH_SHORT).show();
            startActivity(intent);

        }
        if(itemId==R.id.profile){
            Intent intent=new Intent(ChatActivity.this,UserProfile.class);
            intent.putExtra("uid",ReciverUid);
            intent.putExtra("name",ReciverName);
            intent.putExtra("profile",ReciverImage);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }


}