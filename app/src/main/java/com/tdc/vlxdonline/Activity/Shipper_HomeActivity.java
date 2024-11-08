package com.tdc.vlxdonline.Activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.NhanVien;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ActivityShipperHomeBinding;

public class Shipper_HomeActivity extends AppCompatActivity {
    // Binding
    ActivityShipperHomeBinding shipperHomeBinding;
    String canCuoc;
    DatabaseReference reference;
    public static NhanVien nv = new NhanVien();
    private boolean checkfirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shipperHomeBinding = ActivityShipperHomeBinding.inflate(getLayoutInflater());
        setContentView(shipperHomeBinding.getRoot());
        canCuoc = getIntent().getStringExtra("canCuoc");
        reference = FirebaseDatabase.getInstance().getReference("nhanvien").child(canCuoc);
        DocThongTinNV();

        //ReplaceFragment(new NhanDonFragment());
        // Bắt sự kiện
        EventNavigationBottom();

        // Sử dụng OnBackPressedDispatcher để tùy chỉnh hành vi khi nhấn nút back
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Kiểm tra xem có Fragment nào trong back stack không
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    // Nếu có Fragment, quay về Fragment trước đó
                    getSupportFragmentManager().popBackStack();
                } else {
                    // Nếu không có Fragment, hiển thị hộp thoại xác nhận thoát
                    showExitConfirmation();
                }
            }
        });
    }

    // Bắt sự kiện nhấn Navbar Bottom
    private void EventNavigationBottom() {
        shipperHomeBinding.navShipper.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_shipper_DangGiao) {
                ReplaceFragment(new DonDangGiaoFragment());
            } else if (itemId == R.id.nav_shipper_dagiao) {
                ReplaceFragment(new DonDaGiaoFragment());
            } else if (itemId == R.id.nav_shipper_donhang) {
                ReplaceFragment(new NhanDonFragment(Shipper_HomeActivity.nv.getEmailchu()));
            } else if (itemId == R.id.nav_shipper_taikhoan) {
                ReplaceFragment(new TaiKhoanNVFragment());
            }

            return true;
        });
    }

    public void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(shipperHomeBinding.frShipper.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void DocThongTinNV(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    NhanVien nhanVien = dataSnapshot.getValue(NhanVien.class);
                    if (nhanVien != null){
                        nhanVien.setCccd(dataSnapshot.getKey());
                        Shipper_HomeActivity.nv = nhanVien;
                        if (checkfirst) {
                            ReplaceFragment(new NhanDonFragment(nhanVien.getEmailchu()));
                            checkfirst = false;
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Hiển thị hộp thoại xác nhận trước khi thoát ứng dụng
    private void showExitConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc chắn muốn thoát ứng dụng?")
                .setCancelable(false)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Thoát ứng dụng
                        finishAffinity(); // Đóng tất cả các activity và thoát ứng dụng
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Đóng hộp thoại, không thoát ứng dụng
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}