package com.tdc.vlxdonline.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ItemAdminSanphamRcvBinding;

import java.util.List;

public class AdminSanPhamAdapter extends RecyclerView.Adapter<AdminSanPhamAdapter.AdminSanPhamViewHolder> {
    List<Products> dsSanPham;

    public AdminSanPhamAdapter(List<Products> dsSanPham) {
        this.dsSanPham = dsSanPham;
    }

    public List<Products> getDsSanPham() {
        return dsSanPham;
    }

    public void setDsSanPham(List<Products> dsSanPham) {
        this.dsSanPham = dsSanPham;
    }

    // Biến callback cho sự kiện nhấn vào item
    private OnItemClickListener onItemClickListener;

    // Giao diện cho sự kiện nhấn vào item
    public interface OnItemClickListener {
        void onItemClick(Products products); // Phương thức được gọi khi một item được nhấn
    }

    // Phương thức thiết lập listener cho sự kiện nhấn vào item
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public AdminSanPhamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdminSanPhamViewHolder(ItemAdminSanphamRcvBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminSanPhamViewHolder holder, int position) {
        Products sanPham = dsSanPham.get(position);
        holder.bind(sanPham);

        // Xử lý sự kiện nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(sanPham); // Gọi phương thức onItemClick khi item được nhấn
            }
        });
    }

    @Override
    public int getItemCount() {
        return dsSanPham.size();
    }

    public class AdminSanPhamViewHolder extends RecyclerView.ViewHolder {
        ItemAdminSanphamRcvBinding binding;

        public AdminSanPhamViewHolder(@NonNull ItemAdminSanphamRcvBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void bind(Products sanPham) {
            binding.tvID.setText(sanPham.getId());
            binding.tvTen.setText(sanPham.getTen());
            binding.tvTonKho.setText("Tồn Kho: " + sanPham.getTonKho() + " " + sanPham.getDonVi());
            binding.tvGia.setText("Giá Bán: " + sanPham.getGiaBan() + "VND");
            binding.tvGiaNhap.setText("Giá Nhập: " + sanPham.getGiaNhap() + "VND");

            // Hiển thị ảnh sản phẩm từ Firebase
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("products");
            db.child(sanPham.getId() + "/anh").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String image = dataSnapshot.getValue(String.class);

                        // Kiểm tra nếu giá trị không rỗng và không phải "N/A"
                        if (image != null && !image.equals("N/A")) {
                            Glide.with(binding.getRoot().getContext()).load(image).placeholder(R.drawable.baseline_downloading_24).error(android.R.drawable.ic_menu_report_image).into(binding.imageView);
                        } else {
                            // Nếu không có ảnh, hiển thị ảnh mặc định
                            binding.imageView.setImageResource(R.drawable.cho_danh_gia);
                        }
                    } else {
                        // Không có dữ liệu, hiển thị ảnh mặc định
                        binding.imageView.setImageResource(R.drawable.cho_danh_gia);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    binding.imageView.setImageResource(android.R.drawable.ic_menu_report_image);
                }
            });
        }

    }
}
