package com.example.bibi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bibi.R;
import com.example.bibi.adapter.SimilarArticlesAdapter;
import com.example.bibi.model.PostsModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SimilarArticlesActivity extends AppCompatActivity {
    ImageView imageViewImage,imageViewUser;
    TextView name,time,title,tags;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_articles);

        imageViewImage = findViewById(R.id.similar_image_item);
        title = findViewById(R.id.similar_text_item);
        Intent intent = getIntent();
        String image=  intent.getStringExtra("imageUrl");
        String title1 =  intent.getStringExtra("title");

        Glide.with(this).load(image).into(imageViewImage);
        title.setText(title1);
    }




}