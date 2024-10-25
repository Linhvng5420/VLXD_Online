package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentChiTietSpKhoBinding;
import com.tdc.vlxdonline.databinding.FragmentGiaoDienKhoBinding;

public class ChiTietSPKho_Fragment extends Fragment {

    Products product;
    FragmentChiTietSpKhoBinding binding;

    public ChiTietSPKho_Fragment(Products product) {
        this.product = product;

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
        if (product != null) {
            binding.tvTenSpDetail.setText(product.getTen());
            binding.tvGiaSpDetail.setText(product.getGia());
            binding.tvTonKhoDetail.setText("Kho: " + product.getTonKho()); // Giả sử có thuộc tính tonKho trong Products
            binding.tvDaBanDetail.setText("Đã bán: " + product.getDaBan()); // Giả sử có thuộc tính daBan trong Products
            binding.tvMoTaDetail.setText(product.getMoTa()); // Giả sử có thuộc tính moTa trong Products

            // Nếu bạn có một thuộc tính ảnh, hãy sử dụng Glide hoặc Picasso để tải ảnh
            Glide.with(this)
                    .load(product.getAnh()) // Giả sử có thuộc tính anh trong Products
                    .into(binding.ivAnhChinh);
            Glide.with(this)
                    .load(product.getAnh()) // Sử dụng thuộc tính anh từ sản phẩm, có thể thay đổi nếu cần
                    .into(binding.imgDetail); // Đảm bảo id đúng trong layout XML

        }
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại fragment trước đó
                requireActivity().onBackPressed();
            }
        });


        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}