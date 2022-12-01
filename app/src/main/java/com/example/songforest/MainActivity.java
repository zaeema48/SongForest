package com.example.songforest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText PhoneNo;
    android.widget.Button sendButton;
    CountryCodePicker countryCode;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String country_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PhoneNo = findViewById(R.id.number);
        sendButton = findViewById(R.id.button);
        countryCode = findViewById(R.id.countryCode);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();   //Get the instance of current user
        country_code= countryCode.getSelectedCountryCodeWithPlus();  //will add + before the countrycode

        //when you manually change the country code
        countryCode.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country_code= countryCode.getSelectedCountryCodeWithPlus();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number;
                number = PhoneNo.getText().toString();
                if(number.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter your Phone number", Toast.LENGTH_SHORT).show();
                }

                if(number.length()!=10){
                    Toast.makeText(MainActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                }

                else{
                    progressBar.setVisibility(View.VISIBLE);
                    number=country_code+number;

                    //verifying the entered phone number exists or NOT (68-76 line)
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(number)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(MainActivity.this)
                            .setCallbacks(callbacks)
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(options);

                }
            }
        });
        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            // method to fetch the otp automatically
            }
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken){  //string s is the opt sent by the google
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(MainActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(MainActivity.this, UserAuthentication.class);
                intent.putExtra("otp", s);  //string s is the opt sent by the google
                startActivity(intent);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }
        };

    }
    @Override
    public void onStart(){
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //will clear this tasks/ activity
            startActivity(intent);
        }
    }

}