package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText number;
    TextView lof1;
    Button continue1;
    ProgressBar progress1;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        
        number=findViewById(R.id.number);
        continue1=findViewById(R.id.continue1);
        progress1=findViewById(R.id.progress1);
        lof1=findViewById(R.id.log1);
        progress1.setVisibility(View.GONE);

        lof1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        continue1.setOnClickListener((view -> {
            if(!number.getText().toString().trim().isEmpty()){
                if((number.getText().toString().trim()).length()==10) {
                    progress1.setVisibility(View.VISIBLE);

                  PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91" + number.getText().toString(),
                            60,
                            TimeUnit.SECONDS,
                            MainActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    progress1.setVisibility(View.GONE);

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progress1.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    progress1.setVisibility(View.GONE);
                                    Intent intent = new Intent(MainActivity.this, OTPVerificationActivity.class);
                                    intent.putExtra("phone", number.getText().toString());
                                    intent.putExtra("backendotp",backendotp);
                                    startActivity(intent);
                                }
                            }
                    );


                }else {
                    Toast.makeText(this, "Please enter correct number", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();

            }


        }));

    }

}