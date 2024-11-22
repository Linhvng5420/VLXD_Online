package com.tdc.vlxdonline.Adapter;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Model.DanhGia;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ItemDanhGiaBinding;

import java.util.ArrayList;

public class DanhGiaAdapter extends RecyclerView.Adapter<DanhGiaAdapter.DanhGiaHolder> {

    Activity context;
    ArrayList<DanhGia> data;
    OnItemDanhGiaClick onItemDanhGiaClick;
    // Kieu item, 0 dung de xem, 1 dung de danh gia
    int type = 0;

    public void setOnItemDanhGiaClick(OnItemDanhGiaClick onItemDanhGiaClick) {
        this.onItemDanhGiaClick = onItemDanhGiaClick;
    }

    public DanhGiaAdapter(Activity context, ArrayList<DanhGia> data, int type) {
        this.context = context;
        this.data = data;
        this.type = type;
    }

    @NonNull
    @Override
    public DanhGiaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DanhGiaHolder(ItemDanhGiaBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DanhGiaHolder holder, int position) {
        DanhGia temp = data.get(position);

        Glide.with(context).load(temp.getAnh()).into(holder.binding.ivAnhDg);
        holder.binding.tvTenDg.setText(temp.getTen());
        holder.binding.edtMotaDg.setText(temp.getMoTa());
        if (type == 0) holder.binding.edtMotaDg.setEnabled(false);
        // Set lai so sao
        holder.binding.ivStartDg1.setImageResource(R.drawable.star_disable);
        holder.binding.ivStartDg2.setImageResource(R.drawable.star_disable);
        holder.binding.ivStartDg3.setImageResource(R.drawable.star_disable);
        holder.binding.ivStartDg4.setImageResource(R.drawable.star_disable);
        holder.binding.ivStartDg5.setImageResource(R.drawable.star_disable);
        if (temp.getSoSao() > 0) {
            holder.binding.ivStartDg1.setImageResource(R.drawable.star);
        }
        if (temp.getSoSao() > 1) {
            holder.binding.ivStartDg2.setImageResource(R.drawable.star);
        }
        if (temp.getSoSao() > 2) {
            holder.binding.ivStartDg3.setImageResource(R.drawable.star);
        }
        if (temp.getSoSao() > 3) {
            holder.binding.ivStartDg4.setImageResource(R.drawable.star);
        }
        if (temp.getSoSao() > 4) {
            holder.binding.ivStartDg5.setImageResource(R.drawable.star);
        }

        final int pos = position;
        holder.position = pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DanhGiaHolder extends RecyclerView.ViewHolder{
        ItemDanhGiaBinding binding;
        int position;

        public DanhGiaHolder(@NonNull ItemDanhGiaBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDanhGiaClick != null) {
                        onItemDanhGiaClick.onItemClick(position);
                    }
                }
            });
            binding.ivStartDg1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDanhGiaClick != null) {
                        onItemDanhGiaClick.onStartClick(position, 1);
                    }
                }
            });
            binding.ivStartDg2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDanhGiaClick != null) {
                        onItemDanhGiaClick.onStartClick(position, 2);
                    }
                }
            });
            binding.ivStartDg3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDanhGiaClick != null) {
                        onItemDanhGiaClick.onStartClick(position, 3);
                    }
                }
            });
            binding.ivStartDg4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDanhGiaClick != null) {
                        onItemDanhGiaClick.onStartClick(position, 4);
                    }
                }
            });
            binding.ivStartDg5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDanhGiaClick != null) {
                        onItemDanhGiaClick.onStartClick(position, 5);
                    }
                }
            });
            binding.edtMotaDg.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String moTa = s.toString();
                    if (onItemDanhGiaClick != null) {
                        onItemDanhGiaClick.onChangeDescrip(position, moTa);
                    }
                }
            });
        }
    }

    public interface OnItemDanhGiaClick{
        void onItemClick(int position);
        void onStartClick(int position, int soSao);
        void onChangeDescrip(int position, String chuoi);
    }
}
