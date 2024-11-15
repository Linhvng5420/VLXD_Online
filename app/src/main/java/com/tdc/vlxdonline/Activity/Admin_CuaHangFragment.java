package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tdc.vlxdonline.databinding.FragmentAdminCuahangBinding;

public class Admin_CuaHangFragment extends Fragment {
    FragmentAdminCuahangBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminCuahangBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        

        return view;
    }
}