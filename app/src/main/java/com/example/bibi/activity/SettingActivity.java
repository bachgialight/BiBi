package com.example.bibi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bibi.R;
import com.example.bibi.untils.FirebaseUntil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends AppCompatActivity {
    TextView logout;
    ImageView backImage;
    FirebaseAuth auth;
    LinearLayout saveImage;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        logout = findViewById(R.id.logout);
        backImage = findViewById(R.id.back_image);
        saveImage = findViewById(R.id.save_image);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginOut();
            }
        });
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, SaveImageActivity.class);
                startActivity(intent);
            }
        });
    }
    public void loginOut() {

        if (currentUser != null) {
            // Lưu email vào SharedPreferences (hoặc các thông tin khác cần thiết)
            String userEmail = currentUser.getEmail();

            if (userEmail != null) {
                SharedPreferences preferences = getSharedPreferences("YOUR_PREFERENCES_NAME", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(userEmail); // Thay "key_email" bằng key của email hoặc thông tin đăng nhập khác
                editor.apply();
            } else {
                Log.e("LogoutError", "Không thể lấy email người dùng");
            }
        }

        // Thực hiện đăng xuất
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}