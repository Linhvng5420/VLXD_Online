package com.tdc.vlxdonline.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tdc.vlxdonline.R;

public class SanPham_ViewHolder extends RecyclerView.ViewHolder {
    public TextView tvTenSP, giabanSP;
    public ImageView ivImages;

    public SanPham_ViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTenSP = itemView.findViewById(R.id.tvItemsTen);
        giabanSP = itemView.findViewById(R.id.tvGiaSP);
        ivImages = itemView.findViewById(R.id.ivImages);
    }
}
