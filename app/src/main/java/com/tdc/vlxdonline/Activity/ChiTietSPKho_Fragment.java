package com.tdc.vlxdonline.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentChiTietSpKhoBinding;

public class ChiTietSPKho_Fragment extends Fragment {

    Products product; // Biến để lưu thông tin sản phẩm
    String idProduct; // Biến để lưu ID sản phẩm
    FragmentChiTietSpKhoBinding binding; // Binding cho layout của fragment

    // Constructor nhận vào ID sản phẩm
    public ChiTietSPKho_Fragment(String id) {
        this.idProduct = id; // Khởi tạo ID sản phẩm
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChiTietSpKhoBinding.inflate(inflater, container, false);
        docDulieu(); // Tải dữ liệu sản phẩm từ Firebase

        // Listener cho nút quay lại để trở về fragment trước đó
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed(); // Quay về activity trước
            }
        });

        // Listener cho nút thêm số lượng
        binding.btnThemSoLuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirm(); // Hiển thị hộp thoại xác nhận thêm số lượng
            }
        });

        // Listener cho nút giảm số lượng
        binding.btnGiamSoLuong.setOnClickListener(view -> {
            showConfirm1(); // Hiển thị hộp thoại xác nhận giảm số lượng
        });

        // Inflate layout cho fragment này
        return binding.getRoot();
    }

    // Phương thức để hiển thị hộp thoại xác nhận cho việc thêm số lượng
    private void showConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Bạn có chắc chắn thêm số lượng ?") // Thông điệp bằng tiếng Việt
                .setCancelable(false)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        themSoLuong(); // Gọi phương thức thêm số lượng
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel(); // Đóng hộp thoại
                    }
                });
        AlertDialog alert = builder.create();
        alert.show(); // Hiển thị hộp thoại
    }

    // Phương thức để hiển thị hộp thoại xác nhận cho việc giảm số lượng
    private void showConfirm1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Bạn có chắc chắn giảm số lượng ?") // Thông điệp bằng tiếng Việt
                .setCancelable(false)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        giamSoLuong(); // Gọi phương thức giảm số lượng
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel(); // Đóng hộp thoại
                    }
                });
        AlertDialog alert = builder.create();
        alert.show(); // Hiển thị hộp thoại
    }

    // Phương thức để thêm số lượng vào kho sản phẩm
    private void themSoLuong() {
        String soLuongNhap = binding.edtNhapsoluong.getText().toString(); // Lấy số lượng nhập vào

        if (!soLuongNhap.isEmpty()) {
            int soLuongThem = Integer.parseInt(soLuongNhap); // Chuyển đổi số lượng nhập thành số nguyên
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            // Lấy số lượng hiện có từ Firebase
            reference.child("products").child(idProduct).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Products product = snapshot.getValue(Products.class); // Lấy thông tin sản phẩm
                    if (product != null) {
                        int currentStock = Integer.parseInt(product.getTonKho()); // Lấy số lượng hiện tại
                        int updatedStock = currentStock + soLuongThem; // Tính toán số lượng đã cập nhật

                        // Cập nhật số lượng trong kho lên Firebase
                        reference.child("products").child(idProduct).child("tonKho").setValue(String.valueOf(updatedStock))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getActivity(), "Đã thêm số lượng vào kho!", Toast.LENGTH_SHORT).show();
                                    binding.tvTonKhoDetail.setText("Kho: " + updatedStock); // Cập nhật UI
                                    binding.edtNhapsoluong.setText(""); // Xóa ô nhập
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getActivity(), "Không thể cập nhật số lượng!", Toast.LENGTH_SHORT).show()); // Xử lý lỗi
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d("l.d", "Lỗi khi thêm số lượng");
                }
            });

        } else {
            Toast.makeText(getActivity(), "Vui lòng nhập số lượng!", Toast.LENGTH_SHORT).show(); // Yêu cầu nhập số lượng
        }
    }

    // Phương thức để giảm số lượng từ kho sản phẩm
    private void giamSoLuong() {
        String inputQuantityStr = binding.edtNhapsoluong.getText().toString().trim(); // Lấy số lượng nhập vào

        if (inputQuantityStr.isEmpty()) {
            Toast.makeText(getActivity(), "Vui lòng nhập số lượng cần giảm!", Toast.LENGTH_SHORT).show(); // Yêu cầu nhập số lượng
            return;
        }
        int inputQuantity = Integer.parseInt(inputQuantityStr); // Chuyển đổi số lượng nhập thành số nguyên

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        // Lấy số lượng hiện có từ Firebase
        reference.child("products").child(idProduct).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Products product = dataSnapshot.getValue(Products.class); // Lấy thông tin sản phẩm
                if (product != null) {
                    // Lấy số lượng hiện tại và giảm số lượng nhập vào, đảm bảo không âm
                    int currentStock = Integer.parseInt(product.getTonKho());
                    if (inputQuantity <= currentStock) {
                        currentStock -= inputQuantity; // Cập nhật số lượng
                        reference.child("products").child(idProduct).child("tonKho").setValue(String.valueOf(currentStock)); // Cập nhật Firebase
                        binding.tvTonKhoDetail.setText("Kho: " + currentStock); // Cập nhật UI
                        Toast.makeText(getActivity(), "Giảm thành công!", Toast.LENGTH_SHORT).show(); // Thông báo thành công
                        binding.edtNhapsoluong.setText(""); // Xóa ô nhập
                    } else {
                        Toast.makeText(getActivity(), "Số lượng > 0", Toast.LENGTH_SHORT).show(); // Lỗi số lượng không hợp lệ
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi khi cập nhật số lượng", Toast.LENGTH_SHORT).show(); // Xử lý lỗi
            }
        });
    }

    // Phương thức để tải dữ liệu sản phẩm từ Firebase
    private void docDulieu() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("products").child(idProduct).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Products product = dataSnapshot.getValue(Products.class); // Lấy thông tin sản phẩm
                    if (product != null) {
                        // Tải hình ảnh và cập nhật thông tin sản phẩm lên UI
                        Glide.with(getActivity()).load(product.getAnh()).into(binding.ivAnhChinh);
                        Glide.with(getActivity()).load(product.getAnh()).into(binding.imgDetail);
                        binding.tvTenSpDetail.setText(product.getTen());
                        binding.tvGiaSpDetail.setText(product.getGia() + " VND");
                        binding.tvTonKhoDetail.setText("Kho: " + product.getTonKho());
                        binding.tvDaBanDetail.setText("Đã Bán: " + product.getDaBan());
                        binding.tvDonViDetail.setText(product.getDonVi());
                        binding.tvMoTaDetail.setText(product.getMoTa());
                    } else {
                        Toast.makeText(getActivity(), "Sản Phẩm Đã Bị Xóa!", Toast.LENGTH_SHORT).show(); // Sản phẩm không tìm thấy
                    }
                } catch (Exception e) {
                    Log.e("Lỗi", "Lỗi khi tải dữ liệu sản phẩm: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Lỗi", "Đọc dữ liệu không thành công: " + databaseError.getCode()); // Xử lý lỗi
            }
        });
    }
}
