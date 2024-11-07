package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tdc.vlxdonline.Adapter.DonHangAdapter;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentCacDonDaTaoBinding;

import java.util.ArrayList;

public class CacDonDaTaoFragment extends Fragment {

    FragmentCacDonDaTaoBinding binding;
    ArrayList<DonHang> dataDon = new ArrayList<>();
    DonHangAdapter adapter;

    public CacDonDaTaoFragment(ArrayList<DonHang> dataDon) {
        this.dataDon = dataDon;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCacDonDaTaoBinding.inflate(inflater, container, false);
        setAdapterDon();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setAdapterDon(){
        adapter = new DonHangAdapter(getActivity(), dataDon);
        adapter.setOnItemDonHangClick(new DonHangAdapter.OnItemDonHangClick() {
            @Override
            public void onItemClick(int position) {
                ((Customer_HomeActivity) getActivity()).ReplaceFragment(new ChiTietDonFragment(dataDon.get(position).getId()));
            }
        });
        binding.rcCacDonDaTao.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.rcCacDonDaTao.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}