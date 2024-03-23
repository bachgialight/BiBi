package com.example.bibi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.bibi.R;
import com.example.bibi.model.UsersModel;

import java.util.ArrayList;
import java.util.List;

public class InterestsUserActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView imageView;
    List<UsersModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests_user);
        recyclerView = findViewById(R.id.recycler_view_interest);
        list = new ArrayList<>();


    }
}