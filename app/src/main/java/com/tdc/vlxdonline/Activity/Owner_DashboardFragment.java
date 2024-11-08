package com.tdc.vlxdonline.Activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerDashboardBinding;

public class Owner_DashboardFragment extends Fragment {
    FragmentOwnerDashboardBinding binding;
    String idUser = null;
    int statusShop = -1;
    DatabaseReference dbThongBaoChu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idUser = LoginActivity.idUser.substring(0, LoginActivity.idUser.indexOf("@"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOwnerDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Đọc trạng thái shop (0-đóng 1-mở)
        dbThongBaoChu = FirebaseDatabase.getInstance().getReference("thongbaochu").child(idUser).child("trangthaishop");
        dbThongBaoChu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    statusShop = snapshot.getValue(Integer.class);
                    binding.tvThongBao.setText(statusShop == 1 ? "Cửa Hàng Đang Online" : "Cửa Hàng Đang Offline");

                    if (statusShop == 0) {
                        binding.tvThongBao.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    } else {
                        binding.tvThongBao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43A047")));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view, "Lỗi: Không thể đọc trạng thái gian hàng", Snackbar.LENGTH_LONG).show();
            }
        });

        binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_owner, new Owner_SettingFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}