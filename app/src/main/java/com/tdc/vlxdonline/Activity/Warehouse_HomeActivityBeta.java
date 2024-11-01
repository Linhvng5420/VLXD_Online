package com.tdc.vlxdonline.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.SanPham_Adapter;
import com.tdc.vlxdonline.Model.SanPham_Model;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ActivityWarehouseHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class Warehouse_HomeActivityBeta extends AppCompatActivity {
    ActivityWarehouseHomeBinding warehouseHomeBinding;
    RecyclerView recyclerView;
    List<SanPham_Model> list = new ArrayList<>();
    SanPham_Adapter adapter;
    DatabaseReference reference;
    ValueEventListener listener;
    ImageButton ivbtnthem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        warehouseHomeBinding = ActivityWarehouseHomeBinding.inflate(getLayoutInflater());
        setContentView(warehouseHomeBinding.getRoot());

        //3. Bắt sự kiện
        EventNavigationBottom();
        getDate();
        setEvent();
    }

    private void setEvent() {
    }

    private void getDate() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new SanPham_Adapter(Warehouse_HomeActivityBeta.this, list);
        recyclerView.setAdapter(adapter);
        reference = FirebaseDatabase.getInstance().getReference("SanPham");
        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot items : snapshot.getChildren()) {
                    SanPham_Model sanPhamModel = items.getValue(SanPham_Model.class);
                    list.add(sanPhamModel);
                    // String abc = String.valueOf(items.child("images").getValue(String.class));
                    //tvTest.setText(String.valueOf(list));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
        ivbtnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Warehouse_HomeActivityBeta.this, Warehouse_ThemSanPhamActivity.class);
                startActivity(intent);
            }
        });
    }

    // Bắt sự kiện nhấn Navbar Bottom
    private void EventNavigationBottom() {
        warehouseHomeBinding.navWarehouse.setOnItemSelectedListener(item -> {
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