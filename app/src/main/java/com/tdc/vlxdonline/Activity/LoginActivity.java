package com.tdc.vlxdonline.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.NhanVien;
import com.tdc.vlxdonline.databinding.ActivityLoginBinding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    public static int typeUser;
    public static String typeEmployee = "null";
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    public static String idUser = "";
//    String idUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvSignup.setPaintFlags(binding.tvSignup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        setEvents();
    }

    // Trong hàm setEvents
    private void setEvents() {
        // Login
        binding.btnLg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        // Show-Hide pass
        binding.cbDisPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cbDisPass.isChecked()) {
                    binding.edtPassLg.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    binding.edtPassLg.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        // Dang Ky
        binding.tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

                // Sự kiện nhấn "Enter" trên ô mật khẩu
                binding.edtPassLg.setOnEditorActionListener((view, actionId, event) -> {
                    if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                        performLogin(); // Hàm thực hiện đăng nhập
                        return true;
                    }
                    return false;
                });
            }
        });

        // Sự kiện nhấn nút đăng nhập
        binding.btnLg.setOnClickListener(v -> performLogin());

        // Hiển thị/Ẩn mật khẩu
        binding.cbDisPass.setOnClickListener(v -> {
            if (binding.cbDisPass.isChecked()) {
                binding.edtPassLg.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                binding.edtPassLg.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
    }

    private void getTypeEmployee(String email) {
        dbRef.child("nhanvien").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NhanVien nv = snapshot.getValue(NhanVien.class);
                    if (nv.getEmailnv().equals(email)) {
                        typeEmployee = nv.getChucvu();
                        if (nv.getChucvu().equals("cv1")) {
                            Intent intent = new Intent(LoginActivity.this, Shipper_HomeActivity.class);
                            intent.putExtra("canCuoc", snapshot.getKey()); // Truyền emailUser qua Intent
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, Warehouse_HomeActivity.class);
                            intent.putExtra("canCuoc", snapshot.getKey()); // Truyền emailUser qua Intent
                            startActivity(intent);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Tách logic đăng nhập vào hàm riêng
    private void performLogin() {
        String email = binding.edtEmailLg.getText().toString();
        String pass = binding.edtPassLg.getText().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPass = hashPassword(pass);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("account");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isValid = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Object emailObj = snapshot.child("email").getValue();
                    Object passObj = snapshot.child("pass").getValue();
                    Object typeObj = snapshot.child("type").getValue();

                    String dbEmail = emailObj != null ? emailObj.toString() : null;
                    String dbPass = passObj != null ? passObj.toString() : null;
                    String dbType = typeObj != null ? typeObj.toString() : null;

                    if (dbEmail != null && dbEmail.equals(email) && dbPass != null && dbPass.equals(hashedPass)) {
                        isValid = true;
                        switch (dbType) {
                            case "chu":
                                typeUser = 0;
                                break;
                            case "nv":
                                typeUser = 1;
                                break;
                            case "kh":
                                typeUser = 2;
                                break;
                        }
                        idUser = dbEmail;
                        break;
                    }
                }

                if (isValid) {
                    if (typeUser == 0) {
                        Toast.makeText(LoginActivity.this, "Hello [ User: " + idUser + " ]", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, Owner_HomeActivity.class);
                        intent.putExtra("emailUser", idUser);
                        startActivity(intent);
                    } else if (typeUser == 1) {
                        Intent intent = new Intent(LoginActivity.this, Customer_HomeActivity.class);
                        intent.putExtra("emailUser", idUser);
                        startActivity(intent);
                    } else if (typeUser == 2) {
                        getTypeEmployee(email);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Sai thông tin đăng nhập!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onDestroy();
//        finishAffinity();
    }
}