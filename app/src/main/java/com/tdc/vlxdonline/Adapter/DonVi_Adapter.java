package com.tdc.vlxdonline.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Model.DonVi;
import com.tdc.vlxdonline.Model.SanPham_Model;
import com.tdc.vlxdonline.R;

import java.util.List;

public class DonVi_Adapter extends RecyclerView.Adapter<DonVi_ViewHolder>  {
    private Context context;
    private List<DonVi> list;

    private DonVi_Adapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(DonVi_Adapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public DonVi_Adapter(Context context, List<DonVi> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DonVi_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemdv_layout, parent, false);
        return new DonVi_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonVi_ViewHolder holder, int position) {
        holder.tvTenDV.setText(list.get(position).getTen());
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
