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

public class ShareImageForProfileAdapter extends RecyclerView.Adapter<ShareImageForProfileAdapter.ViewHolder> {
    Context context;
    List<PostsModel> list;

    public ShareImageForProfileAdapter(Context context, List<PostsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ShareImageForProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_share_image_for_profile,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareImageForProfileAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getPostImage()).into(holder.imageViewShareImage);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewShareImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewShareImage = itemView.findViewById(R.id.share_image);

        }
    }
}
