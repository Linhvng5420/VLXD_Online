package com.tdc.vlxdonline.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.R;

import java.util.ArrayList;

public class TTKhachHangAdapter extends RecyclerView.Adapter<TTKhachHangAdapter.KhachHangViewHolder> {

    private Context context;
    private ArrayList<KhachHang> khachHangList;
    OnItemInfoClick onItemInfoClick;

    public void setOnItemInfoClick(OnItemInfoClick onItemInfoClick) {
        this.onItemInfoClick = onItemInfoClick;
    }

    public TTKhachHangAdapter(Context context, ArrayList<KhachHang> khachHangList) {
        this.context = context;
        this.khachHangList = khachHangList;
    }

    @NonNull
    @Override
    public KhachHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_thongtinkhachhang, parent, false);
        return new KhachHangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KhachHangViewHolder holder, int position) {
        KhachHang khachHang = khachHangList.get(position);
        holder.tvTenKhach.setText("Tên: " + khachHang.getTen());
        holder.tvSDT.setText("SĐT: " + khachHang.getSdt());
        holder.tvDiaChi.setText("Địa chỉ: " + khachHang.getDiaChi());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemInfoClick != null) {
                    onItemInfoClick.onItemClick(holder.getAdapterPosition()); // Dùng getAdapterPosition() thay vì position
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return khachHangList.size();
    }

    public static class KhachHangViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenKhach, tvSDT, tvDiaChi;

        public KhachHangViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenKhach = itemView.findViewById(R.id.tvTenKhach);
            tvSDT = itemView.findViewById(R.id.tvSDTKhach);
            tvDiaChi = itemView.findViewById(R.id.tvDiaChiKhach);
        }
    }

    public interface OnItemInfoClick{
        void onItemClick(int position);
    }
}

