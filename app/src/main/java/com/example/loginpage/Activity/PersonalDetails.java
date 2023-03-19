package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loginpage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.rpc.context.AttributeContext;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView personalName,Email,group;
    CircleImageView personalImage;
    EditText phoneNumber,Qualification,nationality,DOB,name2,Nickname;
    Spinner bloodGroup;
    RadioGroup gender;
    Button save;
    private int mYear,mMonth,mDay;

    String name,image;

    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        personalImage=findViewById(R.id.personalProfile);
        personalName=findViewById(R.id.personalName);
        Email=findViewById(R.id.email);
        phoneNumber=findViewById(R.id.phonenumber);
        Qualification=findViewById(R.id.qualification);
        nationality=findViewById(R.id.nationality);
        bloodGroup=findViewById(R.id.blood);
        gender=findViewById(R.id.gender);
        save=findViewById(R.id.save);
        DOB=findViewById(R.id.dob);
        name2=findViewById(R.id.name2);
        Nickname=findViewById(R.id.Nickname);
        group=findViewById(R.id.group);

        //spinner
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.type,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroup.setAdapter(adapter);
        bloodGroup.setOnItemSelectedListener(PersonalDetails.this);


        Intent intent=getIntent();
        name=intent.getStringExtra("name");
        image=intent.getStringExtra("image");

        Picasso.get().load(image).into(personalImage);
        personalName.setText(name);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance().getReference("UserProfile").child(mAuth.getUid());

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               String email = snapshot.child("email").getValue().toString();
                String username1 = snapshot.child("username").getValue().toString();
              String phone=snapshot.child("phone").getValue().toString();
                String gender1=snapshot.child("gender").getValue().toString();
                String qualification = snapshot.child("qualification").getValue().toString();
               String nationality1=snapshot.child("nationality").getValue().toString();
               String dob=snapshot.child("DOB").getValue().toString();
                String blood=snapshot.child("blood").getValue().toString();
                String NickName1=snapshot.child("Nickname").getValue().toString();

                Toast.makeText(PersonalDetails.this,blood, Toast.LENGTH_SHORT).show();


             Email.setText(email);
              phoneNumber.setText(phone);
              Nickname.setText(NickName1);
              Qualification.setText(qualification);
               DOB.setText(dob);
               name2.setText(username1);
               nationality.setText(nationality1);
                group.setText(blood);
             //  gender.check(Integer.parseInt(gender1));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save();
            }
        });

        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c=Calendar.getInstance();
                mYear=c.get(Calendar.YEAR);
                mMonth=c.get(Calendar.MONTH);
                mDay=c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(PersonalDetails.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        DOB.setText(dayOfMonth+"-"+(month+1)+"-"+year);

                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();

            }
        });




    }

    private void Save() {


        mDatabase = FirebaseDatabase.getInstance().getReference().child("UserProfile").child(mAuth.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("email",Email.getText().toString());
        map.put("Nickname",Nickname.getText().toString());
        map.put("phone",phoneNumber.getText().toString());
        map.put("gender",gender.toString());
        map.put("qualification", Qualification.getText().toString());
        map.put("nationality",nationality.getText().toString());
        map.put("username",name.toString());
        map.put("DOB",DOB.getText().toString());
        map.put("id",mAuth.getUid());
        map.put("blood",bloodGroup.getSelectedItem().toString());
        mDatabase.setValue(map);
        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice=adapterView.getItemAtPosition(i).toString();
        Toast.makeText(getApplicationContext(), choice, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}