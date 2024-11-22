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

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.AdminSanPhamAdapter;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentAdminCuahangSanphamBinding;

import java.util.ArrayList;
import java.util.List;

public class Admin_CuaHangSanPhamFragment extends Fragment {
    FragmentAdminCuahangSanphamBinding binding;
    String cuahangID;
    final long[] soluongSP = {0};

    List<Products> dsSanPham = new ArrayList<>();
    AdminSanPhamAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cuahangID = getArguments().getString("idCH");
        Log.d("l.d", "onCreate: getAgruments cuahangID: " + cuahangID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminCuahangSanphamBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.tvID.setText("ID Cửa Hàng [ " + cuahangID + " ]");
        showSoLuongDanhSachSP();

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

        //TODO: hiển thị tên cửa hàng


        // hiển thị products
        db.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Xóa danh sách cũ trước khi thêm dữ liệu mới
                adapter.getDsSanPham().clear();
                dsSanPham.clear();

                // Lặp qua tất cả các DataSnapshot con để lấy thông tin nhân viên
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy đối tượng NhanVien từ snapshot
                    Products product = new Products();
                    product = snapshot.getValue(Products.class);
                    if (product != null) {

                        // Lọc theo nhân viên của Chủ CH theo email
                        if (product.getIdChu().equals(cuahangID)) {
                            adapter.getDsSanPham().add(product);
                            dsSanPham.add(product); // Lưu vào danh sách gốc
                        }
                    } else
                        Snackbar.make(getView(), "Danh Sách Nhân Viên Rỗng", Toast.LENGTH_SHORT).show();
                }

                // Thông báo cho adapter cập nhật dữ liệu
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showSoLuongDanhSachSP() {
        soluongSP[0] = 0;

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("products");
        db.orderByChild("idChu").equalTo(cuahangID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    soluongSP[0] = snapshot.getChildrenCount();
                    binding.tvTonKho.setText(soluongSP[0] + " Sản Phẩm");
                } else {
                    Toast.makeText(getContext(), "Cửa hàng không có sản phẩm", Toast.LENGTH_SHORT).show();
                    binding.tvTonKho.setText("Cửa hàng không có sản phẩm");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(getView(), "Lỗi khi lấy dữ liệu sản phẩm!", Toast.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE).show();
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