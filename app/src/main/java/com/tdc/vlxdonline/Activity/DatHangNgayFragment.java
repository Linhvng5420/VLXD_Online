package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.ChiTietDon;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentDatHangNgayBinding;

public class DatHangNgayFragment extends Fragment {

    private FragmentDatHangNgayBinding binding;
    // Bien luu tam don hang
    private DonHang donHang;
    // Bien luu tam chi tiet don, vi dat ngay nen chi co 1 chi tiet
    private ChiTietDon chiTietDon;
    // Thong tin san pham
    private String idProd;
    private Products prod;
    // Khac
    private int soLuong;
    DatabaseReference referDatHangNgay;

    public DatHangNgayFragment(String idProduct, int soLuong) {
        this.idProd = idProduct;
        this.soLuong = soLuong;
        donHang = new DonHang();
        donHang.setIdKhach(Customer_HomeActivity.info.getID());
        chiTietDon = new ChiTietDon();
        chiTietDon.setIdSanPham(idProduct);
        chiTietDon.setIdDon(donHang.getId());
        chiTietDon.setSoLuong(soLuong);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDatHangNgayBinding.inflate(inflater, container, false);
        referDatHangNgay = FirebaseDatabase.getInstance().getReference();
        KhoiTao();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Su kien nhap du lieu
        binding.edtTenNguoiNhan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                donHang.setTenKhach(binding.edtTenNguoiNhan.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.edtSdtNguoiNhan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                donHang.setSdt(binding.edtSdtNguoiNhan.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.edtDiaChiNhan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                donHang.setDiaChi(binding.edtDiaChiNhan.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // Su Kien Tang Giam SL
        binding.btnGiamDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soLuong > 1) {
                    soLuong = soLuong - 1;
                    chiTietDon.setSoLuong(soLuong);
                    binding.edtSlDat.setText(soLuong + "");
                    int tong = Integer.parseInt(prod.getGia()) * soLuong;
                    donHang.setTongTien(tong);
                    binding.tvTongDatNgay.setText(chuyenChuoi(tong));
                }
            }
        });
        binding.btnTangDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soLuong = soLuong + 1;
                chiTietDon.setSoLuong(soLuong);
                binding.edtSlDat.setText(soLuong + "");
                int tong = Integer.parseInt(prod.getGia()) * soLuong;
                donHang.setTongTien(tong);
                binding.tvTongDatNgay.setText(chuyenChuoi(tong));
            }
        });
        // Su kien nhap so luong
        binding.edtSlDat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!binding.edtSlDat.getText().toString().isEmpty()) {
                    soLuong = Integer.parseInt(binding.edtSlDat.getText().toString());
                    chiTietDon.setSoLuong(soLuong);
                    int tong = Integer.parseInt(prod.getGia()) * soLuong;
                    donHang.setTongTien(tong);
                    binding.tvTongDatNgay.setText(chuyenChuoi(tong));
                }else{
                    binding.edtSlDat.setText("1");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // Su kien mua ngay
        binding.btnDatNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!donHang.getTenKhach().isEmpty() && !donHang.getSdt().isEmpty() && !donHang.getDiaChi().isEmpty()) {
                    addDataToFirebase();
                }else{
                    Toast.makeText(getActivity(), "Hãy Nhập Đủ Thông Tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Ham them dau cham cho gia ban
    private StringBuilder chuyenChuoi(int soTien) {
        StringBuilder chuoi = new StringBuilder(soTien + "");
        if (chuoi.length() > 3) {
            int dem = 0;
            int doDai = chuoi.length() - 1;
            for (int i = doDai; i > 0; i--) {
                dem = dem + 1;
                if (dem == 3) {
                    chuoi.insert(i, '.');
                    dem = 0;
                }
            }
        }
        return chuoi;
    }

    private void KhoiTao() {
        binding.edtTenNguoiNhan.setText(Customer_HomeActivity.info.getTen());
        binding.edtSdtNguoiNhan.setText(Customer_HomeActivity.info.getSdt());
        binding.edtDiaChiNhan.setText(Customer_HomeActivity.info.getDiaChi());
        donHang.setTenKhach(Customer_HomeActivity.info.getTen());
        donHang.setSdt(Customer_HomeActivity.info.getSdt());
        donHang.setDiaChi(Customer_HomeActivity.info.getDiaChi());
        referDatHangNgay.child("products").child(idProd).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Products product = dataSnapshot.getValue(Products.class);
                    if (product != null) {
                        prod = product;
                        chiTietDon.setTen(product.getTen());
                        chiTietDon.setAnh(product.getAnh());
                        chiTietDon.setGia(Integer.parseInt(product.getGia()));
                        donHang.setIdChu(product.getIdChu());
                        donHang.setAnh(product.getAnh());
                        donHang.setTongTien(Integer.parseInt(product.getGia()) * soLuong);
                        Glide.with(getActivity()).load(product.getAnh()).into(binding.imgDatHangNgay);
                        binding.tvNameDatHangNgay.setText(product.getTen());
                        binding.tvGiaDatHangNgay.setText(chuyenChuoi(Integer.parseInt(product.getGia())) + " đ");
                        binding.tvDesDatNgay.setText(product.getMoTa());
                        binding.edtSlDat.setText(soLuong + "");
                        binding.tvTongDatNgay.setText(chuyenChuoi(Integer.parseInt(product.getGia()) * soLuong));
                    } else {
                        Toast.makeText(getActivity(), "Sản Phẩm Đã Bị Xóa!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void addDataToFirebase(){
        referDatHangNgay.child("bills").child(donHang.getId()+"").setValue(donHang);
        referDatHangNgay.child("BillDetails").child(donHang.getId()+"").child(idProd).setValue(chiTietDon);
        Toast.makeText(getActivity(), "Hoàn Tất Đặt Hàng, Kiểm Tra Đơn Tại 'Đơn Hàng'", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}