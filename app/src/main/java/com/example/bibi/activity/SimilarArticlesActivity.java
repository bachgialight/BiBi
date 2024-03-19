package com.example.bibi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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
    RecyclerView recyclerView;
    SimilarArticlesAdapter adapter;
    List<PostsModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_articles);
        recyclerView = findViewById(R.id.recycler_view_similar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new SimilarArticlesAdapter(this,list);
        recyclerView.setAdapter(adapter);

        getPostSimilar();
    }

    private void getPostSimilar() {
        postCollection().get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        PostsModel postsModel = document.toObject(PostsModel.class);
                        list.add(postsModel);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    public static CollectionReference postCollection() {
        return FirebaseFirestore.getInstance().collection("posts");
    }
}