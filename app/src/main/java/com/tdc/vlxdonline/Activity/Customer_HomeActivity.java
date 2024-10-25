package com.tdc.vlxdonline.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ActivityCustomerHomeBinding;

public class Customer_HomeActivity extends AppCompatActivity {
    // Binding
    ActivityCustomerHomeBinding customerHomeBinding;
    private String currentTag = null;
    public static KhachHang info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customerHomeBinding = ActivityCustomerHomeBinding.inflate(getLayoutInflater());
        setContentView(customerHomeBinding.getRoot());

        String email = getIntent().getStringExtra("emailUser");
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();

		// Bắt sự kiện
        ReplaceFragment(new CustomerHomeFragment());
        EventNavigationBottom();
    }

    // Bắt sự kiện nhấn Navbar Bottom
    private void EventNavigationBottom() {
        customerHomeBinding.navCustomer.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_customer_sanpham) {
                ReplaceFragment(new CustomerHomeFragment());
            } else if (itemId == R.id.nav_customer_giohang) {
                ReplaceFragment(new CartFragment());
            } else if (itemId == R.id.nav_customer_donhang) {
                ReplaceFragment(new DanhSachDonHangFragment(0));
            } else if (itemId == R.id.nav_customer_taikhoan) {
                ReplaceFragment(new AccountCustomerFragment());
            }

            return true;
        });
    }

    // Replace khi ấn chọn màn hình khác hiện tại
    public void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(customerHomeBinding.frmCustomer.getId());
        if (currentTag == null && currentFragment != null) currentTag = currentFragment.getClass().getName();
        if (currentTag == null || !currentTag.equals(fragment.getClass().getName()) || currentTag.equals(ProdDetailCustomerFragment.class.getName())) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(customerHomeBinding.frmCustomer.getId(), fragment);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
            fragmentTransaction.commit();
        }
        currentTag = null;
    }

    // Override onBack để tùy chỉnh quay lại
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 1) {
            currentTag = getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount - 2).getName();
            ChangeNavItem();
            getSupportFragmentManager().popBackStack();
        } else {
            showExitConfirmation();
        }
    }

    // Hiển thị hộp thoại xác nhận trước khi thoát ứng dụng
    private void showExitConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông Báo!").setMessage("Xác Nhận Thoát Ứng Dụng?");
        builder.setPositiveButton(R.string.thoat, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        builder.setNegativeButton(R.string.quay_lai, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Drawable drawableIcon = getResources().getDrawable(android.R.drawable.ic_dialog_alert);
        drawableIcon.setTint(Color.RED);
        builder.setIcon(drawableIcon);
        Drawable drawableBg = getResources().getDrawable(R.drawable.bg_item_lg);
        drawableBg.setTint(Color.rgb(130, 130, 130));
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(drawableBg);
        alertDialog.show();
    }

    // Hàm đổi icon navbar khi quay lại fragment trước
    private void ChangeNavItem(){
        if (currentTag.equals(CustomerHomeFragment.class.getName())) {
            customerHomeBinding.navCustomer.setSelectedItemId(R.id.nav_customer_sanpham);
        } else if (currentTag.equals(CartFragment.class.getName())) {
            customerHomeBinding.navCustomer.setSelectedItemId(R.id.nav_customer_giohang);
        } else if (currentTag.equals(DanhSachDonHangFragment.class.getName())) {
            customerHomeBinding.navCustomer.setSelectedItemId(R.id.nav_customer_donhang);
        } else if (currentTag.equals(AccountCustomerFragment.class.getName())) {
            customerHomeBinding.navCustomer.setSelectedItemId(R.id.nav_customer_taikhoan);
        }
    }
}