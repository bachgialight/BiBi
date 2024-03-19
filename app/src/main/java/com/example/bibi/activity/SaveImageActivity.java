package com.example.bibi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.bibi.R;
import com.example.bibi.adapter.SaveImageAdapter;
import com.example.bibi.model.PostsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SaveImageActivity extends AppCompatActivity {
    List<PostsModel> list;
    SaveImageAdapter adapter;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser user;
    ImageView backImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_image);
        backImage = findViewById(R.id.back_image);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView = findViewById(R.id.recycler_view_save_image);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        list = new ArrayList<>();
        adapter = new SaveImageAdapter(this,list);
        recyclerView.setAdapter(adapter);
        // hiện danh sách lưu ảnh ra
        showBookmark();
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showBookmark() {
        //kiểm tra xem người dùng  có tồn tại hay không
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("saveImage")
                    .document(user.getUid())
                    .collection("saveImages")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // làm sạch danh sách trước đó
                            list.clear();
                            // duyệt hết phần tử đã chứa
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                PostsModel postsModel = queryDocumentSnapshot.toObject(PostsModel.class);
                                list.add(postsModel);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }
}