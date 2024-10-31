package com.tdc.vlxdonline.Activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.DonHangAdapter;
import com.tdc.vlxdonline.Adapter.ProductAdapter;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentDanhSachDonHangBinding;

import java.util.ArrayList;

public class DanhSachDonHangFragment extends Fragment {

    FragmentDanhSachDonHangBinding binding;
    // Danh sach don hang, duoc duyet theo loai nguoi dung
    ArrayList<DonHang> data = new ArrayList<>();
    DonHangAdapter adapter;
    // Tu khoa dung cho tim kiem
    private String tuKhoa = "";
    // Trang thai don da hoan thanh hay chua
    private int trangThaiLoc;
    private DatabaseReference referDanhSachDon;
    private ValueEventListener eventDocData;
    private Drawable draw;

    public DanhSachDonHangFragment(int type) {
        this.trangThaiLoc = type;
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
        // Nếu là nhân viên giao hàng thì tắt phân loại trạng thái
        if (LoginActivity.typeEmployee == 1) {
            binding.btnDaHoanThanh.setVisibility(View.GONE);
            binding.btnChuaHoanThanh.setVisibility(View.GONE);
        }
        referDanhSachDon = FirebaseDatabase.getInstance().getReference();
        setAdapterDonHang();
        draw = getActivity().getDrawable(R.drawable.bg_detail);
        draw.setTint(Color.rgb(0, 100, 255));
        KhoiTao();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Event Search
        binding.svDonHang.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tuKhoa = query;
                referDanhSachDon.child("bills").addListenerForSingleValueEvent(eventDocData);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    tuKhoa = "";
                    referDanhSachDon.child("bills").addListenerForSingleValueEvent(eventDocData);
                }
                return false;
            }
        });
        // Event chọn đã hoàn thành
        binding.btnDaHoanThanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnDaHoanThanh.setBackground(draw);
                binding.btnDaHoanThanh.setTextColor(Color.WHITE);
                binding.btnChuaHoanThanh.setBackgroundColor(Color.TRANSPARENT);
                binding.btnChuaHoanThanh.setTextColor(Color.BLACK);
                trangThaiLoc = 1;
                referDanhSachDon.child("bills").addListenerForSingleValueEvent(eventDocData);
            }
        });
        // Event chọn chưa hoàn thành
        binding.btnChuaHoanThanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnChuaHoanThanh.setBackground(draw);
                binding.btnChuaHoanThanh.setTextColor(Color.WHITE);
                binding.btnDaHoanThanh.setBackgroundColor(Color.TRANSPARENT);
                binding.btnDaHoanThanh.setTextColor(Color.BLACK);
                trangThaiLoc = 0;
                referDanhSachDon.child("bills").addListenerForSingleValueEvent(eventDocData);
            }
        });
    }

    private void KhoiTao() {
        if (trangThaiLoc == 1) {
            binding.btnDaHoanThanh.setBackground(draw);
            binding.btnDaHoanThanh.setTextColor(Color.WHITE);
            binding.btnChuaHoanThanh.setBackgroundColor(Color.TRANSPARENT);
            binding.btnChuaHoanThanh.setTextColor(Color.BLACK);
        }
        setValueEventDon();
        referDanhSachDon.child("bills").addValueEventListener(eventDocData);
    }

    private void setValueEventDon(){
        eventDocData = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    data.clear(); // Xóa danh sách cũ trước khi cập nhật

                    // Duyệt qua trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DonHang don = snapshot.getValue(DonHang.class);
                        if (!tuKhoa.isEmpty() && !don.getTenKhach().contains(tuKhoa) && !don.getDiaChi().contains(tuKhoa)) continue;
                        int vc = don.getTrangThai();
                        int tt = don.getTrangThaiTT();
                        if (LoginActivity.typeEmployee != 1){
                            // 0 la chua hoan thanh, continue neu don da xac nhan hoan thanh
                            if (trangThaiLoc == 0 && vc == 4 && tt == 2) continue;
                                // 1 la chua da hoan thanh, continue neu don chua xac nhan hoan thanh
                            else if (trangThaiLoc == 1) {
                                if (vc < 4) continue;
                                else if (tt < 2) continue;
                            }
                        }

                        if (LoginActivity.typeUser == 0) continue;
                        else if (LoginActivity.typeUser == 1 && !don.getIdKhach().equals(Customer_HomeActivity.info.getID())) continue;
                        else if (LoginActivity.typeEmployee == 0) continue;
                        else if (LoginActivity.typeEmployee == 1) {
                            // Xac nhan neu khong phai chu thi continue
                            if (true) continue;
                            // 0 la chua nhan don
                            if (trangThaiLoc == 0) if(vc != 1) continue;
                                // Neu khong phai don cua nhan vien nay thi continue
                            else if (true) continue;
                                // 1 la dang giao
                            else if (trangThaiLoc == 1) if (vc != 2 && vc != 3) continue;
                                // 2 la giao hoan tat
                            else if (trangThaiLoc == 2) if (vc < 4) continue;
                        }
                        data.add(don); // Thêm User vào danh sách
                    }

                    adapter.notifyDataSetChanged();
                }catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void setAdapterDonHang(){
        adapter = new DonHangAdapter(getActivity(), data);
        // Event Click Don Hang
        adapter.setOnItemDonHangClick(new DonHangAdapter.OnItemDonHangClick() {
            @Override
            public void onItemClick(int position) {
                DonHang donHang = data.get(position);
                if (donHang.getPhiTraGop() > 0) {

                } else {
                    ((Customer_HomeActivity) getActivity()).ReplaceFragment(new ChiTietDonFragment(donHang.getId()));
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcDanhSachDon.setLayoutManager(linearLayoutManager);
        binding.rcDanhSachDon.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        draw.setTint(Color.rgb(215,215,215));

        binding = null;

        // Loại bỏ listener của Firebase
        if (referDanhSachDon != null && eventDocData != null) {
            referDanhSachDon.child("bills").removeEventListener(eventDocData);
        }

        // Nullify references to help with garbage collection
        referDanhSachDon = null;
        eventDocData = null;

        super.onDestroyView();
    }
}