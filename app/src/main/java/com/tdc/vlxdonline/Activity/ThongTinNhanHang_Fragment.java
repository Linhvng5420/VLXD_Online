package com.tdc.vlxdonline.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.ChiTietXuatAdapter;
import com.tdc.vlxdonline.Model.ChiTietDon;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.Model.DonNhap;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentTaoDonNhapHangBinding;
import com.tdc.vlxdonline.databinding.FragmentThongTinNhanHangBinding;

import java.util.ArrayList;


public class ThongTinNhanHang_Fragment extends Fragment {

    FragmentThongTinNhanHangBinding binding;
    DonHang donHang = new DonHang();
    ArrayList<ChiTietDon> dataChiTietDon = new ArrayList<>();
    ChiTietXuatAdapter chiTietXuatAdapter;
    //boolean taoDon = false;
    DatabaseReference referDatHang;
    KhachHang khachHang;

    public ThongTinNhanHang_Fragment(DonHang don, ArrayList<ChiTietDon> dataChiTietDon) {
        donHang = don;
        this.dataChiTietDon = dataChiTietDon;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentThongTinNhanHangBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        //if (taoDon) getActivity().getSupportFragmentManager().popBackStack();
        referDatHang = FirebaseDatabase.getInstance().getReference();
        setDuLieu();

        return binding.getRoot();
    }

    private void setDuLieu() {
        binding.tvTongTien.setText(donHang.getTongTien() + "");
        chiTietXuatAdapter = new ChiTietXuatAdapter(getActivity(), dataChiTietDon);
        binding.rcvDonHang.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.rcvDonHang.setAdapter(chiTietXuatAdapter);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenNguoiNhan = binding.edtTenNguoiNhan.getText().toString();
                String soDT = binding.edtNhapSoDTNguoiNhan.getText().toString();
                String diaChi = binding.edtNhapDiaChi.getText().toString();
                if (!tenNguoiNhan.equals("") && !soDT.equals("") && !diaChi.equals("") && khachHang != null) {
                    showConfirm();
                } else {
                    Toast.makeText(getActivity(), "Hãy nhập đủ thông tin !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.edtNhapSDT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tvThongTinKhach.setText("");
                referDatHang.child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!binding.edtNhapSDT.getText().toString().equals("")){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                KhachHang temp = snapshot.getValue(KhachHang.class);
                                temp.setID(snapshot.getKey());
                                if (temp.getSdt().contains(binding.edtNhapSDT.getText().toString())) {
                                    String chuoi = binding.tvThongTinKhach.getText().toString();
                                    binding.tvThongTinKhach.setText(chuoi + "SDT: " + temp.getSdt() + ", Tên: " + temp.getTen() + "\n");
                                    if (temp.getSdt().equals(binding.edtNhapSDT.getText().toString())) {
                                        khachHang = temp;
                                        binding.tvThongTinKhach.setText("Tên khách: " + temp.getTen() + "\nSố Điện Thoại: " + temp.getSdt()
                                                + "\nĐịa Chỉ: " + temp.getDiaChi());
                                        binding.edtTenNguoiNhan.setText(temp.getTen());
                                        binding.edtNhapSoDTNguoiNhan.setText(temp.getSdt());
                                        binding.edtNhapDiaChi.setText(temp.getDiaChi());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void TaoDonKhachHang() {
        donHang.setIdKhach(khachHang.getID());
        donHang.setAnh(dataChiTietDon.get(0).getAnh());
        donHang.setTenKhach(binding.edtTenNguoiNhan.getText().toString());
        donHang.setSdt(binding.edtNhapSoDTNguoiNhan.getText().toString());
        donHang.setDiaChi(binding.edtNhapDiaChi.getText().toString());
        referDatHang.child("bills").child(donHang.getId() + "").setValue(donHang);
        for (int i = 0; i < dataChiTietDon.size(); i++) {
            ChiTietDon tempChiTiet = dataChiTietDon.get(i);
            referDatHang.child("BillDetails").child(tempChiTiet.getIdDon() + "").child(tempChiTiet.getIdSanPham()).setValue(tempChiTiet);

            referDatHang.child("products").child(tempChiTiet.getIdSanPham()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Products product = snapshot.getValue(Products.class);
                    int tonKho = Integer.parseInt(product.getTonKho());
                    referDatHang.child("products").child(tempChiTiet.getIdSanPham()).child("tonKho").setValue((tonKho - tempChiTiet.getSoLuong()) + "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void showConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Xác nhận tạo đơn hàng ?") // Thông điệp bằng tiếng Việt
                .setCancelable(false)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TaoDonKhachHang();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel(); // Đóng hộp thoại
                    }
                });
        AlertDialog alert = builder.create();
        alert.show(); // Hiển thị hộp thoại
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}