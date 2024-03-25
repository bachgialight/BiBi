package com.example.bibi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bibi.R;
import com.example.bibi.model.InterestModel;
import com.example.bibi.model.UsersModel;
import com.example.bibi.untils.FirebaseUntil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.annotations.Nullable;

public class InterestUserAdapter extends RecyclerView.Adapter<InterestUserAdapter.ViewHolder> {
    Context context;
    List<InterestModel> list;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    public InterestUserAdapter(Context context, List<InterestModel> list) {
        this.context = context;
        this.list = list;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
    @NonNull
    @Override
    public InterestUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_interest_item,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull InterestUserAdapter.ViewHolder holder, int position) {
        InterestModel interestModel = list.get(position);
        Glide.with(context).load(interestModel.getImageInterest()).into(holder.imageViewUserInterest);
        holder.textTopic.setText(interestModel.getTopic());

        holder.tickInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrRemoveInterest(interestModel.getTopic());
            }
        });
        saveHobble(holder.tickInterest,interestModel.getTopic());
    }
    public void addOrRemoveInterest(String topic) {
        String currentUserUid = auth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(currentUserUid);

        // Get current interests
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UsersModel currentUser = documentSnapshot.toObject(UsersModel.class);
                if (currentUser != null) {
                    List<String> hobble = currentUser.getHobble();
                    if (hobble == null) {
                        hobble = new ArrayList<>(); // Khởi tạo danh sách nếu nó là null
                    }
                    if (hobble.contains(topic)) {
                        hobble.remove(topic); // Nếu chủ đề đã tồn tại, loại bỏ nó khỏi danh sách
                    } else {
                        hobble.add(topic); // Nếu chủ đề chưa tồn tại, thêm nó vào danh sách
                    }

                    currentUser.setHobble(hobble);
                    userRef.set(currentUser, SetOptions.merge()); // Cập nhật tài liệu người dùng
                }
            } else {
                // User document doesn't exist, create a new one
                UsersModel newUser = new UsersModel();
                newUser.setHobble(Collections.singletonList(topic)); // Tạo danh sách mới với chủ đề
                userRef.set(newUser, SetOptions.merge());
            }
        }).addOnFailureListener(e -> {
            // Xử lý lỗi
        });
    }

    // kiểm tra xem người dùng đã thích chủ đề đó chưa
    private void saveHobble(ImageView imageView, String topic) {
        String currentUserUid = auth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(currentUserUid);

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Xử lý lỗi
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    UsersModel currentUser = documentSnapshot.toObject(UsersModel.class);
                    if (currentUser != null && currentUser.getHobble() != null && currentUser.getHobble().contains(topic)) {
                        // Người dùng đã thích chủ đề này
                        imageView.setImageResource(R.drawable.checked);
                        imageView.setTag("save");
                    } else {
                        // Người dùng chưa thích chủ đề này
                        imageView.setImageResource(R.drawable.check_mark);
                        imageView.setTag("no_save");
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewUserInterest,tickInterest;
        TextView textTopic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewUserInterest = itemView.findViewById(R.id.image_user_interest);
            textTopic = itemView.findViewById(R.id.topic);
            tickInterest = itemView.findViewById(R.id.button_user_interest);
        }
    }
}
