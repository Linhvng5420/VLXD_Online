package com.tdc.vlxdonline.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.ThongTinChu;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ActivityOwnerHomeBinding;

public class Owner_HomeActivity extends AppCompatActivity {
    ActivityOwnerHomeBinding ownerHomeBinding;
    String idChu;
    static ThongTinChu infoChu = new ThongTinChu();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ownerHomeBinding = ActivityOwnerHomeBinding.inflate(getLayoutInflater());
        setContentView(ownerHomeBinding.getRoot());
        idChu = getIntent().getStringExtra("emailUser");
        DocThongTinChu();
        //3. Bắt sự kiện
        EventNavigationBottom();
    }

    // Bắt sự kiện nhấn Navbar Bottom
    private void EventNavigationBottom() {
        ownerHomeBinding.navOwner.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_owner_dashboard) {
                ReplaceFragment(new Fragment());
            } else if (itemId == R.id.nav_owner_nhanvien) {
                ReplaceFragment(new Fragment());
            } else if (itemId == R.id.nav_owner_khachhang) {
                ReplaceFragment(new Fragment());
            } else if (itemId == R.id.nav_owner_donhang) {
                ReplaceFragment(new TaiKhoanNVFragment());
            } else if (itemId == R.id.nav_owner_kho) {
                ReplaceFragment(new QuanLyKhoFragment());

//                Chuyen tu fragment sang activity
//                Intent intent = new Intent(Owner_HomeActivity.this, Warehouse_Kho_HomeActivity.class);
//                startActivity(intent);
            }
            return true;
        });
    }

    private void DocThongTinChu() {
        reference.child("thongtinchu").child(idChu.substring(0, idChu.indexOf("@"))).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ThongTinChu chu = snapshot.getValue(ThongTinChu.class);
                if (chu != null) {
                    chu.setID(snapshot.getKey());
                    infoChu = chu;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(ownerHomeBinding.fragmentContainer.getId(), fragment);
        fragmentTransaction.commit();
    }
}