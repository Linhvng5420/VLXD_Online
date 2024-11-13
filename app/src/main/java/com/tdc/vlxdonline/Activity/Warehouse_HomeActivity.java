package com.tdc.vlxdonline.Activity;

import android.content.DialogInterface;
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
import com.tdc.vlxdonline.databinding.ActivityWarehouseHomeBinding;

public class Warehouse_HomeActivity extends AppCompatActivity {
    // Binding
    ActivityWarehouseHomeBinding warehouseHomeBinding;
    String canCuocNV;
    DatabaseReference reference;
    public static NhanVien nhanVien = new NhanVien();
    boolean checkFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        warehouseHomeBinding = ActivityWarehouseHomeBinding.inflate(getLayoutInflater());
        setContentView(warehouseHomeBinding.getRoot());
        canCuocNV = getIntent().getStringExtra("canCuoc");
        reference = FirebaseDatabase.getInstance().getReference("nhanvien").child(canCuocNV);
        DocThongTinNV();

        EventNavigationBottom();

        // Sử dụng OnBackPressedDispatcher để tùy chỉnh hành vi khi nhấn nút back
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Kiểm tra xem có Fragment nào trong back stack không
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
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
        warehouseHomeBinding.navWarehouse.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_warehouse_kho) {
                ReplaceFragment(new GiaoDienKho_Fragment(nhanVien.getEmailchu()));
            } else if (itemId == R.id.nav_warehouse_daxuat) {
                ReplaceFragment(new QuanLyXuatKhoFragment(0));
            } else if (itemId == R.id.nav_warehouse_taikhoan) {
                ReplaceFragment(new TaiKhoanNVFragment());
            } else if (itemId == R.id.nav_warehouse_nhapkho) {
                ReplaceFragment(new GiaoDienDonHang_Fragment());
            } else if (itemId == R.id.nav_warehouse_donhang) {
                //   ReplaceFragment(new ThongTinNhanHang_Fragment());
                ReplaceFragment(new DSDonNVKhoFragment());
            }

            return true;
        });
    }

    private void DocThongTinNV(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    NhanVien nv = dataSnapshot.getValue(NhanVien.class);
                    if (nv != null){
                        nv.setCccd(dataSnapshot.getKey());
                        Warehouse_HomeActivity.nhanVien = nv;
                        if (checkFirst) {
                            ReplaceFragment(new GiaoDienKho_Fragment(nv.getEmailchu()));
                            checkFirst = false;
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment).addToBackStack(null);
        fragmentTransaction.commit();
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