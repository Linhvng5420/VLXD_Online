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

    // Trong hàm setEvents
    private void setEvents() {
        // Sự kiện nhấn "Enter" trên ô mật khẩu
        binding.edtPassLg.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                performLogin(); // Hàm thực hiện đăng nhập
                return true;
            }
            return false;
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

        // Lựa chọn vai trò
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

    // Tách logic đăng nhập vào hàm riêng
    private void performLogin() {
        String email = binding.edtEmailLg.getText().toString();
        String pass = binding.edtPassLg.getText().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

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
                    } else Toast.makeText(LoginActivity.this, "Đã Đăng Nhập Thành Công \nNhưng hiện chưa hỗ trợ chức năng này", Toast.LENGTH_LONG).show();
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
}