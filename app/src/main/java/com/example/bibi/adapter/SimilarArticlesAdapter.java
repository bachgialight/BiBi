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
import com.example.bibi.custom.CustomTimeAgo;
import com.example.bibi.model.PostsModel;

import java.util.List;

public class SimilarArticlesAdapter extends RecyclerView.Adapter<SimilarArticlesAdapter.ViewHolder> {
    Context context;
    List<PostsModel> list;
    public SimilarArticlesAdapter(Context context, List<PostsModel> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public SimilarArticlesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_articles_time,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarArticlesAdapter.ViewHolder holder, int position) {
        PostsModel postsModel = list.get(position);
        Glide.with(context).load(postsModel.getPostImage()).into(holder.imageViewImage);
        String timeAgo = CustomTimeAgo.getTimeAgo(String.valueOf(postsModel.getTimestamp()));
        holder.timeSimilar.setText("Lúc đăng: "+timeAgo);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewImage;
        TextView titleSimilar,timeSimilar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeSimilar = itemView.findViewById(R.id.similar_time_item);
            imageViewImage = itemView.findViewById(R.id.similar_image_item);
            titleSimilar = itemView.findViewById(R.id.similar_text_item);
        }
    }
}
