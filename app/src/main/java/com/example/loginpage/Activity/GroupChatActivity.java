package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.Adapter.GroupChatSendAdapter;
import com.example.loginpage.Adapter.MessageAdapter;
import com.example.loginpage.ModelClass.GroupChatSendModel;
import com.example.loginpage.ModelClass.MessageModel;
import com.example.loginpage.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {
    ImageView Gattachment,Gcamera,attachment1;
    ImageView GSender,groupIconIv;
    EditText typemessage;
    ProgressDialog prog4;
    Toolbar toolbar;
    TextView groupTitleIv;
    String message;
    RecyclerView chatRv;

    String GroupId,myGroupRole;

    private final  int CAMERA_REQUEST_CODE=200;
    private final  int STORAGE_REQUEST_CODE=400;

    private final  int IMAGE_PICK_CAMERA_CODE=100;
    private final  int IMAGE_PICK_GALLERY_CODE=100;

    private String[]cameraPermission;
    private String []storagePermission;

    private Uri image_uri=null;

    FirebaseAuth mAuth;

    private  ArrayList<GroupChatSendModel>groupChatSendModelArrayList;
    GroupChatSendAdapter groupChatSendAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        GSender=findViewById(R.id.Gsender);
        typemessage=findViewById(R.id.msgtype);
        Gattachment=findViewById(R.id.attachment);
        Gcamera=findViewById(R.id.Gcamera);
        toolbar=findViewById(R.id.toolbar);
        groupIconIv=findViewById(R.id.groupIconIv);
        groupTitleIv=findViewById(R.id.groupTitleIv);
        chatRv=findViewById(R.id.chatRv);
        attachment1=findViewById(R.id.attachment1);


        prog4=new ProgressDialog(this);
        prog4.setMessage("Uploading fill....");
        prog4.setCancelable(false);

        setSupportActionBar(toolbar);

        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



       // chatRv.setHasFixedSize(true);
      //  chatRv.setLayoutManager(new LinearLayoutManager(this));
        groupChatSendModelArrayList= new ArrayList<>();
        groupChatSendAdapter=new GroupChatSendAdapter(this,groupChatSendModelArrayList);
        chatRv.setAdapter(groupChatSendAdapter);
       // LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
       // linearLayoutManager.setStackFromEnd(true);




        Intent intent=getIntent();
        GroupId=intent.getStringExtra("groupId");

        mAuth=FirebaseAuth.getInstance();

        loadGroupInfo();
        loadGroupMessage();
        loadMyGroupRole();

        GSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message=typemessage.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(GroupChatActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();
                }else
                {
                    sendMessage();
                }
            }
        });

        attachment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageImportDialog();

            }
        });


    }

    private void showImageImportDialog() {
        String[] option={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Image:")
                .setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            //camera clicked
                            if(!checkCameraPermission()){
                                requestCameraPermission();
                            }
                            else {
                                  pickFromCamera();
                            }
                        }
                        else
                        {
                            //    gallery clicked
                            if(!checkStoragePermission()){
                                requestStoragePermission();
                            }
                            else
                            {
                               pickFromGallery();
                            }
                        }
                    }
                }).show();

    }

    private void pickFromGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }


    private  void pickFromCamera()
    {
        ContentValues cv=new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Group Image Icon Title");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Group Image Icon description");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);


    }

    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission()
    {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission()
    {
        boolean result=ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean  result1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result&&result1;
    }


    private void loadMyGroupRole() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Participants")
                .orderByChild("uid").equalTo(mAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            myGroupRole=""+ds.child("role").getValue();
                            invalidateOptionsMenu();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadGroupMessage() {
        groupChatSendModelArrayList=new ArrayList<>();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Message")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        groupChatSendModelArrayList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            GroupChatSendModel model=ds.getValue(GroupChatSendModel.class);
                            groupChatSendModelArrayList.add(model);
                        }
                        groupChatSendAdapter=new GroupChatSendAdapter(GroupChatActivity.this,groupChatSendModelArrayList);
                        chatRv.setAdapter(groupChatSendAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendMessage() {
        String timestamo=""+System.currentTimeMillis();

        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("sender",""+mAuth.getUid());
        hashMap.put("message",""+ message);
        hashMap.put("timestamp",""+timestamo);
        hashMap.put("type",""+"text");


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Message").child(timestamo)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        typemessage.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void loadGroupInfo() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(GroupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            String groupTitle=""+ds.child("groupTitle").getValue();
                            String groupDescription=""+ds.child("groupDescription").getValue();
                            String groupIcon=""+ds.child("groupIcon").getValue();
                            String timestamp=""+ds.child("timestamp").getValue();
                            String createdBy=""+ds.child("createdBy").getValue();

                            groupTitleIv.setText(groupTitle);
                            try {
                                Picasso.get().load(groupIcon).placeholder(R.drawable.user).into(groupIconIv);
                            }catch (Exception e){
                                groupIconIv.setImageResource(R.drawable.user);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        menu.findItem(R.id.group).setVisible(false);
        menu.findItem(R.id.setting).setVisible(false);
        menu.findItem(R.id.logout).setVisible(false);
        menu.findItem(R.id.search).setVisible(false);

        if(myGroupRole.equals("creator")||myGroupRole.equals("admin")){
            menu.findItem(R.id.action_add_participants).setVisible(true);

        }
        else {
            menu.findItem(R.id.action_add_participants).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_participants:
            Intent intent = new Intent(this, GroupParticipantsActivity.class);
            intent.putExtra("groupId",GroupId);
            startActivity(intent);


            case R.id.action_chat_ingo:
                Intent intent1 = new Intent(this,GroupInfoActivity.class);
                intent1.putExtra("groupId",GroupId);
                startActivity(intent1);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100&&data!=null&&data.getData()!=null){
            image_uri=data.getData();
            sendImageMessage();

        }

        if(requestCode==IMAGE_PICK_CAMERA_CODE){
              //was picked from camera
             sendImageMessage();

        }

    }

    private void sendImageMessage() {
        ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setMessage("Sending Image.....");
        pd.setCanceledOnTouchOutside(false);
        pd.show();


        //String filenamePath="chats"+""+System.currentTimeMillis();
        StorageReference storageReference=FirebaseStorage.getInstance().getReference("GroupChatImage").child(GroupId);
        storageReference.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri>p_uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!p_uriTask.isSuccessful());
                        Uri p_download=p_uriTask.getResult();

                        if(p_uriTask.isSuccessful()){
                            //image uri received save in  db
                            String timestamo=""+System.currentTimeMillis();

                            HashMap<String,Object>hashMap=new HashMap<>();
                            hashMap.put("sender",""+mAuth.getUid());
                            hashMap.put("message",""+p_download);
                            hashMap.put("timestamp",""+timestamo);
                            hashMap.put("type",""+"image");


                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
                            ref.child(GroupId).child("Message").child(timestamo)
                                    .setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            typemessage.setText("");
                                            pd.dismiss();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();

                                }
                            });

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }
                    else
                    {
                        Toast.makeText(this, "camera &storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0)
                {
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickFromGallery();
                    }
                    else
                    {
                        Toast.makeText(this, "storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}