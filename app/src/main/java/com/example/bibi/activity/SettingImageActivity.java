package com.example.bibi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bibi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingImageActivity extends AppCompatActivity {
    ImageView editImage;
    TextView editTextTitle,editTxtTags;
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_image);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        editImage = findViewById(R.id.post_image);
        editTextTitle = findViewById(R.id.edit_text_note);
        editTxtTags = findViewById(R.id.edit_text_tags);
        Intent intent = getIntent();
        String image = intent.getStringExtra("image");
        String name = intent.getStringExtra("postId");
        String title = intent.getStringExtra("title");
        //String tags = intent.getStringExtra("tags");

        Glide.with(this).load(image).into(editImage);
        editTextTitle.setText(title);
        //editTxtTags.setText(tags);
        showEditImage();
    }

    private void showEditImage() {

    }
}