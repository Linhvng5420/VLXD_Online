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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentChiTietSpKhoBinding;
import com.tdc.vlxdonline.databinding.FragmentGiaoDienKhoBinding;

public class ChiTietSPKho_Fragment extends Fragment {

    Products product;
    String idProduct;
    FragmentChiTietSpKhoBinding binding;

    public ChiTietSPKho_Fragment(String id) {
        this.idProduct = id;

        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChiTietSpKhoBinding.inflate(inflater, container, false);
        docDulieu();

//        if (product != null) {
//            binding.tvTenSpDetail.setText(product.getTen());
//            binding.tvGiaSpDetail.setText(product.getGia());
//            binding.tvTonKhoDetail.setText("Kho: " + product.getTonKho()); // Giả sử có thuộc tính tonKho trong Products
//            binding.tvDaBanDetail.setText("Đã bán: " + product.getDaBan()); // Giả sử có thuộc tính daBan trong Products
//            binding.tvMoTaDetail.setText(product.getMoTa()); // Giả sử có thuộc tính moTa trong Products
//
//            // Nếu bạn có một thuộc tính ảnh, hãy sử dụng Glide hoặc Picasso để tải ảnh
//            Glide.with(this)
//                    .load(product.getAnh()) // Giả sử có thuộc tính anh trong Products
//                    .into(binding.ivAnhChinh);
//            Glide.with(this)
//                    .load(product.getAnh()) // Sử dụng thuộc tính anh từ sản phẩm, có thể thay đổi nếu cần
//                    .into(binding.imgDetail); // Đảm bảo id đúng trong layout XML
//
//        }
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại fragment trước đó
                requireActivity().onBackPressed();
            }
        });

        binding.btnThemSoLuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirm();
            }
        });
        binding.btnGiamSoLuong.setOnClickListener(view -> {
            showConfirm1();
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void showConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Bạn có chắc chắn thêm số lượng ?")
                .setCancelable(false)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Thoát ứng dụng

                        themSoLuong(); // Đóng tất cả các activity và thoát ứng dụng
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Đóng hộp thoại, không thoát ứng dụng
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showConfirm1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Bạn có chắc chắn giảm số lượng ?")
                .setCancelable(false)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Thoát ứng dụng
                        giamSoLuong();
                        // Đóng tất cả các activity và thoát ứng dụng
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Đóng hộp thoại, không thoát ứng dụng
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void themSoLuong() {
        String soLuongNhap = binding.edtNhapsoluong.getText().toString();

        if (!soLuongNhap.isEmpty()) {
            int soLuongThem = Integer.parseInt(soLuongNhap);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            // Lấy số lượng hiện có từ Firebase
            reference.child("products").child(idProduct).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Products product = snapshot.getValue(Products.class);
                    if (product != null) {
                        int currentStock = Integer.parseInt(product.getTonKho());
                        int updatedStock = currentStock + soLuongThem;

                        // Cập nhật số lượng trong kho lên Firebase
                        reference.child("products").child(idProduct).child("tonKho").setValue(String.valueOf(updatedStock))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getActivity(), "Đã thêm số lượng vào kho!", Toast.LENGTH_SHORT).show();
                                    binding.tvTonKhoDetail.setText("Kho: " + updatedStock);
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getActivity(), "Không thể cập nhật số lượng!", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d("l.d", "binding.btnThemSoLuong.setOnClickListener");
                }
            });

        } else {
            Toast.makeText(getActivity(), "Vui lòng nhập số lượng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void giamSoLuong() {
        String inputQuantityStr = binding.edtNhapsoluong.getText().toString().trim();

        if (inputQuantityStr.isEmpty()) {
            Toast.makeText(getActivity(), "Vui lòng nhập số lượng cần giảm!", Toast.LENGTH_SHORT).show();
            return;
        }
        int inputQuantity = Integer.parseInt(inputQuantityStr);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("products").child(idProduct).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Products product = dataSnapshot.getValue(Products.class);
                if (product != null) {
                    // Lấy số lượng hiện tại và giảm đi số lượng nhập vào, đảm bảo không âm
                    int currentStock = Integer.parseInt(product.getTonKho());
                    if (inputQuantity <= currentStock) {
                        currentStock -= inputQuantity;
                        reference.child("products").child(idProduct).child("tonKho").setValue(String.valueOf(currentStock));
                        binding.tvTonKhoDetail.setText("Kho: " + currentStock);
                        Toast.makeText(getActivity(), "Giảm thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Số lượng > 0", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi khi cập nhật số lượng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void docDulieu() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("products").child(idProduct).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Products product = dataSnapshot.getValue(Products.class);
                    if (product != null) {
                        Glide.with(getActivity()).load(product.getAnh()).into(binding.ivAnhChinh);
                        Glide.with(getActivity()).load(product.getAnh()).into(binding.imgDetail);
                        binding.tvTenSpDetail.setText(product.getTen());
                        binding.tvGiaSpDetail.setText(product.getGia() + " VND");
                        binding.tvTonKhoDetail.setText("Kho: " + product.getTonKho());
                        binding.tvDaBanDetail.setText("Đã Bán: " + product.getDaBan());
                        binding.tvDonViDetail.setText(product.getDonVi());
                        binding.tvMoTaDetail.setText(product.getMoTa());
                    } else {
                        Toast.makeText(getActivity(), "Sản Phẩm Đã Bị Xóa!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}