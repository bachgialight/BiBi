package com.example.bibi.fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bibi.R;
import com.example.bibi.adapter.PostImageAdapter;
import com.example.bibi.model.PostsModel;
import com.example.bibi.untils.FirebaseUntil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    List<PostsModel> postsList;
    PostImageAdapter adapter;
    LottieAnimationView progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        OpenCVLoader.initDebug();
        progressBar = view.findViewById(R.id.progressBar);
        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.recycler_view_image);
        recyclerView.setHasFixedSize(true);
        // Đo lường RecyclerView

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);

        postsList = new ArrayList<>();

        // Load posts from Firestore
        loadPostsFromFirestore();
        return view;
    }
    private void loadPostsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        postsCollection().orderBy("timestamp",Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postsList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        PostsModel postsModel = document.toObject(PostsModel.class);
                        postsList.add(postsModel);
                    }

                    // Cập nhật RecyclerView sau khi đã có dữ liệu mới
                    adapter = new PostImageAdapter(getContext(), postsList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                });
    }
    public static CollectionReference postsCollection() {
        return FirebaseFirestore.getInstance().collection("posts");
    }
    // Rest of your code...

}