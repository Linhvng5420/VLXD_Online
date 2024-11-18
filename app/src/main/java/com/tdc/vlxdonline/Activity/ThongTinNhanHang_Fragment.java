
package com.tdc.vlxdonline.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.ChiTietXuatAdapter;
import com.tdc.vlxdonline.Adapter.TTKhachHangAdapter;
import com.tdc.vlxdonline.Model.ChiTietDon;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.databinding.FragmentThongTinNhanHangBinding;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class ThongTinNhanHang_Fragment extends Fragment {

    FragmentThongTinNhanHangBinding binding;
    DonHang donHang = new DonHang();
    ArrayList<ChiTietDon> dataChiTietDon = new ArrayList<>();
    ChiTietXuatAdapter chiTietXuatAdapter;
    //boolean taoDon = false;
    ArrayList<KhachHang> khachHangList;
    TTKhachHangAdapter ttkhachHangAdapter;
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
        khachHangList = new ArrayList<>();
        ttkhachHangAdapter = new TTKhachHangAdapter(getActivity(), khachHangList);
        ttkhachHangAdapter.setOnItemInfoClick(new TTKhachHangAdapter.OnItemInfoClick() {
            @Override
            public void onItemClick(int position) {
                khachHang = khachHangList.get(position);
                binding.edtTenNguoiNhan.setText(khachHang.getTen());
                binding.edtNhapSoDTNguoiNhan.setText(khachHang.getSdt());
                binding.edtNhapDiaChi.setText(khachHang.getDiaChi());
                binding.edtNhapSDT.setText(khachHang.getSdt());
            }
        });
        binding.rcvThongTinKhach.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rcvThongTinKhach.setAdapter(ttkhachHangAdapter);

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
                String input = binding.edtNhapSDT.getText().toString();
                khachHangList.clear();

                if (!input.isEmpty()) {
                    binding.rcvThongTinKhach.setVisibility(View.VISIBLE);

                    referDatHang.child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                KhachHang temp = snapshot.getValue(KhachHang.class);
                                temp.setID(snapshot.getKey());
                                if (temp.getSdt().contains(input)) {
                                    khachHangList.add(temp);
                                }
                            }
                            ttkhachHangAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu cho RecyclerView
                            if (khachHangList.isEmpty()) {
                                Toast.makeText(getActivity(), "Không tìm thấy khách hàng.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    khachHang = null;
                    binding.edtTenNguoiNhan.setText("");
                    binding.edtNhapSoDTNguoiNhan.setText("");
                    binding.edtNhapDiaChi.setText("");
                    binding.rcvThongTinKhach.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
//        binding.edtNhapSDT.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                binding.tvThongTinKhach.setText("");
//                referDatHang.child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (!binding.edtNhapSDT.getText().toString().equals("")) {
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                KhachHang temp = snapshot.getValue(KhachHang.class);
//                                temp.setID(snapshot.getKey());
//                                if (temp.getSdt().contains(binding.edtNhapSDT.getText().toString())) {
//                                    String chuoi = binding.tvThongTinKhach.getText().toString();
//                                    binding.tvThongTinKhach.setText(chuoi + "SDT: " + temp.getSdt() + ", Tên: " + temp.getTen() + "\n");
//                                    if (temp.getSdt().equals(binding.edtNhapSDT.getText().toString())) {
//                                        khachHang = temp;
//                                        binding.tvThongTinKhach.setText("Tên khách: " + temp.getTen() + "\nSố Điện Thoại: " + temp.getSdt()
//                                                + "\nĐịa Chỉ: " + temp.getDiaChi());
//                                        binding.edtTenNguoiNhan.setText(temp.getTen());
//                                        binding.edtNhapSoDTNguoiNhan.setText(temp.getSdt());
//                                        binding.edtNhapDiaChi.setText(temp.getDiaChi());
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
    }


    private void TaoDonKhachHang() {
        donHang.setIdKhach(khachHang.getID());
        donHang.setAnh(dataChiTietDon.get(0).getAnh());
        donHang.setTenKhach(binding.edtTenNguoiNhan.getText().toString());
        donHang.setSdt(binding.edtNhapSoDTNguoiNhan.getText().toString());
        donHang.setDiaChi(binding.edtNhapDiaChi.getText().toString());
        referDatHang.child("bills").child(donHang.getId() + "").setValue(donHang);
        int itemCount = dataChiTietDon.size();
        AtomicInteger p = new AtomicInteger(0);
        for (int i = 0; i < dataChiTietDon.size(); i++) {
            dataChiTietDon.get(i).setIdDon(donHang.getId());
            ChiTietDon tempChiTiet = dataChiTietDon.get(i);
            DatabaseReference tempRefer = FirebaseDatabase.getInstance().getReference();
            tempRefer.child("BillDetails").child(tempChiTiet.getIdDon() + "").child(tempChiTiet.getIdSanPham()).setValue(tempChiTiet);

            tempRefer.child("products").child(tempChiTiet.getIdSanPham()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Products product = snapshot.getValue(Products.class);
                    int tonKho = Integer.parseInt(product.getTonKho());
                    tempRefer.child("products").child(tempChiTiet.getIdSanPham()).child("tonKho").setValue((tonKho - tempChiTiet.getSoLuong()) + "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (p.incrementAndGet() == itemCount)
                getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void showConfirm() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
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