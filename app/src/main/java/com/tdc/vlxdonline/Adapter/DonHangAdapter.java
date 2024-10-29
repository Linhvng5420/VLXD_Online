package com.tdc.vlxdonline.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.tdc.vlxdonline.Model.ChiTietNhap;
import com.tdc.vlxdonline.Model.TaoDonHang;
import com.tdc.vlxdonline.R;

import java.util.ArrayList;
import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.ChiTietViewHolder> {

    private Context context;
    private ArrayList<ChiTietNhap> dsChiTiet;

    public DonHangAdapter(Context context, ArrayList<ChiTietNhap> dsChiTiet) {
        this.context = context;
        this.dsChiTiet = dsChiTiet;
    }

    @NonNull
    @Override
    public ChiTietViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hoadon_donhang, parent, false);
        return new ChiTietViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChiTietViewHolder holder, int position) {
        ChiTietNhap chiTiet = dsChiTiet.get(position);

        // Bind data to views
        holder.tvTenSanPham.setText(chiTiet.getTen());
        holder.tvGiaSP.setText("Giá: " + chiTiet.getGia());
        holder.tvSoLuong.setText("Số Lượng: " + chiTiet.getSoLuong());
        // Load image (example with a placeholder if no image URL is provided)
        // Glide or Picasso can be used here to load images from a URL
        // Glide.with(context).load(chiTiet.getImageUrl()).into(holder.imgItemDonHang);
    }

    @Override
    public int getItemCount() {
        return dsChiTiet.size();
    }

    static class ChiTietViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItemDonHang;
        TextView tvTenSanPham, tvGiaSP, tvSoLuong, tvTongTien;

        public ChiTietViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItemDonHang = itemView.findViewById(R.id.img_itemDonHang);
            tvTenSanPham = itemView.findViewById(R.id.tv_tenSanPham);
            tvGiaSP = itemView.findViewById(R.id.tv_giaSP);
            tvSoLuong = itemView.findViewById(R.id.tv_soLuong);
            tvTongTien = itemView.findViewById(R.id.tv_tongTien);
        }
    }
}