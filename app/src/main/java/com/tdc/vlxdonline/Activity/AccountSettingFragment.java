package com.tdc.vlxdonline.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentAccountSettingBinding;

public class AccountSettingFragment extends Fragment {
    FragmentAccountSettingBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountSettingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Sự kiện khi nhấn nút logout
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa thông tin đăng nhập
                LoginActivity.idUser = "";
                LoginActivity.typeUser = -1;

                // Quay về màn hình LoginActivity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Đóng hết màn hình hiện có và ẩn
                startActivity(intent);
                getActivity().finish(); // Đóng Owner_HomeActivity
            }
        });

        return view;
    }

}