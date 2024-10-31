package com.tdc.vlxdonline.Activity;

// Các import cần thiết
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
    FragmentTaoDonNhapHangBinding binding; // Binding để kết nối với layout
    ArrayList<Products> dsSanPham = new ArrayList<>(); // Danh sách sản phẩm
    ProductAdapter adapter; // Adapter cho danh sách sản phẩm
    DonHangAdapter donHangAdapter; // Adapter cho đơn hàng
    ArrayList<ChiTietNhap> dsChiTiet = new ArrayList<>(); // Danh sách chi tiết nhập
    ChiTietNhap temp; // Thông tin chi tiết nhập tạm thời
    Products products = new Products(); // Sản phẩm hiện tại
    DatabaseReference reference; // Tham chiếu đến cơ sở dữ liệu Firebase
    ValueEventListener eventDocDanhSach; // Sự kiện để lắng nghe thay đổi trong cơ sở dữ liệu
    private String tuKhoa = ""; // Từ khóa tìm kiếm
    String category = ""; // Danh mục sản phẩm
    View preView = null; // View trước đó để quản lý giao diện
    int SoLuong = 0; // Số lượng sản phẩm
    DonNhap donNhap = new DonNhap(); // Đơn nhập hiện tại

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
        reference = FirebaseDatabase.getInstance().getReference(); // Khởi tạo tham chiếu đến Firebase
        setHienThiSanPham(); // Thiết lập hiển thị sản phẩm

        donHangAdapter = new DonHangAdapter(getActivity(), dsChiTiet); // Khởi tạo adapter cho chi tiết đơn hàng
        binding.rcvChitiet.setLayoutManager(new LinearLayoutManager(getActivity())); // Thiết lập layout cho RecyclerView
        binding.rcvChitiet.setAdapter(donHangAdapter); // Gán adapter vào RecyclerView

        // Thêm sự kiện tìm kiếm cho SearchView
        binding.svDonhang.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tuKhoa = query; // Cập nhật từ khóa tìm kiếm
                reference.child("products").addListenerForSingleValueEvent(eventDocDanhSach); // Lắng nghe sự kiện cho sản phẩm
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    tuKhoa = ""; // Nếu không có văn bản, làm trống từ khóa
                    reference.child("products").addListenerForSingleValueEvent(eventDocDanhSach); // Lắng nghe lại
                }
                return false;
            }
        });

        // Theo dõi sự thay đổi số lượng nhập
        binding.edtSoLuong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String chuoi = binding.edtSoLuong.getText().toString();
                if (!chuoi.isEmpty()) {
                    SoLuong = Integer.parseInt(chuoi); // Cập nhật số lượng từ input
                } else {
                    SoLuong = 1; // Mặc định số lượng là 1
                    binding.edtSoLuong.setText("1");
                }
                temp.setSoLuong(SoLuong); // Cập nhật số lượng vào đối tượng tạm
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Theo dõi sự thay đổi giá nhập
        binding.edtGia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String chuoi = binding.edtGia.getText().toString();
                if (chuoi.isEmpty()) {
                    binding.edtGia.setText("1"); // Mặc định giá là 1
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Thêm sản phẩm vào đơn nhập
        binding.btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sl = binding.edtSoLuong.getText().toString();
                String gia = binding.edtGia.getText().toString();
                if (!sl.isEmpty() && !gia.isEmpty() && temp.getTen() != null) {
                    temp.setSoLuong(Integer.parseInt(sl)); // Cập nhật số lượng
                    temp.setGia(Integer.parseInt(gia)); // Cập nhật giá
                    dsChiTiet.add(temp); // Thêm vào danh sách chi tiết nhập
                    donNhap.setTongTien(donNhap.getTongTien() + (temp.getSoLuong() * temp.getGia())); // Cập nhật tổng tiền
                    temp = new ChiTietNhap(donNhap.getId()); // Tạo đối tượng chi tiết nhập mới
                    donHangAdapter.notifyDataSetChanged(); // Cập nhật adapter
                } else {
                    Toast.makeText(getActivity(), "Hãy chọn sản phẩm và nhâp đủ thông tin !!!", Toast.LENGTH_SHORT).show(); // Thông báo lỗi
                }
            }
        });

        // Quay lại fragment trước đó
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }

    // Phương thức để tải lên đơn nhập
    private void upLoad() {
        if (dsChiTiet.size() > 0) { // Kiểm tra danh sách chi tiết nhập
            reference.child("donnhap").child(donNhap.getId() + "").setValue(donNhap); // Lưu đơn nhập vào Firebase
            for (int i = 0; i < dsChiTiet.size(); i++) {
                reference.child("chiTietNhap").child(donNhap.getId() + "").child(dsChiTiet.get(i).getIdSanPham()).setValue(dsChiTiet.get(i)); // Lưu chi tiết nhập
            }
            Toast.makeText(getActivity(), "Tạo đơn nhập kho thành công", Toast.LENGTH_SHORT).show(); // Thông báo thành công
            donNhap = new DonNhap(); // Tạo đơn nhập mới
            temp = new ChiTietNhap(donNhap.getId()); // Tạo đối tượng chi tiết nhập mới
            dsChiTiet.clear(); // Xóa danh sách chi tiết nhập
            donHangAdapter.notifyDataSetChanged(); // Cập nhật adapter
        } else {
            Toast.makeText(getActivity(), "Chưa có thông tin nhập hàng", Toast.LENGTH_SHORT).show(); // Thông báo chưa có thông tin
        }
    }

    // Phương thức thiết lập hiển thị sản phẩm
    private void setHienThiSanPham() {
        temp = new ChiTietNhap(donNhap.getId()); // Khởi tạo chi tiết nhập
        eventDocDanhSach = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dsSanPham.clear(); // Xóa danh sách cũ
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Products product = snapshot.getValue(Products.class); // Lấy sản phẩm từ snapshot
                    if (!category.isEmpty() && !category.equals(product.getDanhMuc())) continue; // Kiểm tra danh mục
                    if (!tuKhoa.isEmpty() && !product.getTen().contains(tuKhoa) && !product.getMoTa().contains(tuKhoa)) continue; // Kiểm tra từ khóa
                    dsSanPham.add(product); // Thêm sản phẩm vào danh sách
                }

                adapter = new ProductAdapter(getActivity(), dsSanPham, View.GONE); // Khởi tạo adapter cho sản phẩm

                // Sự kiện khi nhấn vào sản phẩm
                adapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        products = dsSanPham.get(position); // Lấy sản phẩm đã chọn
                        Toast.makeText(getActivity(), "Bạn đã chọn sản phẩm " + products.getTen(), Toast.LENGTH_SHORT).show(); // Thông báo sản phẩm đã chọn
                        temp.setAnh(products.getAnh()); // Cập nhật ảnh
                        temp.setTen(products.getTen()); // Cập nhật tên
                        temp.setIdSanPham(products.getId()); // Cập nhật ID sản phẩm
                        view.setBackgroundColor(Color.rgb(0, 255, 255)); // Đổi màu nền cho sản phẩm đã chọn
                    }

                    @Override
                    public void OnBtnBuyClick(View view, int position) {
                        // Có thể thêm logic cho nút mua nếu cần
                    }
                });

                binding.rcvSanpham.setLayoutManager(new GridLayoutManager(getActivity(), 2)); // Thiết lập layout cho RecyclerView sản phẩm
                binding.rcvSanpham.setAdapter(adapter); // Gán adapter vào RecyclerView
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show(); // Thông báo lỗi
            }
        };

        reference.child("products").addValueEventListener(eventDocDanhSach); // Lắng nghe thay đổi trong danh sách sản phẩm
    }

    // Phương thức để ẩn bàn phím
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaoDonNhapHangBinding.inflate(inflater, container, false); // Khởi tạo binding cho fragment

        // Thiết lập sự kiện touch để ẩn bàn phím khi chạm ra ngoài
        binding.getRoot().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View currentFocus = getActivity().getCurrentFocus();
                    if (currentFocus != null) {
                        hideKeyboard(currentFocus); // Ẩn bàn phím
                    }
                }
                return false; // Trả về false để cho phép xử lý sự kiện tiếp theo
            }
        });

        // Xác nhận tạo đơn nhập
        binding.btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoad(); // Gọi phương thức tải lên đơn nhập
            }
        });
        return binding.getRoot(); // Trả về view của fragment
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng binding khi view bị hủy
    }
}
