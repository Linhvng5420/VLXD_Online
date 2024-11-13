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
import com.tdc.vlxdonline.Adapter.DonNhapAdapter;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.Model.DonNhap;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentGiaoDienDonHangBinding;

import java.util.ArrayList;

public class GiaoDienDonHang_Fragment extends Fragment {

    FragmentGiaoDienDonHangBinding binding;
    ArrayList<DonNhap> dataNhap = new ArrayList<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ArrayList<DonNhap> data = new ArrayList<>();
    DonNhapAdapter adapter;

    public GiaoDienDonHang_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGiaoDienDonHangBinding.inflate(inflater, container, false);
        setAdapterDon();
        docData();
        binding.btnAddDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi phương thức để chuyển sang Fragment khác
                ((Warehouse_HomeActivity)getActivity()).ReplaceFragment(new TaoDonNhapHangFragment());
            }
        });
        binding.btnLoadDH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return binding.getRoot();
    }

    private void docData(){
        reference.child("donNhap").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DonNhap don = snapshot.getValue(DonNhap.class);
                    String emailchu = Warehouse_HomeActivity.nhanVien.getEmailchu();
                    if (don.getIdChu().equals(emailchu)) data.add(don);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAdapterDon(){
        adapter = new DonNhapAdapter(data, getActivity());
        binding.rcvDonhang.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.rcvDonhang.setAdapter(adapter);
    }
}