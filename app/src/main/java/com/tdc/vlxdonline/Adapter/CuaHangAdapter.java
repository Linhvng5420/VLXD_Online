package com.tdc.vlxdonline.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tdc.vlxdonline.Model.ThongTinChu;
import com.tdc.vlxdonline.databinding.ItemOwnerRecycleviewBinding;

import java.util.List;

public class CuaHangAdapter extends RecyclerView.Adapter<CuaHangAdapter.CuaHangViewHolder> {
    List<ThongTinChu> cuaHangList;

    public CuaHangAdapter(List<ThongTinChu> cuaHangList) {
        this.cuaHangList = cuaHangList;
    }

    public List<ThongTinChu> getCuaHangList() {
        return cuaHangList;
    }

    public void setCuaHangList(List<ThongTinChu> cuaHangList) {
        this.cuaHangList = cuaHangList;
    }

    //Viewholder
    @NonNull
    @Override
    public CuaHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOwnerRecycleviewBinding binding = ItemOwnerRecycleviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CuaHangViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CuaHangAdapter.CuaHangViewHolder holder, int position) {
        ThongTinChu cuaHang = cuaHangList.get(position);
        holder.binding(cuaHang);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CuaHangViewHolder extends RecyclerView.ViewHolder {
        final ItemOwnerRecycleviewBinding binding;

        public CuaHangViewHolder(@NonNull ItemOwnerRecycleviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void binding(ThongTinChu cuaHang) {
            binding.tvID.setText(cuaHang.getId());
            binding.tvTen.setText(cuaHang.getTen());
            binding.tvSDT.setText(cuaHang.getSdt());
        }
    }


}
