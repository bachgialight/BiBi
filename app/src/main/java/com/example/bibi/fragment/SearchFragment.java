package com.example.bibi.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.bibi.R;
import com.example.bibi.adapter.SearchImageLabelsAdapter;
import com.example.bibi.adapter.SearchUserAdapter;
import com.example.bibi.model.PostsModel;
import com.example.bibi.model.UsersModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    EditText searchEditInput;
    ImageButton searchImageBtn;
    RecyclerView recyclerViewSearchObject;
    List<PostsModel> list;
    SearchImageLabelsAdapter adapter;
    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        db = FirebaseFirestore.getInstance();
        searchEditInput = view.findViewById(R.id.edit_search);
        searchImageBtn = view.findViewById(R.id.imageButton_search);
        recyclerViewSearchObject = view.findViewById(R.id.recycler_search_object);
        list = new ArrayList<>();
        searchEditInput.requestFocus();
        searchImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchItem = searchEditInput.getText().toString();

                performSearch(searchItem);
            }
        });
        // thuật toán tìm kiếm người dùng
        return view;
    }

    private void performSearch(String searchItem) {
        CollectionReference postsCollection = db.collection("posts");
        postsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PostsModel> posts = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    PostsModel post = document.toObject(PostsModel.class);
                    posts.add(post);
                }

                // Tìm kiếm ảnh dựa trên từ khóa
                List<PostsModel> filteredPosts = new ArrayList<>();
                for (PostsModel post : posts) {
                    if (post.getLabelMap().containsKey(searchItem)) {
                        filteredPosts.add(post);
                    }
                }

                // Hiển thị kết quả lên RecyclerView
                displaySearchResults(filteredPosts);
            } else {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    private void displaySearchResults(List<PostsModel> filteredPosts) {
        adapter = new SearchImageLabelsAdapter(getContext(),filteredPosts);
        recyclerViewSearchObject.setHasFixedSize(true);
        recyclerViewSearchObject.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerViewSearchObject.setAdapter(adapter);
    }

}