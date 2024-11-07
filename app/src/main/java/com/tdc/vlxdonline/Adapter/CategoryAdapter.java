package com.tdc.vlxdonline.Adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Model.Categorys;
import com.tdc.vlxdonline.databinding.ItemLoaiCategoryBinding;
//import com.tdc.vlxdonline.databinding.ItemPhanLoaiBinding;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private Activity context;
    private ArrayList<Categorys> data;
    private OnItemCategoryClickListener onItemCategoryClickListener;

    public void setOnItemCategoryClickListener(OnItemCategoryClickListener onItemCategoryClickListener) {
        this.onItemCategoryClickListener = onItemCategoryClickListener;
    }

    public CategoryAdapter(Activity context, ArrayList<Categorys> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryHolder(ItemLoaiCategoryBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        Categorys cate = data.get(position);
        Glide.with(context).load(cate.getAnh()).into(holder.binding.imgItemCa);
        holder.binding.tvNameCa.setText(cate.getTen());
        final int pos = position;
        holder.position = pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        ItemLoaiCategoryBinding binding;
        int position;
        public CategoryHolder(@NonNull ItemLoaiCategoryBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemCategoryClickListener != null) {
                        onItemCategoryClickListener.OnItemClick(v, position);
                    }
                }
            });
        }
    }

    public interface OnItemCategoryClickListener {
        void OnItemClick(View view, int position);
    }
}
