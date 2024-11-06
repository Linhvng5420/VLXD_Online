package com.tdc.vlxdonline.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Activity.LoginActivity;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.databinding.ItemOwnerKhachhangRcvBinding;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KhachHangAdapter extends RecyclerView.Adapter<KhachHangAdapter.KhachHangViewHolder> {

    // Danh sách chứa dữ liệu khách hàng
    private List<KhachHang> khachHangList;

    // Biến callback cho sự kiện nhấn vào item
    private OnItemClickListener onItemClickListener;

    // Constructor để truyền dữ liệu khách hàng vào adapter
    public KhachHangAdapter(List<KhachHang> khachHangList) {
        this.khachHangList = khachHangList;
    }

    // Giao diện cho sự kiện nhấn vào item
    public interface OnItemClickListener {
        void onItemClick(KhachHang khachHang); // Phương thức được gọi khi một item được nhấn
    }

    // Phương thức thiết lập listener cho sự kiện nhấn vào item
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    // Tạo ViewHolder bằng View Binding để hiển thị item khách hàng
    public KhachHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng View Binding để inflate layout item khách hàng
        ItemOwnerKhachhangRcvBinding binding = ItemOwnerKhachhangRcvBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new KhachHangViewHolder(binding); // Trả về một ViewHolder mới với binding
    }

    @Override
    // Gán dữ liệu khách hàng vào ViewHolder cho từng item
    public void onBindViewHolder(@NonNull KhachHangViewHolder holder, int position) {
        KhachHang khachHang = khachHangList.get(position); // Lấy khách hàng tại vị trí 'position'
        holder.bind(khachHang); // Gọi phương thức bind để hiển thị dữ liệu khách hàng

        // Xử lý sự kiện nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(khachHang); // Gọi phương thức onItemClick khi item được nhấn
            }
        });
    }

    @Override
    // Trả về số lượng item trong danh sách khách hàng
    public int getItemCount() {
        return khachHangList.size();
    }

    // Phương thức để lấy danh sách khách hàng
    public List<KhachHang> getKhachHangList() {
        return khachHangList;
    }

    // ViewHolder cho dữ liệu khách hàng sử dụng View Binding
    public static class KhachHangViewHolder extends RecyclerView.ViewHolder {
        // Binding liên kết với layout của từng item khách hàng
        private final ItemOwnerKhachhangRcvBinding binding;

        // Constructor ViewHolder, nhận đối tượng binding
        public KhachHangViewHolder(@NonNull ItemOwnerKhachhangRcvBinding binding) {
            super(binding.getRoot()); // Gọi hàm super để liên kết View gốc
            this.binding = binding;
        }

        // Phương thức bind để gán dữ liệu khách hàng vào các view trong item
        public void bind(KhachHang khachHang) {
            // Hiển thị ID của khách hàng
            binding.tvID.setText(khachHang.getID());

            // Hiển thị tên khách hàng
            binding.tvTen.setText(khachHang.getTen());

            // Hiển thị tên khách hàng
            binding.tvsdt.setText(khachHang.getSdt());

            //TODO: Hiển thị loại khách hàng
            loaiKH(khachHang.getID());
        }

        // Hiển thị loại khách hàng từ Firebase
        private void loaiKH(String khachHangId) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("duyetkhachhang");

            String key = LoginActivity.idUser.substring(0, LoginActivity.idUser.indexOf("@"));
            databaseRef.child(key).child(khachHangId).child("trangthai")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String status = dataSnapshot.getValue(String.class);
                            if (status != null) {
                                if (status.equals("1")) {
                                    binding.tvLoaiKH.setText("Đã Duyệt");
                                } else {
                                    binding.tvLoaiKH.setText("Chưa Duyệt");
                                }
                            } else {
                                binding.tvLoaiKH.setText("Không xác định");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle potential errors
                            binding.tvLoaiKH.setText("Lỗi truy xuất dữ liệu");
                        }
                    });
        }
    }

    // Hàm cập nhật danh sách khi thực hiện tìm kiếm
    public void updateList(List<KhachHang> filteredList) {
        this.khachHangList = filteredList;
        notifyDataSetChanged(); // Thông báo cho adapter biết dữ liệu đã thay đổi
    }

    // Thêm phương thức sắp xếp danh sách khách hàng theo mã
    public void sortKhachHangList() {
        Collections.sort(khachHangList, new Comparator<KhachHang>() {
            @Override
            public int compare(KhachHang kh1, KhachHang kh2) {
                // Lấy phần số của mã NV và so sánh
                String id1 = kh1.getID().replaceAll("[^0-9]", ""); // Lấy số từ mã NV1
                String id2 = kh2.getID().replaceAll("[^0-9]", ""); // Lấy số từ mã NV2

                // So sánh các số sử dụng Long để tránh lỗi số quá lớn
                return Long.compare(Long.parseLong(id1), Long.parseLong(id2));
            }
        });

        notifyDataSetChanged(); // Cập nhật lại danh sách sau khi sắp xếp
    }
}
