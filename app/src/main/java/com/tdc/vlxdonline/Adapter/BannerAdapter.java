package com.tdc.vlxdonline.Adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Model.Banner;
import com.tdc.vlxdonline.databinding.ItemBannerBinding;

import java.util.ArrayList;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerHolder> {

    Activity context;
    ArrayList<Banner> data;

    public BannerAdapter(Activity context, ArrayList<Banner> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BannerHolder(ItemBannerBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BannerHolder holder, int position) {
        Banner banner = data.get(position);
        Glide.with(context).load(banner.getAnh()).into(holder.binding.ivItemBanner);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class BannerHolder extends RecyclerView.ViewHolder{

        ItemBannerBinding binding;

        public BannerHolder(@NonNull ItemBannerBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
