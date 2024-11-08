package com.tdc.vlxdonline.Adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tdc.vlxdonline.R;

public class AnhSP_ViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivitemQLANH;

    public AnhSP_ViewHolder(@NonNull View itemView) {
        super(itemView);
        ivitemQLANH = itemView.findViewById(R.id.ivitemQLANH);
    }
}
