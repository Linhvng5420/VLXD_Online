package com.tdc.vlxdonline.Activity;

import android.content.DialogInterface;
import android.os.Bundle;

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
import com.tdc.vlxdonline.Model.ThongTinChu;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ActivityOwnerHomeBinding;

public class Owner_HomeActivity extends AppCompatActivity {
    // Binding
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
        //Bắt sự kiện
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
        ownerHomeBinding.navOwner.setOnItemSelectedListener(item -> {
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
                ReplaceFragment(new QuanLyKhoFragment());
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
                    Owner_HomeActivity.infoChu = chu;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(ownerHomeBinding.fragmentContainer.getId(), fragment);
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