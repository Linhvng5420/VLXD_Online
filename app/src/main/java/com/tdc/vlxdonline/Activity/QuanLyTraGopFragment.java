package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("bills");
    ValueEventListener event;
    String idKhach = Customer_HomeActivity.info.getID();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQuanLyTraGopBinding.inflate(inflater, container, false);
        setAdapterDon();
        DocDataTraGop();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void DocDataTraGop(){

    }

    private void setAdapterDon(){
        adapter = new DonHangAdapter(getActivity(), data);
        adapter.setOnItemDonHangClick(new DonHangAdapter.OnItemDonHangClick() {
            @Override
            public void onItemClick(int position) {

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