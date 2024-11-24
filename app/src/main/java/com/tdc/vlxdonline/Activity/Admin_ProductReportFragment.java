package com.tdc.vlxdonline.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.AdminSanPhamAdapter;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentAdminProductReportBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin_ProductReportFragment extends Fragment {
    FragmentAdminProductReportBinding binding;
    List<Products> dsSanPham = new ArrayList<>();
    AdminSanPhamAdapter adapter;

    int countSP = 0; // đếm số lượng sp bị report

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminProductReportBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Khởi tạo Recycleview
        binding.rcvSanPham.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminSanPhamAdapter(new ArrayList<>());
        binding.rcvSanPham.setAdapter(adapter);

        getDataSanPham();
        setupOnClickItem();

        return view;
    }

    private void getDataSanPham() {
        // Xóa danh sách cũ
        dsSanPham.clear();
        adapter.getDsSanPham().clear();
        adapter.notifyDataSetChanged();
        countSP = 0;

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("khieunai").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotKhieuNai) {
                // Danh sách tạm thời để tránh cập nhật UI liên tục
                Map<String, Products> tempProductMap = new HashMap<>();

                // Lấy danh sách khiếu nại
                for (DataSnapshot snapshot : snapshotKhieuNai.getChildren()) {
                    String productId = snapshot.getKey();
                    if (productId != null) {
                        DatabaseReference dbSP = FirebaseDatabase.getInstance().getReference();
                        dbSP.child("products/" + productId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotSP) {
                                countSP = 0;
                                Products product = snapshotSP.getValue(Products.class);
                                if (product != null && snapshotKhieuNai.child("/" + productId + "/daxem").getValue(Boolean.class) == false) {
                                    // Cập nhật hoặc thêm sản phẩm mới vào danh sách tạm thời
                                    tempProductMap.put(productId, product);
                                    countSP++;
                                } else {
                                    // Xóa sản phẩm nếu không còn tồn tại
                                    tempProductMap.remove(productId);
                                }

                                // Cập nhật danh sách chính và giao diện
                                updateProductList(tempProductMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu sản phẩm!", Toast.LENGTH_SHORT).show();
                                Log.d("l.d", "onCancelled: " + error.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi truy cập Firebase!", Toast.LENGTH_SHORT).show();
                Log.d("l.d", "onCancelled: " + error.getMessage());
            }
        });
    }

    // Hàm cập nhật danh sách sản phẩm
    private void updateProductList(Map<String, Products> tempProductMap) {
        // Xóa danh sách cũ
        dsSanPham.clear();

        // Thêm các sản phẩm từ bản đồ tạm thời
        dsSanPham.addAll(tempProductMap.values());

        // Sắp xếp danh sách sản phẩm theo id
        dsSanPham.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));

        // Cập nhật adapter
        adapter.getDsSanPham().clear();
        adapter.getDsSanPham().addAll(dsSanPham);
        adapter.notifyDataSetChanged();

        // Hiển thị số lượng sản phẩm bị báo cáo
        binding.tvTonKho.setText("Số lượng sản phẩm đang bị báo cáo: " + countSP);
    }


    private void setupOnClickItem() {
        adapter.setOnItemClickListener(new AdminSanPhamAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Products products) {
                Bundle bundle = new Bundle();
                bundle.putString("idSP", products.getId());
                ProdDetailCustomerFragment fragment = new ProdDetailCustomerFragment(products.getId());
                fragment.setArguments(bundle);

                // Thực hiện chuyển đổi sang Fragment chi tiết, thay thế Fragment hiện tại
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
                        .commit();
            }
        });
    }
}