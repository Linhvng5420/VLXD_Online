package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentTaiKhoanNVBinding;

public class TaiKhoanNVFragment extends Fragment {
    FragmentTaiKhoanNVBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTaiKhoanNVBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Goi Event o day
        binding.lnHoSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_owner, new TaiKhoanNVHoSoFragment()) // Thay thế fragment_owner hiện tại bằng fragment chi tiết
                        .addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
                        .commit(); // Thực hiện chuyển đổi
            }
        });

        binding.lnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_owner, new Owner_SettingAboutFragment()) // Thay thế fragment_owner hiện tại bằng fragment chi tiết
                        .addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
                        .commit(); // Thực hiện chuyển đổi
            }
        });

    }

}