package com.tdc.vlxdonline.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.tdc.vlxdonline.databinding.FragmentAdminCuahangDetailBinding;

public class Admin_CuaHangDetailFragment extends Fragment {
    FragmentAdminCuahangDetailBinding binding;
    String LoginUserID = LoginActivity.accountID;
    String LoginUserEmail = LoginActivity.idUser;
    String cuahangID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cuahangID = getArguments().getSerializable("idCH").toString();
        Log.d("l.d", ">>> onCreate: cuahangID: " + cuahangID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminCuahangDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Đổi tiêu đề từ Fragment
        if (requireActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Thông Tin Cửa Hàng");
            }
        }

        return view;
    }
}