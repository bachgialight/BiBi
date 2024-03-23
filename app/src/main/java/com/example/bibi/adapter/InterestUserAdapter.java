package com.example.bibi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bibi.R;
import com.example.bibi.model.UsersModel;

import java.util.List;

public class InterestUserAdapter extends RecyclerView.Adapter<InterestUserAdapter.ViewHolder> {
    Context context;
    List<UsersModel> list;

    public InterestUserAdapter(Context context, List<UsersModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InterestUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.)
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull InterestUserAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
