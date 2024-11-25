package com.tdc.vlxdonline.Activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.DonHangAdapter;
import com.tdc.vlxdonline.Model.ChiTietDon;
import com.tdc.vlxdonline.Model.DanhGia;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentDonChuaDanhGiaBinding;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DonChuaDanhGiaFragment extends Fragment {
    FragmentDonChuaDanhGiaBinding binding;
    String idKH;
    ArrayList<DonHang> data = new ArrayList<>();
    DonHangAdapter adapter;
    DatabaseReference reference;

    public DonChuaDanhGiaFragment(String idKH) {
        this.idKH = idKH;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDonChuaDanhGiaBinding.inflate(inflater, container, false);
        reference = FirebaseDatabase.getInstance().getReference();
        setAdapterDon();
        docDanhSach();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void docDanhSach() {
        reference.child("bills").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DonHang temp = snapshot.getValue(DonHang.class);
                    if (temp.getIdKhach().equals(idKH) && temp.getTrangThai() == 4 && temp.getTrangThaiTT() == 2 && !temp.isDaDanhGia()) {
                        data.add(temp);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAdapterDon() {
        adapter = new DonHangAdapter(getActivity(), data);
        adapter.setOnItemDonHangClick(new DonHangAdapter.OnItemDonHangClick() {
            @Override
            public void onItemClick(int position) {
                ChuyenManHinhDanhGia(data.get(position).getId());
            }
        });
        binding.rcChuaDg.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.rcChuaDg.setAdapter(adapter);
    }

    private void ChuyenManHinhDanhGia(long idDon) {
        ArrayList<DanhGia> temp = new ArrayList<>();
        reference.child("BillDetails").child(idDon + "").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int itemCount = (int) dataSnapshot.getChildrenCount();
                AtomicInteger count = new AtomicInteger(0);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChiTietDon chiTiet = snapshot.getValue(ChiTietDon.class);
                    reference.child("products").child(chiTiet.getIdSanPham()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Products tempP = snapshot.getValue(Products.class);
                            if (tempP != null) {
                                DanhGia tempDg = new DanhGia(idKH, chiTiet.getIdSanPham(), chiTiet.getAnh(), chiTiet.getTen(), "", 0, idDon);
                                temp.add(tempDg);
                            }
                            if (count.incrementAndGet() == itemCount) ChuyenTrang(idDon, temp);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ChuyenTrang(long idDon, ArrayList<DanhGia> danhSach){
        if (danhSach.size() > 0) ((Customer_HomeActivity) getActivity()).ReplaceFragment(new DanhGiaDonHangFragment(idKH, idDon, danhSach));
        else {
            ShowWar();
            reference.child("bills").child(idDon + "").child("daDanhGia").setValue(true);
        }
    }

    private void ShowWar() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Thông Báo!").setMessage("Các Sản Phẩm Thuộc Đơn Bạn Chọn Đã Bị Xóa!\nVui Lòng Chọn Đơn Khác Để Đánh Giá!");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Drawable drawableIcon = getResources().getDrawable(android.R.drawable.ic_delete);
        drawableIcon.setTint(Color.RED);
        builder.setIcon(drawableIcon);
        Drawable drawableBg = getResources().getDrawable(R.drawable.bg_item_lg);
        drawableBg.setTint(Color.rgb(100, 220, 255));
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(drawableBg);
        alertDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}