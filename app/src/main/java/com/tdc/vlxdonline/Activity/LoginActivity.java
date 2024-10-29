package com.tdc.vlxdonline.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    public static int typeUser;
    public static String idUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvSignup.setPaintFlags(binding.tvSignup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        setEvents();
    }

    private void setEvents() {
        // Login
        binding.btnLg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edtEmailLg.getText().toString();
                String pass = binding.edtPassLg.getText().toString();

                // Kiểm tra rỗng
                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Truy vấn Firebase
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("account");
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isValid = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String dbEmail = snapshot.child("email").getValue(String.class);
                            String dbPass = snapshot.child("pass").getValue(String.class);
                            String dbType = snapshot.child("type").getValue(String.class);

                            // Kiểm tra email và mật khẩu
                            if (dbEmail.equals(email) && dbPass.equals(pass)) {
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
                                // Lưu idUser cho người dùng đã đăng nhập
                                idUser = dbEmail;
                                break;
                            }
                        }

                        if (isValid) {
                            // Chuyển đến màn hình chủ
                            if (typeUser == 0) {
                                Intent intent = new Intent(LoginActivity.this, Owner_HomeActivity.class);
                                Toast.makeText(LoginActivity.this, "Hello [ User: " + idUser + " ]", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                        } else {
                            // Thông báo lỗi đăng nhập
                            Toast.makeText(LoginActivity.this, "Sai thông tin đăng nhập!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                    }
                });
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
    }
}