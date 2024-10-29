package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ActivityRegisterBinding;

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
                backToLogin();
            }
        });
    }

    private void backToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void KhoiTao() {
        dataType.add("Chủ Cửa Hàng");
        dataType.add("Khách Hàng");
        dataType.add("Nhân Viên");
        adap = new AdapterCenterDrop(this, R.layout.item_center_drop, dataType);
        binding.spRoleRg.setAdapter(adap);
    }

    private String getTypeString() {
        switch(this.type) {
            case 0:
                return "chu";
            case 1:
                return "kh";
            case 2:
                return "nv";
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
        if (!type.equals("chu") && !type.equals("kh") && !type.equals("nv")) {
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
                    String accountId = dbRef.push().getKey(); // Tạo key ngẫu nhiên

                    // Tạo một Map để chứa dữ liệu account mới
                    Map<String, Object> newAccount = new HashMap<>();
                    newAccount.put("ten", ten);
                    newAccount.put("sdt", sdt);
                    newAccount.put("email", email);
                    newAccount.put("pass", pass);
                    newAccount.put("type", type);

                    // Thêm tài khoản vào Firebase
                    dbRef.child(accountId).setValue(newAccount).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Thêm tài khoản thành công!", Toast.LENGTH_SHORT).show();
                                backToLogin();
                            } else {
                                Toast.makeText(getApplicationContext(), "Thêm tài khoản thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
    }

}