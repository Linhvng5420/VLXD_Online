package com.tdc.vlxdonline.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Model.Banner;
import com.tdc.vlxdonline.R;

import java.util.List;

public abstract class Banner_Adapter extends RecyclerView.Adapter<Banner_ViewHolder>{
    private Context context;
    private List<Banner> list;
    private Banner_Adapter.OnItemClickListener listener;

    public abstract void onItemClick(int position);

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(Banner_Adapter.OnItemClickListener listener) {
        this.listener = listener;
    }
    public Banner_Adapter(Context context, List<Banner> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public Banner_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemqlbanner_layout, parent, false);
        return new Banner_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Banner_ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getAnhBanner()).into(holder.ivitemBanner);
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
