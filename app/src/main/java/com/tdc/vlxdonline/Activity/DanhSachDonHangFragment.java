package com.tdc.vlxdonline.Activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.tdc.vlxdonline.Adapter.DonHangAdapter;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentDanhSachDonHangBinding;

import java.util.ArrayList;

public class DanhSachDonHangFragment extends Fragment {

    FragmentDanhSachDonHangBinding binding;
    // Danh sach don hang, duoc duyet theo loai nguoi dung
    ArrayList<DonHang> data = new ArrayList<>();
    DonHangAdapter adapter;
    // Type su dung khi nguoi dung la nhan vien giao hang, 0 Nhan don, 1 Dang giao, 2 Hoan thanh
    private int type;
    // Tu khoa dung cho tim kiem
    private String tuKhoa = "";
    // Trang thai don da hoan thanh hay chua, -1 Exception cho Giao hang, 0 Don chua hoan thanh, 1 Don da hoan thanh
    private int trangThaiLoc = 0;

    public DanhSachDonHangFragment(int type) {
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDanhSachDonHangBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Nếu là nhân viên giao hàng thì tắt phân loại trạng thái
        if (LoginActivity.typeUser == 2 && LoginActivity.typeEmployee == 1) {
            binding.btnDaHoanThanh.setVisibility(View.GONE);
            binding.btnChuaHoanThanh.setVisibility(View.GONE);
            if (type == 0) {
                trangThaiLoc = -1;
            } else if (type == 2) {
                trangThaiLoc = 1;
            }
        }
        KhoiTao();
        adapter = new DonHangAdapter(getActivity(), data);
        // Event Click Don Hang
        adapter.setOnItemDonHangClick(new DonHangAdapter.OnItemDonHangClick() {
            @Override
            public void onItemClick(int position) {
                DonHang donHang = data.get(position);
                if (Integer.parseInt(donHang.getPhiTraGop()) > 0) {

                } else {
                    ((Customer_HomeActivity) getActivity()).ReplaceFragment(new ChiTietDonFragment(donHang));
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcDanhSachDon.setLayoutManager(linearLayoutManager);
        binding.rcDanhSachDon.setAdapter(adapter);
        // Event Search
        binding.svDonHang.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tuKhoa = query;
                KhoiTao();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // Event chọn đã hoàn thành
        binding.btnDaHoanThanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnDaHoanThanh.setBackgroundColor(Color.WHITE);
                Drawable draw = getActivity().getDrawable(R.drawable.bg_img_detail);
                binding.btnChuaHoanThanh.setBackground(draw);
                trangThaiLoc = 1;
                KhoiTao();
            }
        });
        // Event chọn chưa hoàn thành
        binding.btnChuaHoanThanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnChuaHoanThanh.setBackgroundColor(Color.WHITE);
                Drawable draw = getActivity().getDrawable(R.drawable.bg_img_detail);
                binding.btnDaHoanThanh.setBackground(draw);
                trangThaiLoc = 0;
                KhoiTao();
            }
        });
    }

    private void KhoiTao() {
        // Reset Data Don Hang
        data.clear();

        if (!tuKhoa.isEmpty()) {
        }
        if (trangThaiLoc != -1){
            if (trangThaiLoc == 0) {

            } else if (trangThaiLoc == 1) {
                if (true) {

                } else if (false) {

                }
            }
        }

        if (LoginActivity.typeUser == 0) {

        } else if (LoginActivity.typeUser == 1) {

        } else if (LoginActivity.typeEmployee == 0) {

        } else if (LoginActivity.typeEmployee == 1) {
            if (type == 0) {

            } else if (type == 1) {

            } else if (type == 2) {

            }
        }

        if (adapter != null) adapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}