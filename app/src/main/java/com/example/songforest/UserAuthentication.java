package com.example.songforest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class UserAuthentication extends AppCompatActivity {


    EditText enterOTP;
    android.widget.Button verify ;
    TextView change_the_ph_no;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authentication);

        enterOTP = findViewById(R.id.otp);
        verify = findViewById(R.id.verify);
        change_the_ph_no = findViewById(R.id.textView5);

        change_the_ph_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(UserAuthentication.this, MainActivity.class) ;
                startActivity(intent2);
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp_enter=enterOTP.getText().toString();
                if(otp_enter.isEmpty()){
                    Toast.makeText(UserAuthentication.this, "Enter the OTP!!", Toast.LENGTH_SHORT).show();
                }
                else{

                    Intent intent = getIntent();
                    String otp = intent.getStringExtra("otp");
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otp, otp_enter);   //to compare both the otps
                    SignInWithPhoneAuthCredential(credential);
                }
            }
        });



    }

    private void SignInWithPhoneAuthCredential(PhoneAuthCredential credential){

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserAuthentication.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(UserAuthentication.this, CreateActivity.class);
                    startActivity(intent3);
                    finish();
                }
                else{
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    Toast.makeText(UserAuthentication.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}