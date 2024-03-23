package com.example.bibi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bibi.R;
import com.example.bibi.model.PostsModel;

import java.util.List;


    public class MatchingPostsAdapter extends RecyclerView.Adapter<MatchingPostsAdapter.ViewHolder> {

        private Context context;
        private List<PostsModel> matchingPosts;

        public MatchingPostsAdapter(Context context, List<PostsModel> matchingPosts) {
            this.context = context;
            this.matchingPosts = matchingPosts;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_matching_post, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PostsModel post = matchingPosts.get(position);
            // Hiển thị thông tin bài viết, ví dụ:
            Glide.with(context).load(post.getPostImage()).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return matchingPosts.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            // Khai báo các thành phần giao diện ở đây (ví dụ: TextView, ImageView)
            ImageView imageView;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                // Ánh xạ và khởi tạo các thành phần giao diện ở đây
                // Ví dụ:
                // titleTextView = itemView.findViewById(R.id.titleTextView);
                // imageView = itemView.findViewById(R.id.imageView);
                imageView = itemView.findViewById(R.id.matching_image);
            }
        }
    }


