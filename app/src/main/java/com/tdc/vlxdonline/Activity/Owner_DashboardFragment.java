package com.tdc.vlxdonline.Activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerDashboardBinding;

public class Owner_DashboardFragment extends Fragment {
    FragmentOwnerDashboardBinding binding;
    String emailOwnerLogin = LoginActivity.idUser;
    String idOwnerLogin = emailOwnerLogin.substring(0, LoginActivity.idUser.indexOf("@")); //Mã ID
    boolean statusShop;
    DatabaseReference dbThongBaoChu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOwnerDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Đọc trạng thái shop (0-đóng 1-mở)
        dbThongBaoChu = FirebaseDatabase.getInstance().getReference("account").child(idOwnerLogin).child("trangthai/online");
        dbThongBaoChu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    statusShop = snapshot.getValue(boolean.class);
                    binding.tvThongBao.setText(statusShop ? "Cửa Hàng Đang Online" : "Cửa Hàng Đang Offline");

                    if (!statusShop) {
                        binding.tvThongBao.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    } else {
                        binding.tvThongBao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43A047")));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view, "Lỗi: Không thể đọc trạng thái gian hàng", Snackbar.LENGTH_LONG).show();
            }
        });

        // Đọc data
        getDataNhanVien();
        getDataKhachHang();
        getDataKho();

        binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new Owner_SettingFragment()).addToBackStack(null).commit();
            }
        });
    }

    private void getDataKho() {
        // Kết nối với Firebase Realtime Database
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("products");

        // Lắng nghe sự thay đổi dữ liệu trong node 'products'
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalProducts = 0;
                long giaNhap = 0;
                long giaBan = 0;
                long tienLoi = 0;

                // Duyệt qua từng sản phẩm
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    // Lấy idChu của sản phẩm
                    String idChu = productSnapshot.child("idChu").getValue(String.class);

                    // Kiểm tra nếu idChu của sản phẩm khớp với idChu của chủ sở hữu
                    if (idChu != null && idChu.equals(idOwnerLogin)) {
                        totalProducts++;  // Tăng tổng số sản phẩm

                        // Lấy giá nhập và giá bán của sản phẩm
                        giaNhap += Long.parseLong(productSnapshot.child("giaNhap").getValue(String.class));
                        giaBan += Long.parseLong(productSnapshot.child("giaBan").getValue(String.class));
                    }
                }

                // Cập nhật giao diện với số lượng sản phẩm
                binding.tvSLSP1.setText(String.valueOf(totalProducts));

                // Hiển thị giá nhập - giá bán cho sản phẩm cuối cùng khớp
                binding.tvSLSP2.setText("Giá nhập: " + giaNhap + "VND \nGiá bán: " + giaBan + " VND \nTiền lợi: " + (giaBan - giaNhap) + " VND");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi không thể truy xuất dữ liệu
                Toast.makeText(getContext(), "Không thể tải dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDataKhachHang() {
        // Kết nối với Firebase Realtime Database
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("duyetkhachhang").child(idOwnerLogin);

        // Lắng nghe sự thay đổi dữ liệu trong node 'duyetkhachhang'
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalCustomer = 0;  // Tổng số khách hàng
                int totalCustomerAuthen = 0;  // Tổng số khách hàng đã xác thực

                // Duyệt qua từng khách hàng của chủ sở hữu
                for (DataSnapshot khachHangSnapshot : snapshot.getChildren()) {
                    // Tăng tổng số khách hàng
                    totalCustomer++;

                    // Lấy trạng thái xác thực của khách hàng
                    String trangthai = khachHangSnapshot.child("trangthai").getValue(String.class);

                    // Kiểm tra nếu khách hàng đã xác thực
                    if ("1".equals(trangthai)) {
                        totalCustomerAuthen++;
                    }
                }

                // Cập nhật giao diện với số lượng khách hàng
                binding.tvSLKH1.setText(String.valueOf(totalCustomer));
                binding.tvSLKH2.setText("Khách hàng đã xác thực: " + totalCustomerAuthen);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi không thể truy xuất dữ liệu
                Toast.makeText(getContext(), "Không thể tải dữ liệu khách hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDataNhanVien() {
        // Kết nối với Firebase Realtime Database
        DatabaseReference nhanvienRef = FirebaseDatabase.getInstance().getReference("nhanvien");

        // Lắng nghe sự thay đổi dữ liệu trong node 'nhanvien'
        nhanvienRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalEmployees = 0;
                int khoEmployees = 0;
                int giaoHangEmployees = 0;
                int otherEmployees = 0;

                // Duyệt qua từng nhân viên trong node 'nhanvien'
                for (DataSnapshot nhanvienSnapshot : snapshot.getChildren()) {
                    String emailchu = nhanvienSnapshot.child("emailchu").getValue(String.class);

                    // Kiểm tra xem emailchu có khớp với idOwnerLogin hay không
                    if (emailchu != null && emailchu.equals(emailOwnerLogin)) {
                        totalEmployees++;

                        // Lấy chức vụ của nhân viên
                        String chucvu = nhanvienSnapshot.child("chucvu").getValue(String.class);

                        // Phân loại nhân viên dựa vào chức vụ
                        if ("cv0".equalsIgnoreCase(chucvu)) {
                            khoEmployees++;
                        } else if ("cv1".equalsIgnoreCase(chucvu)) {
                            giaoHangEmployees++;
                        } else {
                            otherEmployees++;
                        }
                    }
                }

                // Cập nhật UI với số lượng nhân viên
                binding.tvSLNV1.setText(String.valueOf(totalEmployees));
                binding.tvSLNV2.setText("Kho: " + khoEmployees + " \nGiao Hàng: " + giaoHangEmployees + " \nKhác: " + otherEmployees);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi không thể truy xuất dữ liệu
                Toast.makeText(getContext(), "Không thể tải dữ liệu nhân viên", Toast.LENGTH_SHORT).show();
            }
        });
    }
}