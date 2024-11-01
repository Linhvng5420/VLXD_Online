package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ActivityShipperHomeBinding;

public class Shipper_HomeActivity extends AppCompatActivity {
    ActivityShipperHomeBinding shipperHomeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shipperHomeBinding = ActivityShipperHomeBinding.inflate(getLayoutInflater());
        setContentView(shipperHomeBinding.getRoot());
        
        //3. Bắt sự kiện
        EventNavigationBottom();
    }

    // Bắt sự kiện nhấn Navbar Bottom
    private void EventNavigationBottom() {
        shipperHomeBinding.navShipper.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_owner_dashboard) {
                ReplaceFragment(new Fragment());
            } else if (itemId == R.id.nav_owner_nhanvien) {
                ReplaceFragment(new Fragment());
            } else if (itemId == R.id.nav_owner_khachhang) {
                ReplaceFragment(new Fragment());
            } else if (itemId == R.id.nav_owner_donhang) {
                ReplaceFragment(new Fragment());
            } else if (itemId == R.id.nav_owner_kho) {
                ReplaceFragment(new Fragment());
            }
            return true;
        });
    }
    private void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id."Thay Thế Tên Fragment_.xml vào đây", fragment);
        fragmentTransaction.commit();
    }
}