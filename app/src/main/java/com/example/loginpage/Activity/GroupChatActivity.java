package com.example.loginpage.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.loginpage.Adapter.MessageAdapter;
import com.example.loginpage.ModelClass.MessageModel;
import com.example.loginpage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {
    ImageView Gattachment,Gcamera;
    ImageView GSender;
    EditText Gmsgtype1;
    ProgressDialog prog4;

    FirebaseDatabase database;
    FirebaseStorage storage;

    ArrayList<MessageModel> messageModelArrayList;
    MessageAdapter adapter;
    RecyclerView recyclerView;

    String SenderUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        GSender=findViewById(R.id.Gsender);
        Gmsgtype1=findViewById(R.id.GmessageBox);
        Gattachment=findViewById(R.id.Gattachment);
        Gcamera=findViewById(R.id.Gcamera);


        prog4=new ProgressDialog(this);
        prog4.setMessage("Uploading fill....");
        prog4.setCancelable(false);

        SenderUid=FirebaseAuth.getInstance().getUid();
        database= FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();

        messageModelArrayList=new ArrayList<>();

        Gmsgtype1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=Gmsgtype1.getText().toString();
                // if(message.isEmpty())
                // {
                //     Toast.makeText(ChatActivity.this, "Please enter valid message", Toast.LENGTH_SHORT).show();
                //     return;
                // }
                Gmsgtype1.setText("");
                Date date=new Date();
                MessageModel messageModel=new MessageModel(message,SenderUid,date.getTime());

                database.getReference().child("public")
                        .push()
                        .setValue(message);
            }
        });
    }
}