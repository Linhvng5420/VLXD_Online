package com.tdc.vlxdonline.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Model.AnhSanPham;
import com.tdc.vlxdonline.R;

import java.util.List;

public abstract class AnhSP_Adapter extends RecyclerView.Adapter<AnhSP_ViewHolder>{
    private Context context;
    private List<AnhSanPham> list;
    private OnItemClickListener listener;

    public abstract void onItemClick(int position);

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(AnhSP_Adapter.OnItemClickListener listener) {
        this.listener = listener;
    }
    public AnhSP_Adapter(Context context, List<AnhSanPham> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AnhSP_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemqlanhsp_layout, parent, false);
        return new AnhSP_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnhSP_ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getAnh()).into(holder.ivitemQLANH);
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
