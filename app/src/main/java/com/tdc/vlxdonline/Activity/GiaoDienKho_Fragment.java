package com.tdc.vlxdonline.Activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.CategoryAdapter;
import com.tdc.vlxdonline.Adapter.ProductAdapter;
import com.tdc.vlxdonline.Model.Categorys;
import com.tdc.vlxdonline.Model.ChiTietNhap;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentGiaoDienKhoBinding;

import java.util.ArrayList;

public class GiaoDienKho_Fragment extends Fragment {

    FragmentGiaoDienKhoBinding binding; // Binding cho layout của fragment
    ProductAdapter adapter; // Adapter cho danh sách sản phẩm
    ArrayList<ChiTietNhap> dsChiTiet = new ArrayList<>(); // Danh sách chi tiết nhập
    Products products = new Products(); // Thông tin sản phẩm hiện tại
    RecyclerView recyclerView; // RecyclerView cho sản phẩm
    ArrayList<Products> list = new ArrayList<>(); // Danh sách tất cả sản phẩm
    DatabaseReference reference; // Tham chiếu đến Firebase
    ValueEventListener listener; // Listener cho Firebase
    CategoryAdapter adapterCate; // Adapter cho danh mục
    ArrayList<Categorys> dsCategory = new ArrayList<>(); // Danh sách danh mục
    ArrayList<Products> dsSanPham = new ArrayList<>(); // Danh sách sản phẩm hiện tại
    String category = ""; // Danh mục hiện tại
    View preView = null; // View trước đó được chọn
    ValueEventListener eventDocDanhSach; // Listener cho danh sách sản phẩm
    private String tuKhoa = ""; // Từ khóa tìm kiếm
    private String emailChu; // Biến dùng để kiểm tra có đúng id chủ của nhân viên hay không

    public GiaoDienKho_Fragment(String emailChu) {
        this.emailChu = emailChu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tuKhoa = ""; // Khởi tạo từ khóa tìm kiếm
        category = ""; // Khởi tạo danh mục
        preView = null; // Khởi tạo view trước đó

        reference = FirebaseDatabase.getInstance().getReference(); // Khởi tạo tham chiếu đến Firebase
        setHienThiSanPham(); // Gọi phương thức hiển thị sản phẩm
        readcategorysFromDatabase(); // Đọc danh mục từ Firebase

        // Thêm sự kiện tìm kiếm cho SearchView
        binding.svCustomerHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tuKhoa = query; // Gán từ khóa tìm kiếm
                reference.child("products").addListenerForSingleValueEvent(eventDocDanhSach); // Tải lại danh sách sản phẩm
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    tuKhoa = ""; // Xóa từ khóa khi không có văn bản nhập
                    reference.child("products").addListenerForSingleValueEvent(eventDocDanhSach); // Tải lại danh sách sản phẩm
                }
                return false;
            }
        });
    }

    // Phương thức để thiết lập hiển thị sản phẩm
    private void setHienThiSanPham() {
        eventDocDanhSach = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dsSanPham.clear(); // Xóa danh sách sản phẩm cũ trước khi cập nhật

                    // Duyệt qua từng sản phẩm trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Products product = snapshot.getValue(Products.class);
                        product.setId(snapshot.getKey());
                        // Kiem tra id chu
                        if (!product.getIdChu().equals(emailChu.substring(0, emailChu.indexOf("@")))) continue;
                        // Kiểm tra danh mục và từ khóa tìm kiếm
                        if (!category.isEmpty() && !category.equals(product.getDanhMuc())) continue;
                        if (!tuKhoa.isEmpty() && !product.getTen().contains(tuKhoa) && !product.getMoTa().contains(tuKhoa))
                            continue;
                        dsSanPham.add(product); // Thêm sản phẩm vào danh sách
                    }

                    // Khởi tạo adapter cho sản phẩm
                    adapterCate = new CategoryAdapter(getActivity(), dsCategory);
                    adapter = new ProductAdapter(getActivity(), dsSanPham, View.GONE);

                    // Xử lý sự kiện khi sản phẩm được nhấp
                    adapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
                        @Override
                        public void OnItemClick(View view, int position) {
                            products = dsSanPham.get(position); // Lấy sản phẩm được chọn
                            ((Warehouse_HomeActivity) getActivity()).ReplaceFragment(new ChiTietSPKho_Fragment(products.getId())); // Chuyển đến fragment chi tiết sản phẩm
                        }

                        @Override
                        public void OnBtnBuyClick(View view, int position) {
                            // Xử lý sự kiện khi nút mua được nhấp
                        }
                    });

                    binding.rcvSanpham1.setLayoutManager(new GridLayoutManager(getActivity(), 2

                    )); // Thiết lập layout cho RecyclerView
                    binding.rcvSanpham1.setAdapter(adapter); // Gán adapter cho RecyclerView
                }catch (Exception e){
                    Log.e("Lỗi", "Lỗi khi thay đổi giá: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show(); // Thông báo lỗi
            }
        };

        reference.child("products").addValueEventListener(eventDocDanhSach); // Lắng nghe thay đổi danh sách sản phẩm
    }

    // Phương thức để đọc danh mục từ Firebase
    private void readcategorysFromDatabase() {
        reference.child("categorys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dsCategory.clear(); // Xóa danh sách danh mục cũ trước khi cập nhật

                    // Duyệt qua từng danh mục trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Categorys category = snapshot.getValue(Categorys.class);
                        dsCategory.add(category); // Thêm danh mục vào danh sách
                    }

                    // Khởi tạo adapter cho danh mục
                    adapterCate = new CategoryAdapter(getActivity(), dsCategory);
                    // Xử lý sự kiện khi danh mục được nhấp
                    adapterCate.setOnItemCategoryClickListener(new CategoryAdapter.OnItemCategoryClickListener() {
                        @Override
                        public void OnItemClick(View view, int position) {
                            if (category.equals(dsCategory.get(position).getId())) {
                                category = ""; // Nếu danh mục đã được chọn, bỏ chọn
                                view.setBackgroundColor(Color.TRANSPARENT); // Đặt màu nền về trong suốt
                                preView = null; // Đặt view trước đó về null
                            } else {
                                category = dsCategory.get(position).getId(); // Cập nhật danh mục hiện tại
                                Drawable drawable = getActivity().getDrawable(R.drawable.bg_detail); // Lấy hình nền
                                view.setBackground(drawable); // Thiết lập màu nền cho view hiện tại
                                if (preView != null)
                                    preView.setBackgroundColor(Color.TRANSPARENT); // Đặt màu nền view trước đó về trong suốt
                                preView = view; // Cập nhật view trước đó
                            }
                            reference.child("products").addListenerForSingleValueEvent(eventDocDanhSach); // Tải lại danh sách sản phẩm
                        }
                    });

                    // Thiết lập layout cho RecyclerView của danh mục
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL); // Thiết lập hướng ngang cho danh sách danh mục
                    binding.rcvCategory.setLayoutManager(linearLayoutManager);
                    binding.rcvCategory.setAdapter(adapterCate); // Gán adapter cho RecyclerView danh mục
                } catch (Exception e) {
                    Log.e("Lỗi", "Lỗi khi đọc danh mục: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show(); // Thông báo lỗi
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGiaoDienKhoBinding.inflate(inflater, container, false); // Tạo binding cho fragment
        // Inflate the layout cho fragment này
        return binding.getRoot(); // Trả về root view
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        if (eventDocDanhSach != null) {
            reference.removeEventListener(eventDocDanhSach);
        }
        eventDocDanhSach = null;
        reference = null;
    }
}
