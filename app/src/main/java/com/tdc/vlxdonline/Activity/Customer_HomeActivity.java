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

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ActivityCustomerHomeBinding;

public class Customer_HomeActivity extends AppCompatActivity {
    // Binding
    ActivityCustomerHomeBinding customerHomeBinding;
    private String currentTag = null;
    // Thong tin khach hang dang dang nhap
    public static KhachHang info;
    DatabaseReference referCustomerActi;
    private boolean checkFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customerHomeBinding = ActivityCustomerHomeBinding.inflate(getLayoutInflater());
        setContentView(customerHomeBinding.getRoot());

        referCustomerActi = FirebaseDatabase.getInstance().getReference();
        String email = getIntent().getStringExtra("emailUser");
        try{
            referCustomerActi.child("customers").child(email.substring(0, email.indexOf('@'))).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    KhachHang khach = dataSnapshot.getValue(KhachHang.class);
                    if (khach != null) {
                        Customer_HomeActivity.info = khach;
                        Customer_HomeActivity.info.setEmail(email);
                        Customer_HomeActivity.info.setID(email.substring(0, email.indexOf('@')));
                        if (checkFirst){
                            Toast.makeText(Customer_HomeActivity.this, "Hello " + info.getTen(), Toast.LENGTH_LONG).show();
                            checkFirst = false;
                        }
                    }else{
                        Toast.makeText(Customer_HomeActivity.this, "Tài Khoản Đã Bị Xóa!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }catch (Exception e){}

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
        drawableBg.setTint(Color.rgb(100,220,255));
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