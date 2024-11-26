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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerSettingBinding;

public class Owner_SettingFragment extends Fragment {
    FragmentOwnerSettingBinding binding;

    String idUser;
    boolean statusShop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idUser = LoginActivity.idUser.substring(0, LoginActivity.idUser.indexOf("@"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOwnerSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Thiết lập Toolbar
        setupToolbar(view);

        // Đọc trạng thái shop (0-đóng 1-mở)
        DatabaseReference dbAccount = FirebaseDatabase.getInstance().getReference("account").child(idUser).child("trangthai/online");
        dbAccount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    statusShop = snapshot.getValue(boolean.class);
                    binding.tvDongCuaHang.setVisibility(!statusShop ? View.GONE : View.VISIBLE);
                    binding.tvMoCuaHang.setVisibility(statusShop ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view, "Lỗi: Không thể đọc trạng thái gian hàng", Snackbar.LENGTH_LONG).show();
            }
        });

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
                            DatabaseReference dbAccount = FirebaseDatabase.getInstance().getReference("account").child(idUser).child("trangthai/online");
                            dbAccount.setValue(false);

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
                            DatabaseReference dbAccount = FirebaseDatabase.getInstance().getReference("account").child(idUser).child("trangthai/online");
                            dbAccount.setValue(true);

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

        // Trang Detail
        // Thực hiện chuyển đổi sang Fragment chi tiết, thay thế Fragment hiện tại
        binding.lnHoSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Owner_SettingDetailFragment()) // Thay thế fragment_container hiện tại bằng fragment chi tiết
                        .addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
                        .commit(); // Thực hiện chuyển đổi
            }
        });

        // Trang Sổ Địa Chỉ
        binding.lnSoDiaChi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Owner_SettingSoDiaChiFragment()) // Thay thế fragment_container hiện tại bằng fragment chi tiết
                        .addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
                        .commit(); // Thực hiện chuyển đổi
            }
        });

        // Trang Sổ TK
        binding.lnStk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Owner_SettingSTKFragment()) // Thay thế fragment_container hiện tại bằng fragment chi tiết
                        .addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
                        .commit(); // Thực hiện chuyển đổi
            }
        });

        binding.lnTraGop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ChuQuanLyTraGopFragment(idUser)) // Thay thế fragment_container hiện tại bằng fragment chi tiết
                        .addToBackStack("tempTag") // Cho phép quay lại màn hình trước khi nhấn nút Back
                        .commit(); // Thực hiện chuyển đổi
            }
        });

        binding.lnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Owner_SettingAboutFragment()) // Thay thế fragment_container hiện tại bằng fragment chi tiết
                        .addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
                        .commit(); // Thực hiện chuyển đổi
            }
        });

        binding.lnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Owner_ResetPassWordFragment()) // Thay thế fragment_container hiện tại bằng fragment chi tiết
                        .addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
                        .commit(); // Thực hiện chuyển đổi
            }
        });
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