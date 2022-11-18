package com.example.songforest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashScreen extends AppCompatActivity {

    private static int splashTimer=3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //To remove title bar

        new Handler().postDelayed(new Runnable() {  //is used to delay run method until the splashtime is over
            @Override
            public void run() {
                Intent intent = new Intent (SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();   //splashScreen will not get repeated everytime when back button is clicked
            }
        }, splashTimer);    //splashtimer is passed into the paranthesis of postDelayed method.

    }
}