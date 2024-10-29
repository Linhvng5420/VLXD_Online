package com.tdc.vlxdonline.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tdc.vlxdonline.R;

public class Warehouse_Kho_HomeActivity extends AppCompatActivity {
    TextView tvQLSP,tvQLDM,tvQLDV,tvQLAnhSP,tvQLBanner;

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

    private void setCtronl() {
        tvQLSP = findViewById(R.id.tvQLSP);
        tvQLDM = findViewById(R.id.tvQLDM);
        tvQLDV = findViewById(R.id.tvQLDV);
        tvQLAnhSP = findViewById(R.id.tvQLAnhSP);
        tvQLBanner = findViewById(R.id.tvQLBanner);
    }
}
