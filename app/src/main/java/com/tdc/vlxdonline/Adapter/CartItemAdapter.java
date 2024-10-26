package com.tdc.vlxdonline.Adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline
.Model.CartItem;
import com.tdc.vlxdonline
.databinding.ItemCartBinding;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemHolder> {

    private Activity context;
    private ArrayList<CartItem> data;
    private OnItemCartClickListener onItemCartClickListener;

    public void setOnItemCartClickListener(OnItemCartClickListener onItemCartClickListener) {
        this.onItemCartClickListener = onItemCartClickListener;
    }

    public CartItemAdapter(Activity context, ArrayList<CartItem> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartItemHolder(ItemCartBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemHolder holder, int position) {
        CartItem item = data.get(position);
        Glide.with(context).load(item.getAnh()).into(holder.binding.imgCart);
        holder.binding.tvNameCart.setText(item.getTenSP());
        holder.binding.tvDesCart.setText(item.getMoTa());
        StringBuilder chuoi = new StringBuilder(item.getGia() + "");
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
        holder.binding.tvPrice.setText(chuoi);
        holder.binding.tvSl.setText(item.getSoLuong());
        holder.binding.cbCart.setChecked(item.isSelected());
        final int pos = position;
        holder.position = pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        ItemCartBinding binding;
        int position;

        public CartItemHolder(@NonNull ItemCartBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemCartClickListener != null) {
                        onItemCartClickListener.OnItemClick(v, position);
                    }
                }
            });
            binding.cbCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemCartClickListener != null) {
                        onItemCartClickListener.OnCheckBoxClick(v, position, binding.cbCart.isChecked());
                    }
                }
            });
            binding.btnDeleteCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemCartClickListener != null) {
                        onItemCartClickListener.OnDeleteClick(v, position);
                    }
                }
            });
            binding.btnGiam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemCartClickListener != null) {
                        onItemCartClickListener.OnReduceClick(v, position);
                    }
                }
            });
            binding.btnTang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemCartClickListener != null) {
                        onItemCartClickListener.OnIncreaseClick(v, position);
                    }
                }
            });
        }
    }

    public interface OnItemCartClickListener {
        void OnItemClick(View view, int position);

        void OnCheckBoxClick(View view, int position, boolean selected);

        void OnDeleteClick(View view, int position);

        void OnIncreaseClick(View view, int position);

        void OnReduceClick(View view, int position);
    }
}
