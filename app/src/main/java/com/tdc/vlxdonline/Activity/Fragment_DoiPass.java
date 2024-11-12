package com.tdc.vlxdonline.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.R;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Fragment_DoiPass extends Fragment {

    private EditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doi_pass, container, false);

        // Khởi tạo các EditText
        edtCurrentPassword = view.findViewById(R.id.edtCurrentPassword);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);

        // Lắng nghe sự kiện nhấn nút Lưu
        view.findViewById(R.id.btnluupass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        return view;
    }


    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString(); // Trả về mật khẩu đã mã hóa
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void changePassword() {
        String currentPassword = edtCurrentPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Kiểm tra các điều kiện
        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getActivity(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "Mật khẩu mới và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mã hóa mật khẩu hiện tại và mật khẩu mới
        String hashedCurrentPassword = hashPassword(currentPassword);
        String hashedNewPassword = hashPassword(newPassword);

        String emailNV = LoginActivity.idUser; // Email đăng nhập hiện tại
        DatabaseReference nhanvienRef = FirebaseDatabase.getInstance().getReference("account");

        // Tìm kiếm nhân viên theo email
        nhanvienRef.orderByChild("email").equalTo(emailNV).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy thông tin người dùng
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String password = snapshot.child("pass").getValue(String.class);

                        // Kiểm tra mật khẩu hiện tại (mã hóa)
                        if (password != null && password.equals(hashedCurrentPassword)) {
                            // Hiển thị Dialog xác nhận thay đổi mật khẩu
                            showConfirmationDialog(snapshot.getRef(), hashedNewPassword);
                        } else {
                            Toast.makeText(getActivity(), "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Lỗi: " + databaseError.getMessage());
            }
        });
    }
    private void showConfirmationDialog(DatabaseReference userRef, String hashedNewPassword) {
        // Tạo dialog xác nhận
        new android.app.AlertDialog.Builder(getActivity())
                .setTitle("Xác nhận thay đổi mật khẩu")
                .setMessage("Bạn có chắc chắn muốn thay đổi mật khẩu?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    // Cập nhật mật khẩu mới (mã hóa)
                    userRef.child("pass").setValue(hashedNewPassword);
                    Toast.makeText(getActivity(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed(); // Quay lại fragment trước
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    // Không làm gì khi người dùng hủy
                    dialog.dismiss();
                })
                .show();
    }
}
