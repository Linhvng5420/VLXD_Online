package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.AdapterCenterDrop;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.Model.ModelTTLock;
import com.tdc.vlxdonline.Model.ThongTinChu;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ActivityRegisterBinding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    ArrayList<String> dataType = new ArrayList();
    AdapterCenterDrop adap;
    int type = 0;
    private InputFragment tenFragment;
    private InputFragment sdtFragment;
    private InputFragment emailFragment;
    private InputFragment passwordFragment;
    private InputFragment rePasswordFragment;
    private InputFragment cccdFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvSignin.setPaintFlags(binding.tvSignin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        capNhatFragmentInput();
        setEvents();
    }

    private void setEvents() {
        KhoiTao();
        binding.btnRg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAccount();
            }
        });
        binding.spRoleRg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Quay về trang đăng nhập
        binding.tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void KhoiTao() {
        dataType.add("Chủ Cửa Hàng");
        dataType.add("Khách Hàng");
        adap = new AdapterCenterDrop(this, R.layout.item_center_drop, dataType);
        binding.spRoleRg.setAdapter(adap);
    }

    private String getTypeString() {
        switch(this.type) {
            case 0:
                return "chu";
            case 1:
                return "kh";
            default:
                return "";
        }
    }

    private void addAccount() {
        // Lấy giá trị
        String pass = this.passwordFragment.getEditText().getText().toString();
        String rePass = this.rePasswordFragment.getEditText().getText().toString();
        String email = this.emailFragment.getEditText().getText().toString();
        String sdt = this.sdtFragment.getEditText().getText().toString();
        String ten = this.tenFragment.getEditText().getText().toString();
        String cccd = this.cccdFragment.getEditText().getText().toString();
        String type = this.getTypeString();

        // Kiểm tra tên account
        if (ten.length() < 3) {
            Toast.makeText(this, "Tên phải có ít nhất 3 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra số điện thoại account bằng regex
        if (!sdt.matches("^\\d{10,11}$")) {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra số điện thoại account bằng regex
        if (!cccd.matches("^\\d{10,11}$")) {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng email account bằng regex
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra độ dài của mật khẩu
        if (pass.length() < 6 || rePass.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra password và rePassword
        if(!pass.equals(rePass)){
            Toast.makeText(this, "Password xác nhận không đúng.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra type account
        if (!type.equals("chu") && !type.equals("kh")) {
            Toast.makeText(this, "Loại tài khoản không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kết nối với table "account" trên Firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("account");

        // Kiểm tra email chưa tồn tại trong table
        dbRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email đã tồn tại
                    Toast.makeText(getApplicationContext(), "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                } else {
                    // Email chưa tồn tại, thêm account mới
                    String accountId = email.substring(0, email.indexOf("@"));

                    // Tạo một Map để chứa dữ liệu account mới
                    Map<String, Object> newAccount = new HashMap<>();

                    newAccount.put("email", email);
                    newAccount.put("pass", hashPassword(pass));
                    newAccount.put("type", type);

// Thêm tài khoản vào Firebase
                    dbRef.child(accountId).setValue(newAccount).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Thêm tài khoản thành công!", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(getApplicationContext(), "Thêm tài khoản thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // Refer them thong tin nguoi dung
                    DatabaseReference tempRefer = FirebaseDatabase.getInstance().getReference();
                    if (type.equals("chu")) {
                        ThongTinChu tempChu = new ThongTinChu(null, ten, sdt, email, "N/A", "N/A", "N/A");
                        tempRefer.child("thongtinchu").child(accountId).setValue(tempChu);
                        tempRefer.child("thongtinchu").child(accountId).child("cccd").setValue(cccd);
                        tempRefer.child("thongtinchu").child(accountId).child("cuahang").setValue("N/A");
                        tempRefer.child("account").child(accountId).child("trangthai").setValue(new ModelTTLock());
                    } else {
                        KhachHang tempKH = new KhachHang(null, ten, sdt, email, "N/A", "N/A", "N/A", cccd, "N/A");
                        tempRefer.child("customers").child(accountId).setValue(tempKH);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void capNhatFragmentInput() {
        this.tenFragment = InputFragment.newInstance("Họ Và Tên", "Nhập Họ Tên .....", false, 5);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_ten, this.tenFragment)
                .commit();

        this.sdtFragment = InputFragment.newInstance("Số Điện Thoại", "Nhập Số Điện Thoại .....", false, 5);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_sdt, this.sdtFragment)
                .commit();

        this.emailFragment = InputFragment.newInstance("Email", "Nhập Email .....", false, 5);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_email, this.emailFragment)
                .commit();

        this.passwordFragment = InputFragment.newInstance("Mật Khẩu", "Nhập Mật khẩu...", true, 5);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_pass, this.passwordFragment)
                .commit();

        this.rePasswordFragment = InputFragment.newInstance("Nhập Lại Mật Khẩu", "Nhập Lại Mật Khẩu .....", true, 5);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_rePass, this.rePasswordFragment)
                .commit();

        this.cccdFragment = InputFragment.newInstance("Căn Cước Công Dân", "Nhập Số Căn Cước Công Dân .....", false, 5);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_cccd, this.cccdFragment)
                .commit();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}