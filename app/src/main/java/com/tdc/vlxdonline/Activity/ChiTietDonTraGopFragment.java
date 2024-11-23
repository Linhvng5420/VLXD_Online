package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tdc.vlxdonline.Adapter.ChiTietDonHangAdapter;
import com.tdc.vlxdonline.Model.ChiTietDon;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentChiTietDonTraGopBinding;

import java.util.ArrayList;

public class ChiTietDonTraGopFragment extends Fragment {
    FragmentChiTietDonTraGopBinding binding;
    ArrayList<ChiTietDon> dataChiTiet = new ArrayList<>();
    ChiTietDonHangAdapter adapterChiTiet;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chi_tiet_don_tra_gop, container, false);
    }
}