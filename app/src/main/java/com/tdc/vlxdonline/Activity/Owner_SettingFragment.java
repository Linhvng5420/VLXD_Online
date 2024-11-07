package com.tdc.vlxdonline.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.databinding.FragmentOwnerAccountSettingBinding;

public class Owner_SettingFragment extends Fragment {
    FragmentOwnerAccountSettingBinding binding;

    String idUser = null;
    int statusShop;
    DatabaseReference dbThongBaoChu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idUser = LoginActivity.idUser.substring(0, LoginActivity.idUser.indexOf("@"));

        // Đọc trạng thái shop (0-đóng 1-mở)
        dbThongBaoChu = FirebaseDatabase.getInstance().getReference("thongbaochu").child(idUser).child("trangthaishop");

        dbThongBaoChu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    statusShop = snapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: Không thể đọc trạng thái gian hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOwnerAccountSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Cập nhật trạng thái shop lên giao diện
        binding.tvDongCuaHang.setVisibility(statusShop == 0 ? View.VISIBLE : View.GONE);
        binding.tvMoCuaHang.setVisibility(statusShop == 1 ? View.VISIBLE : View.GONE);

        // Sự kiện khi nhấn nút logout
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
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

        // Sự kiện khi nhấn nút đóng cửa hàng
        binding.tvDongCuaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("ĐÓNG GIAN HÀNG")
                        .setMessage("Bạn có chắc chắn muốn đóng gian hàng?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("products");

                            // Lấy tất cả các sản phẩm từ Firebase
                            db.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // Duyệt qua tất cả các sản phẩm
                                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                                        // Lấy idChu của mỗi sản phẩm
                                        String idChu = productSnapshot.child("idChu").getValue(String.class);

                                        // Kiểm tra nếu idChu trùng với idUser hiện tại
                                        if (idChu != null && idChu.equals(idUser)) {
                                            // Lấy id của sản phẩm
                                            String idProduct = productSnapshot.getKey();
                                            if (idProduct != null && !idProduct.startsWith("@")) {
                                                // Thêm ký tự '@' vào đầu id của sản phẩm
                                                db.child(idProduct).child("id").setValue("@" + idProduct);
                                            }
                                        }
                                    }
                                    // Thông báo sau khi cập nhật
//                                    Toast.makeText(getContext(), "Gian hàng đã đóng thành công", Toast.LENGTH_SHORT).show();
                                    dbThongBaoChu.setValue(0);
                                    binding.tvDongCuaHang.setVisibility(View.GONE);
                                    binding.tvMoCuaHang.setVisibility(View.VISIBLE);
                                    Snackbar.make(view, "Gian Hàng Của Bạn Đã Offline", Snackbar.LENGTH_LONG)
                                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                                            .show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Xử lý lỗi khi truy cập Firebase không thành công
                                    Toast.makeText(getContext(), "Lỗi: Không thể đóng gian hàng", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).setNegativeButton("Không", null).show();
            }
        });

        // Sự kiện khi nhấn nút mở gian hàng
        binding.tvMoCuaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("MỞ GIAN HÀNG")
                        .setMessage("Bạn có chắc chắn muốn mở gian hàng?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("products");

                            // Lấy tất cả các sản phẩm từ Firebase
                            db.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // Duyệt qua tất cả các sản phẩm
                                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                                        // Lấy idChu của mỗi sản phẩm
                                        String idChu = productSnapshot.child("idChu").getValue(String.class);

                                        // Kiểm tra nếu idChu trùng với idUser hiện tại
                                        if (idChu != null && idChu.equals(idUser)) {
                                            // Lấy key của sản phẩm
                                            String key = productSnapshot.getKey();
                                            // Lấy id của sản phẩm
                                            String idProduct = productSnapshot.child("id").getValue(String.class);

                                            if (idProduct != null && idProduct.startsWith("@")) {
                                                // Xóa ký tự '@' khỏi đầu id @35973975935 của sản phẩm
                                                db.child(key).child("id").setValue(key);
                                            }
                                        }
                                    }
                                    // Thông báo sau khi cập nhật
                                    dbThongBaoChu.setValue(1);
                                    binding.tvDongCuaHang.setVisibility(View.VISIBLE);
                                    binding.tvMoCuaHang.setVisibility(View.GONE);
                                    Snackbar.make(view, "Gian Hàng Của Bạn Đã Online", Snackbar.LENGTH_LONG)
                                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                                            .show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Xử lý lỗi khi truy cập Firebase không thành công
                                    Toast.makeText(getContext(), "Lỗi: Không thể đóng gian hàng", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).setNegativeButton("Không", null).show();
            }
        });
    }
}