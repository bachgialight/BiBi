package com.example.bibi.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bibi.R;
import com.example.bibi.adapter.PostImageAdapter;
import com.example.bibi.adapter.ShareImageForProfileAdapter;
import com.example.bibi.model.PostsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HeloFragment extends Fragment {
    FirebaseUser user;
    FirebaseAuth auth;
    List<PostsModel> list;
    ShareImageForProfileAdapter adapter;
    RecyclerView recyclerViewHeart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_helo, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerViewHeart = view.findViewById(R.id.recycler_view_image_heart);
        recyclerViewHeart.setLayoutManager(new GridLayoutManager(getActivity(),2));
        list = new ArrayList<>();
        adapter = new ShareImageForProfileAdapter(HeloFragment.this.getActivity(),list);
        recyclerViewHeart.setAdapter(adapter);
        // thực hiện việc hiện thị phần dữ liệu hỉnh ảnh đã chia sẻ từ data
        showShareImage();
        return view;
    }
    private void showShareImage() {
        if (user != null) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("likes")
                    .whereEqualTo(user.getUid(), true) // Filter bằng userId của người dùng
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            list.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                PostsModel postsModel = document.toObject(PostsModel.class);
                                list.add(postsModel);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Xử lý khi không thành công
                        }
                    });
        }

    }
}