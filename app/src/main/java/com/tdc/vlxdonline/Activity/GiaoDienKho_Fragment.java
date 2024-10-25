package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");

        // Đọc dữ liệu từ nhánh "products"
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Duyệt qua từng sản phẩm trong nhánh "products"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Chuyển đổi dữ liệu từ Firebase thành đối tượng Products
                    Products product = snapshot.getValue(Products.class);
//                    dsSanPham.add......
                    // In thông tin sản phẩm ra Log hoặc xử lý theo ý muốn
                    Log.d("l.d", "Product: " + product.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu xảy ra
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });



        // Đọc dữ liệu từ Firebase
        // Khởi tạo danh sách sản phẩm
//        readProductsFromFirebase();

        getData();
        setEvent();
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

    private void setEvent() {
//        dsCategory.add(new Categorys("Thép","1","https://th.bing.com/th/id/OIP.UWORqopZEI954B5G-Z4sbgHaHQ?w=169&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7"));
//        dsCategory.add(new Categorys("Thép","1","https://th.bing.com/th/id/OIP.UWORqopZEI954B5G-Z4sbgHaHQ?w=169&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7"));
//        dsCategory.add(new Categorys("Thép","1","https://th.bing.com/th/id/OIP.UWORqopZEI954B5G-Z4sbgHaHQ?w=169&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7"));
//
//        dsSanPham.add(new Products("A", "A", "A", "1", "1", "https://th.bing.com/th/id/OIP.UWORqopZEI954B5G-Z4sbgHaHQ?w=169&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7", "100000", "0.0", "1", "1000", "580"));
//        dsSanPham.add(new Products("B", "B", "B", "1", "2", "https://th.bing.com/th/id/OIP.BO1VNjeGOUGcGRWQNUVCZQHaHa?w=1024&h=1024&rs=1&pid=ImgDetMain", "200000", "4.0", "1", "1000", "580"));
//        dsSanPham.add(new Products("C", "C", "C", "1", "3", "https://th.bing.com/th/id/OIP.vyMrfzra1TPcklie3-GA9gHaH9?w=180&h=183&c=7&r=0&o=5&dpr=1.3&pid=1.7", "300000", "5.0", "1", "1000", "580"));
//        dsSanPham.add(new Products("D", "D", "D", "1", "4", "https://th.bing.com/th?id=OIF.EGFQW6bdgdgP%2fL6l2yvVChg&rs=1&pid=ImgDetMain", "400000", "3.5", "1", "1000", "580"));


//        adapterCate = new CategoryAdapter(getActivity(), dsCategory);


//        adapterCate.setOnItemCategoryClickListener(new CategoryAdapter.OnItemCategoryClickListener() {
//            @Override
//            public void OnItemClick(View view, int position) {
//                // Lấy ID của danh mục
//                category = Integer.parseInt(dsCategory.get(position).getId());
//
//                // Hiển thị thông báo
//                Toast.makeText(getActivity(), "Da chon san pham " + dsCategory.get(position).getTen(), Toast.LENGTH_SHORT).show();
//            }
//        });
        adapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                products = dsSanPham.get(position);

                Toast.makeText(getActivity(), "Da chon san pham " + products.getTen(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnBtnBuyClick(View view, int position) {

            }
        });


    }

    private void getData() {

        adapter = new ProductAdapter(getActivity(), dsSanPham, View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rcvCategory.setLayoutManager(linearLayoutManager);
        binding.rcvCategory.setAdapter(adapterCate);
        binding.rcvSanpham1.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binding.rcvSanpham1.setAdapter(adapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");

        // Đọc dữ liệu từ nhánh "products"
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Duyệt qua từng sản phẩm trong nhánh "products"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Chuyển đổi dữ liệu từ Firebase thành đối tượng Products
                    Products product = snapshot.getValue(Products.class);
                    dsSanPham.add(product);
                    // In thông tin sản phẩm ra Log hoặc xử lý theo ý muốn
                    Log.d("l.d", "getData: Product: " + product.toString());
                    Log.d("l.d", "getData: List Product: " + dsSanPham.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu xảy ra
                Log.w("Firebase", "Failed to read value.", error.toException());
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