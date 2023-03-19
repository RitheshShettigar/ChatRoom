package com.example.loginpage.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    EditText email,password;
    Button btn_login;
    ProgressDialog prog1;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    TextView reg,otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        email=findViewById(R.id.email2);
        password=findViewById(R.id.password2);
        reg=findViewById(R.id.reg);
        btn_login=findViewById(R.id.login);
       // otp=findViewById(R.id.otp);
        TextView textView=findViewById(R.id.forgotpassword);

        prog1=new ProgressDialog(this);
        prog1.setMessage("Please wait....");
        prog1.setCancelable(false);

     //  otp.setOnClickListener(new View.OnClickListener() {
     //      @Override
     //      public void onClick(View view) {
     //          Intent intent = new Intent(LoginActivity.this,MainActivity.class);
     //          startActivity(intent);

     //      }
     //  });



        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,PasswordResetActivity.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        auth=FirebaseAuth.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prog1.show();
                String txtemail = email.getText().toString();
                String txtpassword = password.getText().toString();
                if (TextUtils.isEmpty(txtemail) || TextUtils.isEmpty(txtpassword)) {
                    prog1.dismiss();
                    Toast.makeText(LoginActivity.this, "all the fileds are required", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(txtemail, txtpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                Toast.makeText(LoginActivity.this, "Login sucessful", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "entery is not currect", Toast.LENGTH_SHORT).show();
                                prog1.dismiss();
                            }
                        }
                    });
                }
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
               // Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
               // startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, SplashScreeActivity.class);
            startActivity(intent);
        }
    }

}