package com.tdc.vlxdonline.Activity;

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
    int category = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Test Firebase
        // Tạo tham chiếu đến Realtime Database
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");
//
//        // Đọc dữ liệu từ nhánh "products"
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Duyệt qua từng sản phẩm trong nhánh "products"
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    // Chuyển đổi dữ liệu từ Firebase thành đối tượng Products
//                    Products product = snapshot.getValue(Products.class);
////                    dsSanPham.add......
//                    // In thông tin sản phẩm ra Log hoặc xử lý theo ý muốn
//                    Log.d("l.d", "Product: " + product.toString());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Xử lý lỗi nếu xảy ra
//                Log.w("Firebase", "Failed to read value.", error.toException());
//            }
//        });


        // Đọc dữ liệu từ Firebase
        // Khởi tạo danh sách sản phẩm
//        readProductsFromFirebase();
        reference = FirebaseDatabase.getInstance().getReference();
        setHienThiSanPham();
        readcategorysFromDatabase();
    }

//    private void readProductsFromFirebase() {
//        // Tham chiếu đến Realtime Database nhánh "products"
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");
//
//        // Lắng nghe thay đổi từ Firebase Realtime Database
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                dsSanPham.clear(); // Xóa dữ liệu cũ (nếu có)
//
//                // Duyệt qua tất cả các sản phẩm
//                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
//                    // Chuyển đổi dữ liệu Firebase thành đối tượng Products
//                    Products product = productSnapshot.getValue(Products.class);
//                    dsSanPham.add(product); // Thêm sản phẩm vào danh sách
//
//                    // In thông tin sản phẩm ra Log hoặc xử lý theo ý muốn
//                    Log.d("l.d", "getData: Product: " + product.toString());
//                    Log.d("l.d", "getData: List Product: " + dsSanPham.toString());
//
//                }
//
//                // Cập nhật adapter để hiển thị dữ liệu
////                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Xử lý lỗi khi đọc dữ liệu từ Firebase
//            }
//        });
//    }


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
                adapterCate = new CategoryAdapter(getActivity(), dsCategory);
                adapter = new ProductAdapter(getActivity(), dsSanPham, View.GONE);


                adapterCate.setOnItemCategoryClickListener(new CategoryAdapter.OnItemCategoryClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        // Lấy ID của danh mục
                        category = Integer.parseInt(dsCategory.get(position).getId());

                        // Hiển thị thông báo
                        Toast.makeText(getActivity(), "Da chon san pham " + dsCategory.get(position).getTen(), Toast.LENGTH_SHORT).show();
                    }
                });

                adapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        products = dsSanPham.get(position);
                        ((Warehouse_HomeActivity)getActivity()).ReplaceFragment(new ChiTietSPKho_Fragment(products));

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
        });
    }

    private void readcategorysFromDatabase() {
        reference.child("categorys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                        category = Integer.parseInt(dsCategory.get(position).getId());
                        setHienThiSanPham();
                    }
                });
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                binding.rcvCategory.setLayoutManager(linearLayoutManager);
                binding.rcvCategory.setAdapter(adapterCate);
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