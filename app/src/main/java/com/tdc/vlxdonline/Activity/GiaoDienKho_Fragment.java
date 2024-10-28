package com.tdc.vlxdonline.Activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.CategoryAdapter;
import com.tdc.vlxdonline.Adapter.DonHangAdapter;
import com.tdc.vlxdonline.Adapter.ProductAdapter;
import com.tdc.vlxdonline.Model.Categorys;
import com.tdc.vlxdonline.Model.ChiTietNhap;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentGiaoDienKhoBinding;

import java.util.ArrayList;
import java.util.List;


public class GiaoDienKho_Fragment extends Fragment {

    FragmentGiaoDienKhoBinding binding;
    ProductAdapter adapter;
    ArrayList<ChiTietNhap> dsChiTiet = new ArrayList<>();
    Products products = new Products();
    RecyclerView recyclerView;
    ArrayList<Products> list = new ArrayList<>();
    DatabaseReference reference;
    ValueEventListener listener;
    CategoryAdapter adapterCate;
    ArrayList<Categorys> dsCategory = new ArrayList<>();
    ArrayList<Products> dsSanPham = new ArrayList<>();
    String category = "";
    View preView = null;
    ValueEventListener eventDocDanhSach;
    private String tuKhoa = "";


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
        readcategorysFromDatabase();
        // Thêm sự kiện tìm kiếm cho SearchView
        binding.svCustomerHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
    }


    private void setHienThiSanPham() {
        eventDocDanhSach = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dsSanPham.clear(); // Xóa danh sách cũ trước khi cập nhật

                // Duyệt qua từng User trong DataSnapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Products product = snapshot.getValue(Products.class);
                    if (!category.isEmpty() && !category.equals(product.getDanhMuc())) continue;
                    if (!tuKhoa.isEmpty() && !product.getTen().contains(tuKhoa) && !product.getMoTa().contains(tuKhoa))
                        continue;
                    dsSanPham.add(product); // Thêm User vào danh sách
                }

//                SapXepDanhSach();

                // Xử lý danh sách userList (ví dụ: hiển thị trong RecyclerView)
                // Event Click Product
                adapterCate = new CategoryAdapter(getActivity(), dsCategory);
                adapter = new ProductAdapter(getActivity(), dsSanPham, View.GONE);


                adapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        products = dsSanPham.get(position);
                        ((Warehouse_HomeActivity) getActivity()).ReplaceFragment(new ChiTietSPKho_Fragment(products.getId()));

//                        Toast.makeText(getActivity(), "Đã chọn sản phẩm " + products.getTen(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnBtnBuyClick(View view, int position) {

                    }
                });
                binding.rcvSanpham1.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                binding.rcvSanpham1.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
            }
        };

        reference.child("products").addValueEventListener(eventDocDanhSach);
    }

    private void readcategorysFromDatabase() {
        reference.child("categorys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dsCategory.clear(); // Xóa danh sách cũ trước khi cập nhật

                    // Duyệt qua từng User trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Categorys category = snapshot.getValue(Categorys.class);
                        dsCategory.add(category); // Thêm User vào danh sách
                    }
                    // Xử lý danh sách userList (ví dụ: hiển thị trong RecyclerView)
                    // Category Adapter
                    adapterCate = new CategoryAdapter(getActivity(), dsCategory);
                    adapterCate.setOnItemCategoryClickListener(new CategoryAdapter.OnItemCategoryClickListener() {
                        @Override
                        public void OnItemClick(View view, int position) {
                            if (category.equals(dsCategory.get(position).getId())) {
                                category = "";
                                view.setBackgroundColor(Color.TRANSPARENT);
                                preView = null;
                            } else {
                                category = dsCategory.get(position).getId();
                                Drawable drawable = getActivity().getDrawable(R.drawable.bg_detail);
                                view.setBackground(drawable);
                                if (preView != null) preView.setBackgroundColor(Color.TRANSPARENT);
                                preView = view;
                            }
                            reference.child("products").addListenerForSingleValueEvent(eventDocDanhSach);
                        }
                    });
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    binding.rcvCategory.setLayoutManager(linearLayoutManager);
                    binding.rcvCategory.setAdapter(adapterCate);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGiaoDienKhoBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}