package com.tdc.vlxdonline.Activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentGiaoDienDonHangBinding;

import java.util.ArrayList;

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
                TaoDonNhapHangFragment taoDonNhapHangFragment = new TaoDonNhapHangFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, taoDonNhapHangFragment) // Đảm bảo ID này đúng với container của bạn
                        .addToBackStack(null) // Thêm vào back stack để có thể quay lại
                        .commit();
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