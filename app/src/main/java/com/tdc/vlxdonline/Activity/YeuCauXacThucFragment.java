package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.ThongTinChu;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentYeuCauXacThucBinding;

import java.util.ArrayList;

public class YeuCauXacThucFragment extends Fragment {

    FragmentYeuCauXacThucBinding binding;
    ArrayList<ThongTinChu> dataChu = new ArrayList<>();
    ArrayList<String> dataHienThi = new ArrayList<>();
    ArrayAdapter<String> adapter;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ValueEventListener eventListener;
    String tuKhoa = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentYeuCauXacThucBinding.inflate(inflater, container, false);
        setAdapterHT();
        DocThongTin();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.svYeuCau.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tuKhoa = query;
                LocThongTin();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tuKhoa = newText;
                LocThongTin();
                return false;
            }
        });
    }

    private void setAdapterHT(){
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataHienThi);
        binding.lvDSChu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void DocThongTin(){
        if (eventListener == null){
            eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataChu.clear();
                    dataHienThi.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ThongTinChu tt = snapshot.getValue(ThongTinChu.class);
                        if (tuKhoa.equals("") || tt.getTen().contains(tuKhoa) || tt.getEmail().contains(tuKhoa)) {
                            dataChu.add(tt);
                            dataHienThi.add("Tên Chủ: " + tt.getTen() + "\n" + "Email: " + tt.getEmail() + "\n" + "Số Điện Thoại: " + tt.getSdt());
                        }
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            reference.child("thongtinchu").addValueEventListener(eventListener);
        }
    }

    private void LocThongTin(){
        dataHienThi.clear();
        for (int i = 0; i < dataChu.size(); i++) {
            ThongTinChu tt = dataChu.get(i);
            if (tuKhoa.equals("") || tt.getTen().contains(tuKhoa) || tt.getEmail().contains(tuKhoa)){
                dataHienThi.add("Tên Chủ: " + tt.getTen() + "\n" + "Email: " + tt.getEmail() + "\n" + "Số Điện Thoại: " + tt.getSdt());
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        reference.child("thongtinchu").removeEventListener(eventListener);
        eventListener = null;
        reference = null;
    }
}