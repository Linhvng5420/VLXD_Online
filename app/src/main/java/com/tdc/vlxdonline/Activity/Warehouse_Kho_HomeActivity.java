package com.tdc.vlxdonline.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tdc.vlxdonline.R;

public class Warehouse_Kho_HomeActivity extends AppCompatActivity {

    TextView tvQLSP, tvQLDM, tvQLDV, tvQLAnhSP, tvQLBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kho_home_layout);
        setCtronl();
        setEvent();
    }

    private void setEvent() {
        tvQLSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Warehouse_Kho_HomeActivity.this, Warehouse_ThemSanPhamActivity.class);
                startActivity(intent);
            }
        });
        tvQLDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Warehouse_Kho_HomeActivity.this, Warehouse_DanhMucActivity.class);
                startActivity(intent);
            }
        });
        tvQLDV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Warehouse_Kho_HomeActivity.this, Warehouse_DonViActivity.class);
                startActivity(intent);
            }
        });
        tvQLAnhSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Warehouse_Kho_HomeActivity.this, Warehouse_AnhSPActivity.class);
                startActivity(intent);
            }
        });
        tvQLBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Warehouse_Kho_HomeActivity.this, Warehouse_BannerActivity.class);
                startActivity(intent);
            }
        });

    }

    //Bắt sự kiện nhấn Navbar Bottom
//    private void EventNavigationBottom() {
//        warehouseHomeBinding.navWarehouse.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.nav_owner_dashboard) {
//                ReplaceFragment(new Fragment());
//            } else if (itemId == R.id.nav_owner_nhanvien) {
//                ReplaceFragment(new Fragment());
//            } else if (itemId == R.id.nav_owner_khachhang) {
//                ReplaceFragment(new Fragment());
//            } else if (itemId == R.id.nav_owner_donhang) {
//                ReplaceFragment(new Fragment());
//            } else if (itemId == R.id.nav_owner_kho) {
//                ReplaceFragment(new Fragment());
//            }
//
//            return true;
//        });

    private void setCtronl() {
        tvQLSP = findViewById(R.id.tvQLSP);
        tvQLDM = findViewById(R.id.tvQLDM);
        tvQLDV = findViewById(R.id.tvQLDV);
        tvQLAnhSP = findViewById(R.id.tvQLAnhSP);
        tvQLBanner = findViewById(R.id.tvQLBanner);
    }
}
