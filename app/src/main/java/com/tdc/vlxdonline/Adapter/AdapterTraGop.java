package com.tdc.vlxdonline.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.tdc.vlxdonline.Model.TraGop;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ItemTraGopBinding;

import java.util.ArrayList;

public class AdapterTraGop extends RecyclerView.Adapter<AdapterTraGop.HolderTraGop> {

    Activity context;
    ArrayList<TraGop> data;

    public AdapterTraGop(Activity context, ArrayList<TraGop> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public HolderTraGop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderTraGop(ItemTraGopBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HolderTraGop holder, int position) {
        TraGop temp = data.get(position);

        holder.binding.tvTtThanhToan.setText("Đã Thanh Toán");
        holder.binding.tvTtThanhToan.setBackgroundColor(Color.rgb(0, 255, 170));

        holder.binding.tvDotTT.setText("Đợt " + temp.getThuTu());
        StringBuilder chuoi = new StringBuilder(temp.getSoTien()+"");
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
        holder.binding.tvTongTT.setText(chuoi + " đ");
        holder.binding.tvHanTT.setText(temp.getHanTra());
        if (!temp.isDaTra()) {
            holder.binding.tvTtThanhToan.setText("Chưa Thanh Toán");
            holder.binding.tvTtThanhToan.setBackgroundColor(Color.rgb(150, 150, 150));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class HolderTraGop extends RecyclerView.ViewHolder {
        ItemTraGopBinding binding;

        public HolderTraGop(@NonNull ItemTraGopBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
