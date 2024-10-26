package com.tdc.vlxdonline.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ItemDonHangBinding;

import java.util.ArrayList;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.DonHangHolder> {

    private Context context;
    private ArrayList<DonHang> data;
    OnItemDonHangClick onItemDonHangClick;

    public void setOnItemDonHangClick(OnItemDonHangClick onItemDonHangClick) {
        this.onItemDonHangClick = onItemDonHangClick;
    }

    public DonHangAdapter(Context context, ArrayList<DonHang> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public DonHangHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DonHangHolder(ItemDonHangBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DonHangHolder holder, int position) {
        DonHang donHang = data.get(position);

        Glide.with(context).load(donHang.getAnh()).into(holder.binding.ivAnhDon);
        holder.binding.tvTenNhanDon.setText(donHang.getTenKhach());
        holder.binding.tvSdtDon.setText(donHang.getSdt());
        holder.binding.tvDiaChiDon.setText(donHang.getDiaChi());
        StringBuilder chuoi = new StringBuilder(donHang.getTongTien() + "");
        if (chuoi.length() > 3) {
            int dem = 0;
            int doDai = chuoi.length() - 1;
            for (int i = doDai; i > 0; i--) {
                dem = dem + 1;
                if (dem == 3) {
                    chuoi.insert(i, '.');
                    dem = 0;
                }
            }
        }
        holder.binding.tvTongGia.setText(chuoi);
        // Sử lý hiển thị trạng thái
        int trangThai = donHang.getTrangThai();
        int trangThaiTT = donHang.getTrangThaiTT();
        if (trangThai == 0) {
            holder.binding.tvTrangThaiVc.setBackgroundColor(Color.rgb(255, 0, 0));
            holder.binding.tvTrangThaiVc.setText(R.string.cho_xac_nhan);
        }else if (trangThai == 1) {
            holder.binding.tvTrangThaiVc.setBackgroundColor(Color.rgb(0, 100, 255));
            holder.binding.tvTrangThaiVc.setText(R.string.cho_nhan_don);
        }else if (trangThai == 2) {
            holder.binding.tvTrangThaiVc.setBackgroundColor(Color.rgb(0, 255, 255));
            holder.binding.tvTrangThaiVc.setText(R.string.dang_van_chuyen);
        }else if (trangThai == 3) {
            holder.binding.tvTrangThaiVc.setBackgroundColor(Color.rgb(255, 255, 0));
            holder.binding.tvTrangThaiVc.setText(R.string.cho_nhan_hang);
        }else if (trangThai == 4) {
            holder.binding.tvTrangThaiVc.setBackgroundColor(Color.rgb(255, 0, 0));
            if (trangThaiTT == 0) {
                holder.binding.tvTrangThaiVc.setText(R.string.chua_thanh_toan);
            } else if (trangThaiTT == 1) {
                holder.binding.tvTrangThaiVc.setText(R.string.tra_gop);
            } else if (trangThaiTT == 2) {
                holder.binding.tvTrangThaiVc.setBackgroundColor(Color.rgb(0, 255, 0));
                holder.binding.tvTrangThaiVc.setText(R.string.da_hoan_thanh);
            }
        }

        final int pos = position;
        holder.position = pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DonHangHolder extends RecyclerView.ViewHolder {

        ItemDonHangBinding binding;
        int position;

        public DonHangHolder(@NonNull ItemDonHangBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDonHangClick != null) {
                        onItemDonHangClick.onItemClick(position);
                    }
                }
            });
        }
    }

    public interface OnItemDonHangClick{
        void onItemClick(int position);
    }
}
