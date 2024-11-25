package com.tdc.vlxdonline.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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
    DatabaseReference reference;
    ValueEventListener event;

    // Constructor nhận vào ID sản phẩm
    public ChiTietSPKho_Fragment(String id) {
        this.idProduct = id; // Khởi tạo ID sản phẩm
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference();
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
        binding.edtThayDoiGia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String gia = binding.edtThayDoiGia.getText().toString();
                if (gia.length() > 10) {
                    binding.edtThayDoiGia.setText(gia.substring(0, 10));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

        // Inflate layout cho fragment này
        return binding.getRoot();
    }

    // Phương thức để hiển thị hộp thoại xác nhận cho việc thay doi gia
    private void showConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông Báo !").setMessage("Bạn có chắc chắn thay đổi giá ?") // Thông điệp bằng tiếng Việt
                .setCancelable(false)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        thayDoiGia(); // Gọi phương thức thêm số lượng
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel(); // Đóng hộp thoại
                    }
                });
        Drawable drawableIcon = getResources().getDrawable(android.R.drawable.ic_dialog_alert);
        drawableIcon.setTint(Color.RED);
        builder.setIcon(drawableIcon);
        Drawable drawableBg = getResources().getDrawable(R.drawable.bg_item_lg);
        drawableBg.setTint(Color.YELLOW);
        AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(drawableBg);
        alert.show(); // Hiển thị hộp thoại
    }

    // Phương thức để thay doi gia ban sản phẩm
    private void thayDoiGia() {
        String giaBanMoi = binding.edtThayDoiGia.getText().toString(); // Lấy số lượng nhập vào

        if (!giaBanMoi.isEmpty()) {

            int giaMoi = Integer.parseInt(giaBanMoi); // Chuyển đổi số lượng nhập thành số nguyên
            if (giaMoi > Integer.parseInt(product.getGiaNhap())) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


                // Lấy số lượng hiện có từ Firebase
                reference.child("products").child(idProduct).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        try {
                            Products product = snapshot.getValue(Products.class); // Lấy thông tin sản phẩm
                            if (product != null) {

                                // Cập nhật số lượng trong kho lên Firebase
                                reference.child("products").child(idProduct).child("giaBan").setValue(String.valueOf(giaMoi))
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getActivity(), "Đã thay doi gia san pham!", Toast.LENGTH_SHORT).show();
                                            binding.tvGiaSpDetail.setText(giaMoi + " VND"); // Cập nhật UI
                                            binding.edtThayDoiGia.setText(""); // Xóa ô nhập
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(getActivity(), "Không thể cập nhật gia!", Toast.LENGTH_SHORT).show()); // Xử lý lỗi
                            }
                        }catch (Exception e){
                            Log.e("Lỗi", "Lỗi khi thay đổi giá: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.d("l.d", "Lỗi khi thêm số lượng");
                    }
                });
            }else {
                Toast.makeText(getActivity(), "Giá bán phải lớn hơn giá nhập", Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(getActivity(), "Vui lòng nhập giá cần đổi!", Toast.LENGTH_SHORT).show(); // Yêu cầu nhập số lượng
        }
    }

    // Phương thức để tải dữ liệu sản phẩm từ Firebase
    private void docDulieu() {
        event = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Products prod = dataSnapshot.getValue(Products.class); // Lấy thông tin sản phẩm
                    if (prod != null) {
                        product = prod;
                        // Tải hình ảnh và cập nhật thông tin sản phẩm lên UI
                        Glide.with(getActivity()).load(product.getAnh()).into(binding.ivAnhChinh);
                        Glide.with(getActivity()).load(product.getAnh()).into(binding.imgDetail);
                        binding.tvTenSpDetail.setText(product.getTen());
                        binding.tvGiaSpDetail.setText(product.getGiaBan() + " VND");
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
        };
        reference.child("products").child(idProduct).addValueEventListener(event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        reference.child("products").child(idProduct).removeEventListener(event);
        event = null;
        reference = null;
    }
}
