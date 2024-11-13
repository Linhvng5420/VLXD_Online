package com.tdc.vlxdonline.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Model.ChiTietDon;
import com.tdc.vlxdonline.Model.DonNhap;
import com.tdc.vlxdonline.R;

import java.util.ArrayList;

public class DonNhapAdapter extends RecyclerView.Adapter<DonNhapAdapter.DonNhapHolder> {
    ArrayList<DonNhap> data = new ArrayList<>();
    Activity context;

    public DonNhapAdapter(ArrayList<DonNhap> data, Activity context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public DonNhapHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_thongtinnhanhang, parent, false);
        return new DonNhapHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull DonNhapHolder holder, int position) {
        DonNhap chiTiet = data.get(position);

        holder.tvmaNhap.setText(chiTiet.getId()+"");
        holder.tv_tongTien_nhap.setText(chiTiet.getTongTien()+"");
        holder.tvngayNhap.setText(chiTiet.getNgayTao());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DonNhapHolder extends RecyclerView.ViewHolder {
        TextView tvmaNhap, tvngayNhap, tv_tongTien_nhap;

        public DonNhapHolder(@NonNull View itemView) {
            super(itemView);
            tvmaNhap = itemView.findViewById(R.id.tv_maNhap);
            tvngayNhap = itemView.findViewById(R.id.tv_ngayNhap);
            tv_tongTien_nhap = itemView.findViewById(R.id.tv_tongTien_nhap);
        }
    }
}
