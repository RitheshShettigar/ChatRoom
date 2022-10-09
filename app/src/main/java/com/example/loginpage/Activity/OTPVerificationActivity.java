package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPVerificationActivity extends AppCompatActivity {

    EditText input1q,input2q,input3q,input4q,input5q,input6q;
    Button verify;
    String getotpbackend;
    ProgressBar progress2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        input1q=findViewById(R.id.input1);
        input2q=findViewById(R.id.input2);
        input3q=findViewById(R.id.input3);
        input4q=findViewById(R.id.input4);
        input5q=findViewById(R.id.input5);
        input6q=findViewById(R.id.input6);
        verify=findViewById(R.id.verify);
        progress2=findViewById(R.id.progress2);
        progress2.setVisibility(View.GONE);


        TextView textView=findViewById(R.id.phnumber);
        textView.setText(String.format("+91-%s",getIntent().getStringExtra("phone")));

        getotpbackend=getIntent().getStringExtra("backendotp");



        verify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!input1q.getText().toString().trim().isEmpty()&&!input2q.getText().toString().trim().isEmpty()&&!input3q.getText().toString().trim().isEmpty()&&!input4q.getText().toString().trim().isEmpty()&&!input5q.getText().toString().trim().isEmpty()&&!input6q.getText().toString().trim().isEmpty())
                {
                    String entercodeotp=input1q.getText().toString()+
                            input2q.getText().toString()+
                            input3q.getText().toString()+
                            input4q.getText().toString()+
                            input5q.getText().toString()+
                            input6q.getText().toString();

                    if(getotpbackend!=null){
                        progress2.setVisibility(View.VISIBLE);

                        PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(
                                getotpbackend,entercodeotp
                        );
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progress2.setVisibility(View.GONE);

                                        if(task.isSuccessful()){
                                            Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                                            intent.putExtra("phone1", textView.getText().toString());
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }else{
                                            Toast.makeText(OTPVerificationActivity.this, "Enter the correct OTP", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }else {
                        Toast.makeText(OTPVerificationActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

                    }

                }else{
                    Toast.makeText(OTPVerificationActivity.this, "please entre all field", Toast.LENGTH_SHORT).show();

                }
            }

        });

        numbeautomove();


        TextView resendlabel=findViewById(R.id.resendotp);
        resendlabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" +getIntent().getStringExtra("phone"),
                        60,
                        TimeUnit.SECONDS,
                        OTPVerificationActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(OTPVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCodeSent(@NonNull String newbackendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                getotpbackend=newbackendotp;
                                Toast.makeText(OTPVerificationActivity.this, "OTP sended successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

            }
        });



    }


    //auto move number
    private void numbeautomove() {
        input1q.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    input2q.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        input2q.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    input3q.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        input3q.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    input4q.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        input4q.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    input5q.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        input5q.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    input6q.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}