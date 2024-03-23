package com.example.bibi.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bibi.R;
import com.example.bibi.activity.SettingImageActivity;
import com.example.bibi.model.PostsModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ImageUserAdapter extends RecyclerView.Adapter<ImageUserAdapter.ViewHolder> {
    private Context context;
    private List<PostsModel> matchingPosts;

    public ImageUserAdapter(Context context, List<PostsModel> matchingPosts) {
        this.context = context;
        this.matchingPosts = matchingPosts;
    }

    @NonNull
    @Override
    public ImageUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item_user, parent, false);
        return new ImageUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageUserAdapter.ViewHolder holder, int position) {
        PostsModel post = matchingPosts.get(position);
        // Hiển thị thông tin bài viết, ví dụ:
        Glide.with(context).load(post.getPostImage()).into(holder.imageUser);
        holder.settingDialogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // cày đặc hình ảnh của người dùng
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_image_user_dialog);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                LinearLayout linearLayoutDelete = dialog.findViewById(R.id.delete_image);
                linearLayoutDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // xóa ảnh hiện tại
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String posId = post.getPostId();
                        db.collection("posts").document(posId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "xóa thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                });
                LinearLayout linearLayoutEdit = dialog.findViewById(R.id.edit_image_user);
                linearLayoutEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SettingImageActivity.class);
                        intent.putExtra("image",post.getPostImage());
                        intent.putExtra("postId",post.getPostId());
                        intent.putExtra("title",post.getTitle());
                        //intent.putExtra("tags",post.getTags().toString());
                        context.startActivity(intent);
                    }
                });
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return matchingPosts.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageUser,settingDialogImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUser = itemView.findViewById(R.id.image_user);
            settingDialogImage = itemView.findViewById(R.id.show_item_dialog_setting);

        }
    }
}
