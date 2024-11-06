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
            binding.tvcccd.setText(khachHang.getID());

            // Hiển thị tên khách hàng
            binding.tvTenNV.setText(khachHang.getTen());

            //TODO: Hiển thị loại khách hàng

        }

        // Hiển thị loại khách hàng từ Firebase
        private void chucVuTuFireBase(String chucVuId) {
            /*DatabaseReference chucVuRef = FirebaseDatabase.getInstance().getReference("chucvu").child(chucVuId);

            // Lấy dữ liệu tên chức vụ từ Firebase
            chucVuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Lấy tên chức vụ từ Firebase
                        String tenChucVu = dataSnapshot.child("ten").getValue(String.class);
                        binding.tvChucVu.setText(tenChucVu != null ? tenChucVu : "N/A"); // Gán tên chức vụ vào TextView
                    } else {
                        binding.tvChucVu.setText("N/A"); // Nếu không tìm thấy chức vụ, hiển thị "N/A"
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    binding.tvChucVu.setText("N/A"); // Xử lý lỗi nếu có
                }
            });*/
        }
    }

    // Hàm cập nhật danh sách khi thực hiện tìm kiếm
    public void updateList(List<KhachHang> filteredList) {
        this.khachHangList = filteredList;
        notifyDataSetChanged(); // Thông báo cho adapter biết dữ liệu đã thay đổi
    }

    // Thêm phương thức sắp xếp danh sách khách hàng theo mã NV
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
