package com.tdc.vlxdonline.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Owner_ResetPassWordFragment extends Fragment {
    private EditText edtOldPassword, edtNewPassword, edtConfirmNewPassword;
    private Button btnChangePassword;
    private DatabaseReference databaseReference;
    private String currentUserEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owner_reset_password, container, false);
        setupToolbar(view);

        // Khởi tạo các View
        edtOldPassword = view.findViewById(R.id.edtOldPassword);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = view.findViewById(R.id.edtConfirmNewPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        // Lấy email người dùng hiện tại
        currentUserEmail = "chu1@m.c";  // Lấy email người dùng hiện tại từ thông tin phiên làm việc hoặc context

        // Khởi tạo tham chiếu đến Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("account");

        // Thiết lập sự kiện khi nhấn nút Đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            String oldPassword = edtOldPassword.getText().toString();
            String newPassword = edtNewPassword.getText().toString();
            String confirmNewPassword = edtConfirmNewPassword.getText().toString();

            // Kiểm tra mật khẩu mới nhập đúng không
            if (newPassword.equals(confirmNewPassword)) {
                // Kiểm tra mật khẩu mới có trừng với mật khẩu cũ không
                if (newPassword.equals(oldPassword)) {
                    Toast.makeText(getContext(), "Mật khẩu mới không được trùng với mật khẩu cũ!", Toast.LENGTH_SHORT).show();
                } else
                    verifyOldPasswordAndChange(oldPassword, newPassword);
            } else {
                Toast.makeText(getContext(), "Mật khẩu mới không khớp!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void verifyOldPasswordAndChange(String oldPassword, String newPassword) {
        // Mã hóa mật khẩu cũ
        String hashedOldPassword = hashPassword(oldPassword);

        // Lấy dữ liệu từ Firebase và kiểm tra mật khẩu cũ
        databaseReference.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String storedPassword = snapshot.child("pass").getValue(String.class);

                        if (storedPassword != null && storedPassword.equals(hashedOldPassword)) {
                            // Nếu mật khẩu cũ đúng, thực hiện đổi mật khẩu
                            String hashedNewPassword = hashPassword(newPassword);
                            snapshot.getRef().child("pass").setValue(hashedNewPassword);

                            // Hiển thị dialog xác nhận đổi mật khẩu thành công
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Đổi mật khẩu")
                                    .setMessage("Đổi mật khẩu thành công!")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        if (getActivity() != null) {
                                            getActivity().onBackPressed();
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(getContext(), "Mật khẩu cũ không chính xác!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Lỗi kết nối đến Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm mã hóa mật khẩu SHA-256
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

    // CUỐI: THIẾT LẬP TOOLBAR VÀ ĐIỀU HƯỚNG
    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Xử lý khi nhấn nút quay về trên Toolbar
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }
}
