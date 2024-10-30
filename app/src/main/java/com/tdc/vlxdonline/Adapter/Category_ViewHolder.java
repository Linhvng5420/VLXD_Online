package com.tdc.vlxdonline.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tdc.vlxdonline.R;

public class Category_ViewHolder extends RecyclerView.ViewHolder{
public ImageView ivitemAnhDM;
    public TextView tvTenDM;
    public Category_ViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTenDM = itemView.findViewById(R.id.tvTenDM);
        ivitemAnhDM = itemView.findViewById(R.id.ivitemAnhDM);
    }
}
