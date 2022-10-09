package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.Adapter.MessageAdapter;
import com.example.loginpage.ModelClass.MessageModel;
import com.example.loginpage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReciverImage,ReciverUid,ReciverName,SenderUid;
    CircleImageView Image;
    TextView Name;
    FirebaseDatabase database;
    FirebaseAuth auth;
    public static String simage;
    public static String rimage;

    CardView Sender;
    EditText msgtype1;

    String senderRoom,reciverRoom;

    //RecyclerView messageAdapter;
    RecyclerView recyclerView;
    ArrayList<MessageModel>messageModelArrayList;

    MessageAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

        Sender=findViewById(R.id.sender);
        msgtype1=findViewById(R.id.msgtype);



       recyclerView=findViewById(R.id.messageadapter1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageModelArrayList= new ArrayList<>();
        adapter=new MessageAdapter(this,messageModelArrayList,senderRoom,reciverRoom);
        recyclerView.setAdapter(adapter);



        Intent intent=getIntent();

        ReciverName=intent.getStringExtra("name");
        ReciverImage=getIntent().getStringExtra("ReciverImage");
        ReciverUid=getIntent().getStringExtra("uid");


        Image=findViewById(R.id.image);
        Name=findViewById(R.id.username4);
        Picasso.get().load(ReciverImage).into(Image);

        Name.setText(ReciverName);

        SenderUid=auth.getUid();

        senderRoom=SenderUid+ReciverUid;
        reciverRoom=ReciverUid+SenderUid;


        DatabaseReference reference=database.getReference().child("User").child(auth.getUid());
        DatabaseReference chatreference=database.getReference().child("chats").child(senderRoom).child("messages");


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
                if(message.isEmpty())
                {
                    Toast.makeText(ChatActivity.this, "Please enter valid message", Toast.LENGTH_SHORT).show();
                    return;
                }
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
                                .setValue(messageModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });

                    }
                });
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}