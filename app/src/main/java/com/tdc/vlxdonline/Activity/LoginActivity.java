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
import com.tdc.vlxdonline.Model.TypeUser;
import com.tdc.vlxdonline.Model.Users;
import com.tdc.vlxdonline.databinding.ActivityLoginBinding;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    public static int typeUser, typeEmployee = -1;
    public static String idUser = "";
    ArrayList<TypeUser> dataTypeUser = new ArrayList<>();
    ArrayList<Users> dataUsers = new ArrayList<>();
    ArrayAdapter adapter;
    static String emailUser = null;

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
                                Toast.makeText(LoginActivity.this, "Hello [ User: " + idUser + " ]", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(LoginActivity.this, Owner_HomeActivity.class);
                                intent.putExtra("emailUser", idUser); // Truyền emailUser qua Intent
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

        // Choose role
        binding.spRoleLg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeUser = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}