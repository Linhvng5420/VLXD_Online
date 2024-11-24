package com.tdc.vlxdonline.Activity;

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
import com.tdc.vlxdonline.Adapter.DanhGiaAdapter;
import com.tdc.vlxdonline.Model.DanhGia;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentDaDanhGiaBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DaDanhGiaFragment extends Fragment {
    FragmentDaDanhGiaBinding binding;
    // Type màn hình 0 là khách, 1 là sản phẩm
    private int type;
    // id truy cập, id khách hoặc id sản phẩm
    String id;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    // data danh gia
    ArrayList<DanhGia> data = new ArrayList<>();
    DanhGiaAdapter adapter;

    public DaDanhGiaFragment(int type, String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDaDanhGiaBinding.inflate(inflater, container, false);
        setAdapterDG();
        DocDataDanhSach();
        if (type == 1) binding.tvTitleDg.setText("DANH SÁCH ĐÁNH GIÁ");
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void DocDataDanhSach(){
        String bang = "customers";
        if (type == 0) {
            bang = "products";
        }
        final String fBang = bang;
        reference.child("danhgia").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Dong bo thoi gian de khong loi tinh toan
                    int itemCount = (int) snapshot.getChildrenCount();
                    AtomicInteger count = new AtomicInteger(0);
                    for (DataSnapshot miniDataSnapshot : snapshot.getChildren()) {
                        DanhGia tempDG = miniDataSnapshot.getValue(DanhGia.class);
                        String idRead = tempDG.getIdKhach();
                        if (type == 0) {
                            idRead = tempDG.getIdSp();
                        }
                        DatabaseReference tempRefer = FirebaseDatabase.getInstance().getReference();
                        tempRefer.child(fBang).child(idRead).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (type == 0) {
                                    Products p = snapshot.getValue(Products.class);
                                    if (p != null) {
                                        tempDG.setAnh(p.getAnh());
                                        tempDG.setTen(p.getTen());
                                    }
                                } else {
                                    KhachHang k = snapshot.getValue(KhachHang.class);
                                    if (k != null) {
                                        tempDG.setAnh(k.getAvata());
                                        tempDG.setTen(k.getTen());
                                    }
                                }
                                if (count.incrementAndGet() == itemCount) {
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        data.add(tempDG);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAdapterDG(){
        adapter = new DanhGiaAdapter(getActivity(), data, 0);
        adapter.setOnItemDanhGiaClick(new DanhGiaAdapter.OnItemDanhGiaClick() {
            @Override
            public void onItemClick(int position) {
                ((Customer_HomeActivity) getActivity()).ReplaceFragment(new ProdDetailCustomerFragment(data.get(position).getIdSp()));
            }

            @Override
            public void onStartClick(int position, int soSao) {

            }

            @Override
            public void onChangeDescrip(int position, String chuoi) {

            }
        });
        binding.rcDaDg.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.rcDaDg.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}