package com.tdc.vlxdonline.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tdc.vlxdonline.R;

public class DonVi_ViewHolder extends RecyclerView.ViewHolder {

    public TextView tvTenDV;
    public DonVi_ViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTenDV = itemView.findViewById(R.id.tvTenDV);
    }
}
