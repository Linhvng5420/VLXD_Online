package com.tdc.vlxdonline.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.Categorys;
import com.tdc.vlxdonline.Model.DonVi;
import com.tdc.vlxdonline.Model.SendMail;
import com.tdc.vlxdonline.Model.TypeUser;
import com.tdc.vlxdonline.Model.Users;
import com.tdc.vlxdonline.databinding.ActivityLoginBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    public static int typeUser, typeEmployee = -1;
    String idUser = "";

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
                            // Kiểm tra và chuyển đổi kiểu dữ liệu từ Firebase
                            Object emailObj = snapshot.child("email").getValue();
                            Object passObj = snapshot.child("pass").getValue();
                            Object typeObj = snapshot.child("type").getValue();

                            String dbEmail = emailObj != null ? emailObj.toString() : null;
                            String dbPass = passObj != null ? passObj.toString() : null;
                            String dbType = typeObj != null ? typeObj.toString() : null;

                            // Kiểm tra email và mật khẩu
                            if (dbEmail != null && dbEmail.equals(email) && dbPass != null && dbPass.equals(pass)) {
                                isValid = true;
                                switch (dbType) {
                                    case "chu":
                                        typeUser = 0;
                                        break;
                                    case "kh":
                                        typeUser = 1;
                                        break;
                                    case "nv":
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
                            if (typeUser != 1) Toast.makeText(LoginActivity.this, "Hello [ User: " + idUser + " ]", Toast.LENGTH_LONG).show();

                            Class c = null;
                            if (typeUser == 0) {
                                c = Owner_HomeActivity.class;
                            } else if (typeUser == 1) {
                                c = Customer_HomeActivity.class;
                            } else if (typeEmployee == 0) {
                                c = Warehouse_HomeActivity.class;
                            } else if (typeEmployee == 1) {
                                c = Shipper_HomeActivity.class;
                            }

                            Intent intent = new Intent(LoginActivity.this, c);
                            intent.putExtra("emailUser", idUser); // Truyền emailUser qua Intent
                            startActivity(intent);
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