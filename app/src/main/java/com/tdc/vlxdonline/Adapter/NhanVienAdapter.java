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
import com.tdc.vlxdonline.Model.NhanVien;
import com.tdc.vlxdonline.databinding.ItemOwnerNhanvienRcvBinding;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NhanVienAdapter extends RecyclerView.Adapter<NhanVienAdapter.NhanVienViewHolder> {

    // Danh sách chứa dữ liệu nhân viên
    private List<NhanVien> nhanVienList;

    // Biến callback cho sự kiện nhấn vào item
    private OnItemClickListener onItemClickListener;

    // Constructor để truyền dữ liệu nhân viên vào adapter
    public NhanVienAdapter(List<NhanVien> nhanVienList) {
        this.nhanVienList = nhanVienList;
    }

    // Giao diện cho sự kiện nhấn vào item
    public interface OnItemClickListener {
        void onItemClick(NhanVien nhanVien); // Phương thức được gọi khi một item được nhấn
    }

    // Phương thức thiết lập listener cho sự kiện nhấn vào item
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    // Tạo ViewHolder bằng View Binding để hiển thị item nhân viên
    public NhanVienViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng View Binding để inflate layout item nhân viên
        ItemOwnerNhanvienRcvBinding binding = ItemOwnerNhanvienRcvBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NhanVienViewHolder(binding); // Trả về một ViewHolder mới với binding
    }

    @Override
    // Gán dữ liệu nhân viên vào ViewHolder cho từng item
    public void onBindViewHolder(@NonNull NhanVienViewHolder holder, int position) {
        NhanVien nhanVien = nhanVienList.get(position); // Lấy nhân viên tại vị trí 'position'
        holder.bind(nhanVien); // Gọi phương thức bind để hiển thị dữ liệu nhân viên

        // Xử lý sự kiện nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(nhanVien); // Gọi phương thức onItemClick khi item được nhấn
            }
        });
    }

    @Override
    // Trả về số lượng item trong danh sách nhân viên
    public int getItemCount() {
        return nhanVienList.size();
    }

    // Phương thức để lấy danh sách nhân viên
    public List<NhanVien> getNhanVienList() {
        return nhanVienList;
    }

    // ViewHolder cho dữ liệu nhân viên sử dụng View Binding
    public static class NhanVienViewHolder extends RecyclerView.ViewHolder {
        // Binding liên kết với layout của từng item nhân viên
        private final ItemOwnerNhanvienRcvBinding binding;

        // Constructor ViewHolder, nhận đối tượng binding
        public NhanVienViewHolder(@NonNull ItemOwnerNhanvienRcvBinding binding) {
            super(binding.getRoot()); // Gọi hàm super để liên kết View gốc
            this.binding = binding;
        }

        // Phương thức bind để gán dữ liệu nhân viên vào các view trong item
        public void bind(NhanVien nhanVien) {
            // Hiển thị ID của nhân viên
            binding.tvcccd.setText(nhanVien.getCccd());

            // Hiển thị tên nhân viên
            binding.tvTenNV.setText(nhanVien.getTennv());

            // Hiển thị chức vụ từ Firebase nếu chucVuId không null
            String chucVuId = nhanVien.getChucvu();
            if (chucVuId != null && !chucVuId.isEmpty()) {
                chucVuTuFireBase(chucVuId);
            } else {
                binding.tvChucVu.setText("N/A"); // Hiển thị "N/A" nếu chucVuId là null hoặc rỗng
            }
        }

        // Hiển thị chức vụ từ Firebase
        private void chucVuTuFireBase(String chucVuId) {
            DatabaseReference chucVuRef = FirebaseDatabase.getInstance().getReference("chucvu").child(chucVuId);

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
            });
        }
    }

    // Hàm cập nhật danh sách khi thực hiện tìm kiếm
    public void updateList(List<NhanVien> filteredList) {
        this.nhanVienList = filteredList;
        notifyDataSetChanged(); // Thông báo cho adapter biết dữ liệu đã thay đổi
    }

    // Thêm phương thức sắp xếp danh sách nhân viên theo mã NV
    public void sortNhanVienList() {
        Collections.sort(nhanVienList, new Comparator<NhanVien>() {
            @Override
            public int compare(NhanVien nv1, NhanVien nv2) {
                // Lấy phần số của mã NV và so sánh
                String id1 = nv1.getCccd().replaceAll("[^0-9]", ""); // Lấy số từ mã NV1
                String id2 = nv2.getCccd().replaceAll("[^0-9]", ""); // Lấy số từ mã NV2

                // So sánh các số sử dụng Long để tránh lỗi số quá lớn
                return Long.compare(Long.parseLong(id1), Long.parseLong(id2));
            }
        });

        notifyDataSetChanged(); // Cập nhật lại danh sách sau khi sắp xếp
    }
}
