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
        // Su Kien Tang Giam SL
        binding.btnGiamDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int so = Integer.parseInt(binding.edtSlDat.getText().toString());
                if (so > 1) {
                    soLuong = so - 1;
                    binding.edtSlDat.setText((so - 1) + "");
                    int tong = Integer.parseInt(prod.getGia()) * soLuong;
                    binding.tvTongDatNgay.setText(chuyenChuoi(tong));
                }
            }
        });
        binding.btnTangDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int so = Integer.parseInt(binding.edtSlDat.getText().toString());
                soLuong = so + 1;
                binding.edtSlDat.setText((so + 1) + "");
                int tong = Integer.parseInt(prod.getGia()) * soLuong;
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
                soLuong = Integer.parseInt(binding.edtSlDat.getText().toString());
                int tong = Integer.parseInt(prod.getGia()) * soLuong;
                binding.tvTongDatNgay.setText(chuyenChuoi(tong));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // Su kien mua ngay
        binding.btnDatNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), prod.getGia(), Toast.LENGTH_SHORT).show();
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
        referDatHangNgay.child("products").child(idProd).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Products product = dataSnapshot.getValue(Products.class);
                    if (product != null) {
                        prod = product;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}