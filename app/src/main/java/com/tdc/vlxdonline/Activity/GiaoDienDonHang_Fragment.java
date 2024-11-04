package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentGiaoDienDonHangBinding;

public class GiaoDienDonHang_Fragment extends Fragment {

    FragmentGiaoDienDonHangBinding binding;


    public GiaoDienDonHang_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGiaoDienDonHangBinding.inflate(inflater, container, false);

        binding.btnAddDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi phương thức để chuyển sang Fragment khác
                ((Warehouse_HomeActivity)getActivity()).ReplaceFragment(new TaoDonNhapHangFragment());
            }
        });
        binding.btnLoadDH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return binding.getRoot();
    }
}