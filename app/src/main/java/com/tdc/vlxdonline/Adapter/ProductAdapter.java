package com.tdc.vlxdonline.Adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ItemProductBinding;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {

    private Activity context;
    private ArrayList<Products> data;
    private int displayButtonBuy;
    private OnItemProductClickListener onItemProductClickListener;

    public void setOnItemProductClickListener(OnItemProductClickListener onItemProductClickListener) {
        this.onItemProductClickListener = onItemProductClickListener;
    }

    public ProductAdapter(Activity context, ArrayList<Products> data, int displayButtonBuy) {
        this.context = context;
        this.data = data;
        this.displayButtonBuy = displayButtonBuy;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductHolder(ItemProductBinding.inflate(context.getLayoutInflater(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        Products product = data.get(position);
        Glide.with(context).load(product.getAnh()).into(holder.binding.imgItem);
        holder.binding.tvNameItem.setText(product.getTen());
        StringBuilder chuoi = new StringBuilder(product.getGiaBan());
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
        holder.binding.tvPriceItem.setText(chuoi);
        // SetUp lai danh gia truoc khi hien thi theo san pham
        holder.binding.tvChuaDg.setVisibility(View.VISIBLE);
        holder.binding.lnSoSao.setVisibility(View.VISIBLE);
        holder.binding.star1.setImageResource(R.drawable.star_disable);
        holder.binding.star2.setImageResource(R.drawable.star_disable);
        holder.binding.star3.setImageResource(R.drawable.star_disable);
        holder.binding.star4.setImageResource(R.drawable.star_disable);
        holder.binding.star5.setImageResource(R.drawable.star_disable);
        // Hien thi danh gia theo so sao
        double sao = Double.parseDouble(product.getSoSao());
        if (sao > 0) {
            holder.binding.tvChuaDg.setVisibility(View.GONE);
            holder.binding.star1.setImageResource(R.drawable.star);
            if (sao > 1.7) {
                holder.binding.star2.setImageResource(R.drawable.star);
            }
            if (sao > 2.7) {
                holder.binding.star3.setImageResource(R.drawable.star);
            }
            if (sao > 3.7) {
                holder.binding.star4.setImageResource(R.drawable.star);
            }
            if (sao > 4.7) {
                holder.binding.star5.setImageResource(R.drawable.star);
            }
        }else{
            holder.binding.lnSoSao.setVisibility(View.GONE);
        }

        holder.binding.btnBuy.setVisibility(displayButtonBuy);
        final int pos = position;
        holder.position = pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ProductHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;
        int position;
        public ProductHolder(@NonNull ItemProductBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemProductClickListener != null) {
                        onItemProductClickListener.OnItemClick(v, position);
                    }
                }
            });
            binding.btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemProductClickListener != null) {
                        onItemProductClickListener.OnBtnBuyClick(v, position);
                    }
                }
            });
        }
    }

    public interface OnItemProductClickListener{
        void OnItemClick(View view, int position);
        void OnBtnBuyClick(View view, int position);
    }
}
