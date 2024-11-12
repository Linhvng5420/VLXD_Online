package com.tdc.vlxdonline.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.NhanVien;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentTaiKhoanNVBinding;

public class TaiKhoanNVFragment extends Fragment {
    FragmentTaiKhoanNVBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTaiKhoanNVBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Goi Event o day

        binding.lnHoSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailNV = LoginActivity.idUser; // Email đăng nhập hiện tại
                DatabaseReference nhanvienRef = FirebaseDatabase.getInstance().getReference("account");

                // Lấy thông tin nhân viên dựa trên email
                nhanvienRef.orderByChild("email").equalTo(emailNV).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Lấy ID nhân viên từ key của node
                            String idNhanVien = dataSnapshot.getChildren().iterator().next().getKey();

                            // Đảm bảo ID không null trước khi chuyển sang Fragment khác
                            if (idNhanVien != null) {
                                Log.d("l.d", "ID của nhân viên: " + idNhanVien);

                                // Tạo Bundle và truyền ID nhân viên vào
                                Bundle bundleIDNhanVien = new Bundle();
                                bundleIDNhanVien.putString("idNhanVien", idNhanVien); // Chỉ cần dùng putString nếu chỉ truyền một giá trị đơn giản

                                // Chuyển đến Fragment chi tiết
                                Owner_NhanVienDetailFragment nhanVienDetailFragment = new Owner_NhanVienDetailFragment();
                                nhanVienDetailFragment.setArguments(bundleIDNhanVien);

                                // Thực hiện chuyển Fragment
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, nhanVienDetailFragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        } else {
                            Log.e("FirebaseError", "Không tìm thấy nhân viên với email: " + emailNV);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("FirebaseError", "Lỗi: " + databaseError.getMessage());
                    }
                });
            }
        });
        binding.lnDoipass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Fragment_DoiPass())
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.lnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Owner_SettingAboutFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
// Sự kiện khi nhấn nút logout
        binding.btnDangxua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setTitle("Đăng Xuất").setMessage("Đăng Xuất Khỏi Ứng Dụng").setPositiveButton("Có", (dialog, which) -> {
                    // Xóa thông tin đăng nhập
                    LoginActivity.idUser = null;
                    LoginActivity.typeUser = -1;

                    // Quay về màn hình LoginActivity
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    // Đóng hết các màn hình hiện có hoặc ẩn để quay lại màn hình đăng nhập
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    getActivity().finish(); // Đóng Owner_HomeActivity để quay lại màn hình đăng nhập
                }).setNegativeButton("Không", null).show();
            }
        });
    }
}