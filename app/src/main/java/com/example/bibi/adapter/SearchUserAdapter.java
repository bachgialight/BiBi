package com.example.bibi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bibi.R;
import com.example.bibi.activity.UserProfileActivity;
import com.example.bibi.model.UsersModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserAdapter extends FirestoreRecyclerAdapter<UsersModel, SearchUserAdapter.ViewHolder> {
    Context context;

    public SearchUserAdapter(@NonNull FirestoreRecyclerOptions<UsersModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public SearchUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull UsersModel model) {
        Glide.with(context).load(model.getProfileImage()).into(holder.imageViewProfile);
        holder.txtNameUser.setText(model.getName());

        //bắt sự kiện click vào
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("uid",model.getUid());
                context.startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfile;
        TextView txtNameUser, txtCountPost;
        Button btnVisit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.roundedImageViewUser);
            txtNameUser = itemView.findViewById(R.id.name_user);
            txtCountPost = itemView.findViewById(R.id.post_image_user);
            btnVisit = itemView.findViewById(R.id.button_visit);
        }
    }
}
