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
import java.util.List;

public class Admin_ProductReportFragment extends Fragment {
    FragmentAdminProductReportBinding binding;
    List<Products> dsSanPham = new ArrayList<>();
    AdminSanPhamAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("khieunai").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatabaseReference dbSP = FirebaseDatabase.getInstance().getReference();
                    dbSP.child("products/" + snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Products product = new Products();
                            product = snapshot.getValue(Products.class);
                            if (product != null) {
                                adapter.getDsSanPham().add(product);
                                dsSanPham.add(product);
                            }

                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu sản phẩm!", Toast.LENGTH_SHORT).show();
                            Log.d("l.d", "onCancelled: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
                        .commit();
            }
        });
    }
}