package com.tdc.vlxdonline.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.ThongTinChu;
import com.tdc.vlxdonline.databinding.ItemOwnerRecycleviewBinding;

import java.util.List;

public class CuaHangAdapter extends RecyclerView.Adapter<CuaHangAdapter.CuaHangViewHolder> {
    List<ThongTinChu> cuaHangList;
    private OnItemClickListener onItemClickListener;

    public CuaHangAdapter(List<ThongTinChu> cuaHangList) {
        this.cuaHangList = cuaHangList;
    }

    public List<ThongTinChu> getCuaHangList() {
        return cuaHangList;
    }

    public void setCuaHangList(List<ThongTinChu> cuaHangList) {
        this.cuaHangList = cuaHangList;
    }

    // Giao diện cho sự kiện nhấn vào item
    public interface OnItemClickListener {
        void onItemClick(ThongTinChu cuahang); // Phương thức được gọi khi một item được nhấn
    }

    // Phương thức thiết lập listener cho sự kiện nhấn vào item
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
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

        // Xử lý sự kiện nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(cuaHang); // Gọi phương thức onItemClick khi item được nhấn
            }
        });
    }

    @Override
    public int getItemCount() {
        return cuaHangList.size();
    }

    public class CuaHangViewHolder extends RecyclerView.ViewHolder {
        final ItemOwnerRecycleviewBinding binding;

        public CuaHangViewHolder(@NonNull ItemOwnerRecycleviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void binding(ThongTinChu cuaHang) {
            binding.tvID.setText(cuaHang.getId());
            binding.tvSDT.setText(cuaHang.getSdt());
            String tenCuaHang = "N/A";

            DatabaseReference dbTenCuaHang = FirebaseDatabase.getInstance().getReference("thongtinchu/" + cuaHang.getId() + "/cuahang");

            dbTenCuaHang.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String tenCuaHang = snapshot.getValue(String.class);
                    binding.tvTen.setText(tenCuaHang != null && !tenCuaHang.equals("N/A") ? tenCuaHang : cuaHang.getTen());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // Lấy trạng thái tài khoản cửa hàng
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("account/" + cuaHang.getId());
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean lock = snapshot.child("trangthai/lock").getValue(Boolean.class);
                    Boolean online = snapshot.child("trangthai/online").getValue(Boolean.class);
                    String locktime = snapshot.child("trangthai/locktime").getValue(String.class);
                    String locktype = snapshot.child("trangthai/locktype").getValue(String.class);

                    if (lock == null) {
                        Toast.makeText(binding.getRoot().getContext(), "Lỗi đăng nhập tài khoản", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (lock) {
                        binding.tvPhu.setText(locktype.equals("vinhvien") ? "Khóa Vĩnh Viễn" : "Khóa: " + locktime);
                    } else {
                        binding.tvPhu.setText(online ? "Online" : "Offline");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    binding.tvPhu.setText("N/A");
                }
            });
        }
    }
}
