package com.example.bibi.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bibi.R;
import com.example.bibi.adapter.PostImageAdapter;
import com.example.bibi.model.PostsModel;
import com.example.bibi.untils.FirebaseUntil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class ImageUserSecondFragment extends Fragment {

    RecyclerView recyclerView;
    List<PostsModel> postsList;
    PostImageAdapter adapter;
    LottieAnimationView progressBar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_image_user_second, container, false);
// Lấy dữ liệu từ Bundle

        String uid = getArguments().getString("uid","");
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView = view.findViewById(R.id.recycler_view_image);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);

        postsList = new ArrayList<>();

        // Load posts from Firestore
        loadPostsFromFirestore(uid);
        return view;
    }
    private void loadPostsFromFirestore(String uid) {
        progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar khi bắt đầu load

        // truy vấn linh hoạt dù có bao nhiêu lớp vẫn có thể so sánh dược posts->id->uid
        postsCollection().whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postsList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        PostsModel postsModel = document.toObject(PostsModel.class);
                        postsList.add(postsModel);
                    }
                    adapter = new PostImageAdapter(getContext(), postsList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                });
    }

    // Rest of your code...

    public static CollectionReference postsCollection() {
        return FirebaseFirestore.getInstance().collection("posts");
    }
}