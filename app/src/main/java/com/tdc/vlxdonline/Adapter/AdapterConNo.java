package com.tdc.vlxdonline.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.Model.TraGop;
import com.tdc.vlxdonline.databinding.ItemConNoBinding;
import com.tdc.vlxdonline.databinding.ItemTraGopBinding;

import java.util.ArrayList;

public class AdapterConNo extends RecyclerView.Adapter<AdapterConNo.HolderConNo> {
    Activity context;
    ArrayList<KhachHang> data;
    OnItemConNoClick onItemConNoClick;

    public void setOnItemConNoClick(OnItemConNoClick onItemConNoClick) {
        this.onItemConNoClick = onItemConNoClick;
    }

    public AdapterConNo(Activity context, ArrayList<KhachHang> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public AdapterConNo.HolderConNo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterConNo.HolderConNo(ItemConNoBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterConNo.HolderConNo holder, int position) {
        KhachHang temp = data.get(position);

        Glide.with(context).load(temp.getAvata()).into(holder.binding.ivAnhNo);
        holder.binding.tvTenNo.setText(temp.getTen());
        holder.binding.tvSDTNo.setText(temp.getSdt());
        holder.binding.tvDiaChiNo.setText(temp.getDiaChi());

        final int pos = position;
        holder.position = pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class HolderConNo extends RecyclerView.ViewHolder {
        ItemConNoBinding binding;
        int position;

        public HolderConNo(@NonNull ItemConNoBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemConNoClick != null) {
                        onItemConNoClick.onItemClick(data.get(position).getID());
                    }
                }
            });
        }
    }

    public interface OnItemConNoClick{
        void onItemClick(String idKhach);
    }
}
