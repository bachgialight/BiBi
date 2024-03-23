package com.example.bibi.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bibi.R;
import com.example.bibi.adapter.ImageUserAdapter;
import com.example.bibi.adapter.PostImageAdapter;
import com.example.bibi.model.PostsModel;
import com.example.bibi.untils.FirebaseUntil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class ImageUserFragment extends Fragment {
    RecyclerView recyclerView;
    List<PostsModel> postsList;
    ImageUserAdapter adapter;
    LottieAnimationView progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_user, container, false);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView = view.findViewById(R.id.recycler_view_image);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));;

        postsList = new ArrayList<>();

        // Load posts from Firestore
        loadPostsFromFirestore();
        return view;
    }
    private void loadPostsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar khi bắt đầu load

        // truy vấn linh hoạt dù có bao nhiêu lớp vẫn có thể so sánh dược posts->id->uid
        postsCollection().whereEqualTo("uid", FirebaseUntil.currentUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postsList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        PostsModel postsModel = document.toObject(PostsModel.class);
                        postsList.add(postsModel);
                    }
                    adapter = new ImageUserAdapter(getContext(), postsList);
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