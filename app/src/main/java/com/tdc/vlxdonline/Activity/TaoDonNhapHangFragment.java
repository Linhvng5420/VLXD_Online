package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.CategoryAdapter;
import com.tdc.vlxdonline.Adapter.DonHangAdapter;
import com.tdc.vlxdonline.Adapter.ProductAdapter;

import com.tdc.vlxdonline.Model.ChiTietNhap;
import com.tdc.vlxdonline.Model.DonNhap;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentTaoDonNhapHangBinding;


import java.util.ArrayList;

public class TaoDonNhapHangFragment extends Fragment {
    FragmentTaoDonNhapHangBinding binding;
    ArrayList<Products> dsSanPham = new ArrayList<>();
    ProductAdapter adapter;
    DonHangAdapter donHangAdapter;
    ArrayList<ChiTietNhap> dsChiTiet = new ArrayList<>();
    ChiTietNhap temp = new ChiTietNhap();
    Products products = new Products();
    DatabaseReference reference;
    ValueEventListener eventDocDanhSach;
    private String tuKhoa = "";
    String category = "";
    View preView = null;
    int SoLuong = 0;
    DonNhap donNhap = new DonNhap();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tuKhoa = "";
        category = "";
        preView = null;
        reference = FirebaseDatabase.getInstance().getReference();
        setHienThiSanPham();

        DonHangAdapter donHangAdapter = new DonHangAdapter(getActivity(), dsChiTiet);
        binding.rcvChitiet.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rcvChitiet.setAdapter(donHangAdapter);
        // Thêm sự kiện tìm kiếm cho SearchView
        binding.svDonhang.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tuKhoa = query;
                reference.child("products").addListenerForSingleValueEvent(eventDocDanhSach);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    tuKhoa = "";
                    reference.child("products").addListenerForSingleValueEvent(eventDocDanhSach);
                }
                return false;
            }
        });

        binding.edtSoLuong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        setEvent();
//        btnXacNhan = binding.btnXacNhan; // Ensure binding is initialized
//        btnXacNhan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Replace with ChiTietSPKho_Fragment
//                ThongTinNhanHang_Fragment thongTinNhanHangFragment = new ThongTinNhanHang_Fragment();
//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_container, thongTinNhanHangFragment) // Replace with the correct container ID
//                        .addToBackStack(null) // Optional: add to back stack to allow user to navigate back
//                        .commit();
//            }
//        });



//        setHienThiHoaDon();
    }


    private void setEvent() {

// bug ấn nút xác nhận + thêm 1 item dsChiTiet
        dsChiTiet.add(new ChiTietNhap("0", "0", 1000, 95000, "Thép", "https://www.pexels.com/vi-vn/anh/c-nh-bai-d-xe-ng-m-moody-vao-ban-dem-29113387/"));
        donHangAdapter = new DonHangAdapter(getActivity(), dsChiTiet);
        donHangAdapter.notifyDataSetChanged();
    }


    private void setHienThiSanPham() {
        eventDocDanhSach = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dsSanPham.clear(); // Xóa danh sách cũ trước khi cập nhật
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Products product = snapshot.getValue(Products.class);
                    if (!category.isEmpty() && !category.equals(product.getDanhMuc())) continue;
                    if (!tuKhoa.isEmpty() && !product.getTen().contains(tuKhoa) && !product.getMoTa().contains(tuKhoa))
                        continue;
                    dsSanPham.add(product); // Thêm User vào danh sách
                }

                adapter = new ProductAdapter(getActivity(), dsSanPham, View.GONE);


                adapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        products = dsSanPham.get(position);

//                        ((Warehouse_HomeActivity) getActivity()).ReplaceFragment(new ChiTietSPKho_Fragment(products.getId()));
                        Toast.makeText(getActivity(), "Bạn đã chọn sản phẩm "+ products.getTen(), Toast.LENGTH_SHORT).show();
                        temp.setAnh(products.getAnh());
                        temp.setTen(products.getTen());
                        temp.setIdSanPham(products.getId());

//                        Toast.makeText(getActivity(), "Đã chọn sản phẩm " + products.getTen(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnBtnBuyClick(View view, int position) {

                    }
                });
                binding.rcvSanpham.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                binding.rcvSanpham.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
            }
        };

        reference.child("products").addValueEventListener(eventDocDanhSach);
    }


    //    private void setHienThiHoaDon(){
//        reference.child("products").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                dsSanPham.clear(); // Xóa danh sách cũ trước khi cập nhật
//
//                // Duyệt qua từng User trong DataSnapshot
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Products product = snapshot.getValue(Products.class);
//                    dsSanPham.add(product); // Thêm User vào danh sách
//                }
//
////                SapXepDanhSach();
//
//                // Xử lý danh sách userList (ví dụ: hiển thị trong RecyclerView)
//                // Event Click Product
//                adapter = new ProductAdapter(getActivity(), dsSanPham, View.GONE);
//
//                adapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
//                    @Override
//                    public void OnItemClick(View view, int position) {
//                        products = dsSanPham.get(position);
////                        ((Warehouse_HomeActivity)getActivity()).ReplaceFragment(new ChiTietSPKho_Fragment(products));
//
////                        Toast.makeText(getActivity(), "Đã chọn sản phẩm " + products.getTen(), Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public void OnBtnBuyClick(View view, int position) {
//
//                    }
//                });
//                binding.rcvChitiet.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//                binding.rcvChitiet.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaoDonNhapHangBinding.inflate(inflater, container, false);


        // đã chuyển được trang nhưng quay về lại +1 item ở rcv chi tiết 2
        binding.btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi phương thức để chuyển sang Fragment khác
                ((Warehouse_HomeActivity) getActivity()).ReplaceFragment(new ThongTinNhanHang_Fragment());
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}