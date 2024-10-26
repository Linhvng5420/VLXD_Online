package com.tdc.vlxdonline.Activity;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountSettingBinding.inflate(inflater, container, false);
        return inflater.inflate(R.layout.fragment_account_setting, container, false);
    }

        
}