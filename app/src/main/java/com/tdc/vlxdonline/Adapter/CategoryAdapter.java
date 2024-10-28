package com.tdc.vlxdonline.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Model.Categorys;
import com.tdc.vlxdonline.Model.DonVi;
import com.tdc.vlxdonline.Model.SanPham_Model;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ItemPhanLoaiBinding;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter  extends RecyclerView.Adapter<Category_ViewHolder> {
    private Context context;
    private List<Categorys> list;

    private CategoryAdapter.OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(CategoryAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
    public CategoryAdapter(Context context, List<Categorys> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Category_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemdm_layout, parent, false);
        return new Category_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Category_ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getAnh()).into(holder.ivitemAnhDM);
        holder.tvTenDM.setText(list.get(position).getTen());
        // Thiết lập sự kiện click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(holder.getAdapterPosition()); // Dùng getAdapterPosition() thay vì position
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
