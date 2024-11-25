package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentQuanLyTraGopBinding;

import java.util.ArrayList;

public class QuanLyTraGopFragment extends Fragment {
    FragmentQuanLyTraGopBinding binding;
    ArrayList<DonHang> data = new ArrayList<>();
    DonHangAdapter adapter;
    DatabaseReference reference;
    ValueEventListener event;
    String idKhach;
    String idChu;

    public QuanLyTraGopFragment(String idKhach, String idChu) {
        this.idKhach = idKhach;
        this.idChu = idChu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQuanLyTraGopBinding.inflate(inflater, container, false);
        reference = FirebaseDatabase.getInstance().getReference("bills");
        setAdapterDon();
        DocDataTraGop();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void DocDataTraGop(){
        if (event == null) {
            event = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        data.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DonHang temp = snapshot.getValue(DonHang.class);
                            if (idChu != null && !idChu.equals(temp.getIdChu())) continue;
                            if (temp.getIdKhach().equals(idKhach) && temp.getTrangThaiTT() == 1) {
                                data.add(temp);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }catch (Exception e){}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            reference.addValueEventListener(event);
        }
    }

    private void setAdapterDon(){
        adapter = new DonHangAdapter(getActivity(), data);
        adapter.setOnItemDonHangClick(new DonHangAdapter.OnItemDonHangClick() {
            @Override
            public void onItemClick(int position) {
                if (idChu != null) ((Owner_HomeActivity) getActivity()).ReplaceFragment(new ChiTietDonTraGopFragment(data.get(position).getId(), 0));
                else ((Customer_HomeActivity) getActivity()).ReplaceFragment(new ChiTietDonTraGopFragment(data.get(position).getId(), 1));
            }
        });
        binding.rcDonTraGop.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.rcDonTraGop.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        reference.removeEventListener(event);

        reference = null;
        event = null;
    }
}