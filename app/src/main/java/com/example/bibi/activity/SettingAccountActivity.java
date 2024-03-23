package com.example.bibi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.example.bibi.R;

public class SettingAccountActivity extends AppCompatActivity {
    LinearLayout showEditEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);
        showEditEmail = findViewById(R.id.email_user);
        showEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hiện thị dialog email để điều chỉnh email của người dùng
                Dialog dialog = new Dialog(SettingAccountActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.acitivity_change_email);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_diaglog);


                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.show();
            }
        });
    }
}