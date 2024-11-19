package com.tdc.vlxdonline.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ActivityAdminHomeBinding;

public class Admin_HomeActivity extends AppCompatActivity {

    private ActivityAdminHomeBinding binding;
    private ActionBarDrawerToggle drawerToggle;
    private String accountID = LoginActivity.accountID;
    private String[] accountInfo = new String[4]; // ID, Email, Tên, SĐT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new Admin_CuaHangFragment());

        setupToolbarDrawer();
        handleAppInitializationErrors();
        loadAdminData();
        setupMenuListeners();
        setupLogoutListener();
        setupBackButtonHandler();
    }

    // Thiết lập toolbar và toggle cho navigation drawer
    private void setupToolbarDrawer() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.cho_nhan_don
        );
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    // Thay thế fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    // Xử lý các lỗi khi khởi động ứng dụng
    private void handleAppInitializationErrors() {
        if (binding == null) {
            Toast.makeText(this, "Không tìm thấy binding", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (accountID == null) {
            Toast.makeText(this, "Không tìm thấy accountID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Lấy dữ liệu admin từ Firebase
    private void loadAdminData() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("account");
        dbRef.child(accountID).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                accountInfo[0] = dataSnapshot.getKey();
                accountInfo[1] = dataSnapshot.child("email").getValue(String.class);
                accountInfo[2] = dataSnapshot.child("ten").getValue(String.class);
                accountInfo[3] = dataSnapshot.child("sdt").getValue(String.class);

                displayAdminInfo();
            } else {
                Toast.makeText(this, "Không tìm thấy TT tài khoản", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hiển thị thông tin admin lên giao diện
    private void displayAdminInfo() {
        binding.userId.setText("ID: " + accountInfo[0]);
        binding.userEmail.setText("Email: " + accountInfo[1]);
        binding.userName.setText("Tên: " + accountInfo[2]);
        binding.userSdt.setText("SĐT: " + accountInfo[3]);
    }

    // Thiết lập các sự kiện click cho menu
    private void setupMenuListeners() {
        binding.navCuaHang.setOnClickListener(view -> handleMenuClick(new Admin_CuaHangFragment()));
//        binding.navSanPham.setOnClickListener(view -> handleMenuClick(new Admin_SanPhamFragment()));
//        binding.navKhieuNai.setOnClickListener(view -> handleMenuClick(new Admin_KhieuNaiFragment()));
    }

    // Xử lý sự kiện click menu
    private void handleMenuClick(Fragment fragment) {
        replaceFragment(fragment);
        binding.drawerLayout.closeDrawer(binding.navView);
    }

    // Thiết lập sự kiện logout
    private void setupLogoutListener() {
        binding.navLogout.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Đăng Xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Có", (dialog, which) -> logoutAndRedirectToLogin())
                .setNegativeButton("Không", null)
                .show());
    }

    // Xử lý logout và chuyển về màn hình login
    private void logoutAndRedirectToLogin() {
        LoginActivity.idUser = null;
        LoginActivity.typeUser = -1;
        LoginActivity.typeEmployee = "";
        LoginActivity.accountID = "";

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Thiết lập sự kiện nút back
    private void setupBackButtonHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    showExitConfirmationDialog();
                }
            }
        });
    }

    // Hiển thị hộp thoại xác nhận thoát ứng dụng
    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Bạn có chắc chắn muốn thoát ứng dụng?")
                .setCancelable(false)
                .setPositiveButton("Có", (dialog, id) -> finishAffinity())
                .setNegativeButton("Không", (dialog, id) -> dialog.cancel())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onBackPressed() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin VLXDOnline");
        }
        super.onBackPressed();
    }

}