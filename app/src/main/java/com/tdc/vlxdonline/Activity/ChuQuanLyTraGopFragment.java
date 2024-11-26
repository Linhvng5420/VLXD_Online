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
import com.tdc.vlxdonline.Adapter.AdapterConNo;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentChuQuanLyTraGopBinding;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ChuQuanLyTraGopFragment extends Fragment {
    FragmentChuQuanLyTraGopBinding binding;
    DatabaseReference reference;
    ArrayList<KhachHang> dataNo = new ArrayList<>();
    AdapterConNo adapterConNo;
    ValueEventListener event;
    String idChu;

    public ChuQuanLyTraGopFragment(String idChu) {
        this.idChu = idChu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChuQuanLyTraGopBinding.inflate(inflater, container, false);
        reference = FirebaseDatabase.getInstance().getReference();
        setAdapterConNo();
        DocDanhSachNo();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void DocDanhSachNo(){
        if (event == null) {
            event = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataNo.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DonHang temp = snapshot.getValue(DonHang.class);
                        if (temp.getIdChu().equals(idChu) && temp.getTrangThaiTT() == 1) {
                            DatabaseReference referKH = FirebaseDatabase.getInstance().getReference("customers").child(temp.getIdKhach());
                            referKH.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    KhachHang tempKH = snapshot.getValue(KhachHang.class);
                                    if (tempKH != null) {
                                        tempKH.setID(snapshot.getKey());
                                        boolean checkE = false;
                                        for (int i = 0; i < dataNo.size(); i++) {
                                            if (dataNo.get(i).getID().equals(tempKH.getID())) {
                                                checkE = true;
                                            }
                                        }
                                        if (!checkE) {
                                            dataNo.add(tempKH);
                                            adapterConNo.notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            reference.child("bills").addValueEventListener(event);
        }
    }

    private void setAdapterConNo(){
        adapterConNo = new AdapterConNo(getActivity(), dataNo);
        adapterConNo.setOnItemConNoClick(new AdapterConNo.OnItemConNoClick() {
            @Override
            public void onItemClick(String idKhach) {
                ((Owner_HomeActivity) getActivity()).ReplaceFragment(new QuanLyTraGopFragment(idKhach, idChu));
            }
        });
        binding.rcKhachTraGop.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.rcKhachTraGop.setAdapter(adapterConNo);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        reference.child("bills").removeEventListener(event);

        event = null;
        reference = null;
    }
}