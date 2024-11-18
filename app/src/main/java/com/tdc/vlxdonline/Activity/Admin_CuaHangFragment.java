package com.tdc.vlxdonline.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.CuaHangAdapter;
import com.tdc.vlxdonline.Model.ThongTinChu;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentAdminCuahangBinding;

import java.util.ArrayList;
import java.util.List;

public class Admin_CuaHangFragment extends Fragment {
    FragmentAdminCuahangBinding binding;
    String LoginEmailID = LoginActivity.accountID;
    String LoginEmail = LoginActivity.idUser;
    List<ThongTinChu> listChuCuaHang;
    CuaHangAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listChuCuaHang = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminCuahangBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Adapter - Recycleview
        binding.ownerRcvNhanVien.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CuaHangAdapter(listChuCuaHang);
        binding.ownerRcvNhanVien.setAdapter(adapter);

        // Get Data Firebase listChuCuaHang
        getDataCuaHang();
        setOnClickItemRecycleView();
        //
        return view;
    }

    private void setOnClickItemRecycleView() {
        adapter.setOnItemClickListener(new CuaHangAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ThongTinChu cuahang) {
                Bundle bundleIDKhachHang = new Bundle();
                bundleIDKhachHang.putSerializable("idCH", cuahang.getId());
                Owner_KhachHangDetailFragment khachHangDetailFragment = new Owner_KhachHangDetailFragment();
                khachHangDetailFragment.setArguments(bundleIDKhachHang);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, khachHangDetailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void getDataCuaHang() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("thongtinchu");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listChuCuaHang.clear();
                adapter.getCuaHangList().clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ThongTinChu thongTinChu = new ThongTinChu();
                    thongTinChu = dataSnapshot.getValue(ThongTinChu.class);

                    if (thongTinChu != null) {
                        thongTinChu.setId(dataSnapshot.getKey());
                        listChuCuaHang.add(thongTinChu);
                    } else {
                        binding.tvTitle.setText("Danh Sách Cửa Hàng Rỗng");
                        Log.d("l.d", "getDataCuaHang: null");
                    }
                }
                Log.d("l.d", "getDataCuaHang: " + listChuCuaHang.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}