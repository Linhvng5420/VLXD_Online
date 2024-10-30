package com.tdc.vlxdonline.Adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tdc.vlxdonline.R;

public class Banner_ViewHolder extends RecyclerView.ViewHolder {
    ImageView ivitemBanner;
    public Banner_ViewHolder(@NonNull View itemView) {
        super(itemView);
        ivitemBanner = itemView.findViewById(R.id.ivitemBanner);

    }
}
