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
import com.example.bibi.model.UsersModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class SearchImageLabelsAdapter extends RecyclerView.Adapter<SearchImageLabelsAdapter.ViewHolder> {
    Context context;

    List<PostsModel> list;

    public SearchImageLabelsAdapter(Context context, List<PostsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SearchImageLabelsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_image_item, parent, false);
        return new SearchImageLabelsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getPostImage()).into(holder.imageViewSearchItem);

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewSearchItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSearchItem = itemView.findViewById(R.id.image_search_item);
        }
    }
}
