package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.CategoryAdapter;
import com.tdc.vlxdonline.Adapter.ProductAdapter;
import com.tdc.vlxdonline.Model.ChiTietNhap;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentTaoDonNhapHangBinding;


import java.util.ArrayList;

public class TaoDonNhapHangFragment extends Fragment {

    FragmentTaoDonNhapHangBinding binding;
    ArrayList<Products> dsSanPham = new ArrayList<>();
    ProductAdapter adapter;
    ArrayList<ChiTietNhap> dsChiTiet = new ArrayList<>();
    Products products = new Products();
    Button btnNext1;
    DatabaseReference reference;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference();
        setHienThiSanPham();
    }

    private void setHienThiSanPham() {
        reference.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dsSanPham.clear(); // Xóa danh sách cũ trước khi cập nhật

                // Duyệt qua từng User trong DataSnapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Products product = snapshot.getValue(Products.class);
                    dsSanPham.add(product); // Thêm User vào danh sách
                }

//                SapXepDanhSach();

                // Xử lý danh sách userList (ví dụ: hiển thị trong RecyclerView)
                // Event Click Product
                adapter = new ProductAdapter(getActivity(), dsSanPham, View.GONE);

                adapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        products = dsSanPham.get(position);
//                        ((Warehouse_HomeActivity)getActivity()).ReplaceFragment(new ChiTietSPKho_Fragment(products));

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
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaoDonNhapHangBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}