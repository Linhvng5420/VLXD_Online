package com.tdc.vlxdonline.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

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
    ActivityOwnerHomeBinding ownerHomeBinding;
    String idChu;
    static ThongTinChu infoChu = new ThongTinChu();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String emailUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng View Binding để lấy layout (ActivityOwnerHomeBinding) và gán vào ownerHomeBinding
        ownerHomeBinding = ActivityOwnerHomeBinding.inflate(getLayoutInflater());
        // Thiết lập nội dung hiển thị của Activity từ layout đã được binding
        setContentView(ownerHomeBinding.getRoot());
        idChu = getIntent().getStringExtra("emailUser");
        DocThongTinChu();
        //Bắt sự kiện

        // Thiết lập Fragment mặc định (Hiển thị màn hình mặc định ban đầu của ứng dụng)
        //ReplaceFragment(new Owner_DashboardFragment());

        // Bắt instance
        emailUser = getIntent().getStringExtra("emailUser");
        Log.d("l.e", "onCreate: emailUser = " + emailUser);

        // TODO: Gọi phương thức bắt sự kiện khi nhấn các nút trên thanh điều hướng (Bottom Navigation Bar)
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

    // Phương thức bắt sự kiện cho Bottom Navigation Bar khi người dùng nhấn vào các mục
    private void EventNavigationBottom() {
        // Thiết lập listener cho sự kiện chọn item trong Bottom Navigation
        ownerHomeBinding.navOwner.setOnItemSelectedListener(item -> {
            // Lấy ID của mục được chọn
            int itemId = item.getItemId();

            // Kiểm tra ID và thay thế fragment tương ứng
            if (itemId == R.id.nav_owner_dashboard) {
                ReplaceFragment(new Owner_DashboardFragment());
            } else if (itemId == R.id.nav_owner_nhanvien) {
                ReplaceFragment(new Owner_NhanVienFragment());
            } else if (itemId == R.id.nav_owner_khachhang) {
                ReplaceFragment(new Owner_KhachHangFragment());
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
    // Phương thức thay thế (replace) Fragment hiển thị trên màn hình
    private void ReplaceFragment(Fragment fragment) {
        // Lấy FragmentManager để quản lý các fragment trong Activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Bắt đầu một giao dịch Fragment (FragmentTransaction)
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Thay thế Fragment hiện tại bằng Fragment mới được truyền vào
        fragmentTransaction.replace(R.id.fragment_owner, fragment);
        // Xác nhận thay đổi Fragment bằng cách commit giao dịch
        fragmentTransaction.commit();
    }

    // Hiển thị hộp thoại xác nhận trước khi thoát ứng dụng
    private void showExitConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc chắn muốn thoát ứng dụng?")
                .setCancelable(false)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Đóng tất cả các activity và thoát ứng dụng
                        finishAffinity();
                        onDestroy();
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
