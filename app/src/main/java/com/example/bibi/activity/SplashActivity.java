package com.example.bibi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.bibi.R;
import com.example.bibi.activity.PhoneActivity;
import com.example.bibi.untils.FirebaseUntil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // kiểm tra xem người dùng đã đăng nhập chưa
                if (FirebaseUntil.isLoggedIn()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getApplicationContext(), LoginOrRegisterActivity.class);
                    startActivity(intent);
                }
                finish();

            }
        },2000);
    }
}