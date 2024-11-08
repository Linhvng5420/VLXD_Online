package com.tdc.vlxdonline.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tdc.vlxdonline.R;

public class QuanLyKhoFragment extends Fragment {
    TextView tvQLSP, tvQLDM, tvQLDV, tvQLAnhSP, tvQLBanner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quan_ly_kho, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the views
        tvQLSP = view.findViewById(R.id.tvQLSP);
        tvQLDM = view.findViewById(R.id.tvQLDM);
        tvQLDV = view.findViewById(R.id.tvQLDV);
        tvQLAnhSP = view.findViewById(R.id.tvQLAnhSP);
        tvQLBanner = view.findViewById(R.id.tvQLBanner);

        // Set the OnClickListeners
        tvQLSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Warehouse_ThemSanPhamActivity.class);
                startActivity(intent);
            }
        });
        tvQLDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Warehouse_DanhMucActivity.class);
                startActivity(intent);
            }
        });
        tvQLDV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Warehouse_DonViActivity.class);
                startActivity(intent);
            }
        });
        tvQLAnhSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Warehouse_AnhSPActivity.class);
                startActivity(intent);
            }
        });
        tvQLBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Warehouse_BannerActivity.class);
                startActivity(intent);
            }
        });
    }
}
