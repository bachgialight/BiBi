package com.example.bibi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bibi.R;
import com.example.bibi.adapter.InterestUserAdapter;
import com.example.bibi.model.InterestModel;
import com.example.bibi.model.UsersModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class InterestsUserActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView imageView;
    List<InterestModel> list;

    InterestUserAdapter adapter;
    Button btnFinish;
    LottieAnimationView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests_user);
        progressBar = findViewById(R.id.progressBar);

        btnFinish = findViewById(R.id.nextBtn);
        recyclerView = findViewById(R.id.recycler_view_interest);
        list = new ArrayList<>();
        adapter = new InterestUserAdapter(this,list);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);
        showInterest();
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InterestsUserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showInterest() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("interest").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                list.clear();
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    InterestModel interestModel = queryDocumentSnapshot.toObject(InterestModel.class);
                    list.add(interestModel);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}