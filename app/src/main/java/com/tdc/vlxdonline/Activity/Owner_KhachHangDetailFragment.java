package com.tdc.vlxdonline.Activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerKhachhangDetailBinding;

public class Owner_KhachHangDetailFragment extends Fragment {
    FragmentOwnerKhachhangDetailBinding binding;

    KhachHang khachHang;
    String idChuLogin = LoginActivity.idUser.substring(0, LoginActivity.idUser.indexOf("@"));
    String idKH;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOwnerKhachhangDetailBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    //TODO: Hien thi thong tin khach hang
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Thiết lập Toolbar cho Fragment
        setupToolbar(view);

        // Lấy ID khách hàng từ Bundle rồi truy xuất thông tin khách hàng từ firebase và Hiển thị lên giao diện
        getDataKhachHang();

        // Buttons
        setupAuthentButtons();
        setupCallButton();
    }

    // NHẬN ID TỪ BUNDLE, TRUY XUẤT FIREBASE VÀ HIỂN THỊ THÔNG TIN LÊN GIAO DIỆN
    private void getDataKhachHang() {
        // getArguments() trả về Bundle chứa thông tin được truyền từ Fragment trước
        if (getArguments() != null) // Kiểm tra xem Bundle có tồn tại hay không
        {
            // Lấy thông tin khách hàng từ Bundle
            idKH = getArguments().getSerializable("idKH").toString();

            // Lấy thông tin khách hàng từ firebase thông qua ID
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("customers");
            db.child(idKH).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Lấy thông tin khách hàng từ firebase và ánh xạ vào đối tượng KhachHang
                        khachHang = dataSnapshot.getValue(KhachHang.class);
                        khachHang.setID(idKH);
                        Log.d("l.d", "nhanIDKhachHangTuBundle: " + khachHang.toString());

                        if (khachHang != null) {
                            binding.etID.setText(khachHang.getID());
                            binding.etTen.setText(khachHang.getTen());
                            binding.etSDT.setText(khachHang.getSdt());
                            binding.etEmail.setText(khachHang.getEmail());
                            binding.etCCCD.setText(khachHang.getSoCCCD());
                            binding.etDiaChi.setText(khachHang.getDiaChi());

                            setupAuthenticated(idKH);

                        } else {
                            Log.d("l.d", "khách hàng không tồn tại trong cơ sở dữ liệu.");
                        }
                    } else {
                        Log.d("l.d", "Không tìm thấy khách hàng với ID: " + idKH);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("l.d", "Lỗi truy xuất thông tin khách hàng từ firebase: " + databaseError.getMessage());
                }
            });

            hienthiAnhCCCD();
        } else {
            Log.d("l.d", "nhanIDKhachHangTuBundle: Lỗi truyền bundle từ fragment qua Detail");
        }
    }

    // XÁC THỰC KHÁCH HÀNG: HIỂN THỊ THÔNG TIN VÀ TRẠNG THÁI XÁC THỰC
    private void setupAuthenticated(String idKH) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("duyetkhachhang");
        db.child(idChuLogin).child(idKH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy trạng thái xác thực của khách hàng từ trường "trangthai"
                    String authStatus = snapshot.child("trangthai").getValue(String.class);

                    if ("1".equals(authStatus)) {
                        // Nếu trạng thái là "1", đặt màu backgroundTint thành xanh lá (#4CAF50)
                        binding.ivAuthenticated.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                    } else if ("0".equals(authStatus)) {
                        // Nếu trạng thái là "0", đặt màu backgroundTint thành Đỏ (#F44336)
                        binding.ivAuthenticated.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                    } else {
                        Log.d("l.d", "khách hàng không xác định được trạng thái xác thực.");
                    }
                } else {
                    Log.d("l.d", "khách hàng không tồn tại trong cơ sở dữ liệu.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("l.d", "Đã xảy ra lỗi khi truy cập database: " + error.getMessage());
            }
        });
    }

    // XÁC THỰC KHÁCH HÀNG: CẬP NHẬT TRẠNG THÁI XÁC THỰC
    private void updateAuthenticationStatus(String status) {

        // Cập nhật trạng thái xác thực cho khách hàng trong cơ sở dữ liệu
        DatabaseReference dbDuyetKhachHanng = FirebaseDatabase.getInstance().getReference("duyetkhachhang");
        dbDuyetKhachHanng.child(idChuLogin).child(idKH).child("trangthai").setValue(status)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật màu của ivAuthenticated theo trạng thái mới
                    if ("1".equals(status)) {
                        binding.ivAuthenticated.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                        Toast.makeText(getContext(), "Xác thực thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.ivAuthenticated.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                        Toast.makeText(getContext(), "Hủy xác thực thành công", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("l.d", "Trạng thái xác thực đã được cập nhật thành công.");
                })
                .addOnFailureListener(e -> Log.d("l.d", "Lỗi cập nhật trạng thái xác thực: " + e.getMessage()));

        // Cập nhật trạng thái thông báo cho chu cua hang ve yeu cau xac thuc cua khach hang
        if ("1".equals(status)) {
            DatabaseReference dbThongBaoChu = FirebaseDatabase.getInstance().getReference("thongbaochu");
            dbThongBaoChu.child(idChuLogin).child(idKH).child("xacthuc").setValue("0");
        }

    }

    // ẢNH: HÀM ĐỂ HIỂN THỊ ẢNH CC
    private void hienthiAnhCCCD() {
        // Lấy dữ liệu của nhân viên từ Firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("customers");
        db.child(idKH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra nếu có dữ liệu
                if (dataSnapshot.exists()) {
                    // Lấy dữ liệu của nhân viên
                    String anhAvata = dataSnapshot.child("avata").getValue(String.class);
                    String anhCC1 = dataSnapshot.child("cccdMatTruoc").getValue(String.class);
                    String anhCC2 = dataSnapshot.child("cccdMatSau").getValue(String.class);

                    // Hiển thị hình ảnh
                    if (!anhAvata.equals("N/A")) {
                        Glide.with(getContext()).load(anhAvata) // Tải ảnh từ URL
                                .into(binding.ivAvata); // imageViewCC là ID của ImageView trong layout
                    } else
                        binding.ivAvata.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_report_image));

                    if (!anhCC1.equals("N/A")) {
                        Glide.with(getContext()).load(anhCC1) // Tải ảnh từ URL
                                .into(binding.ivCCCD1); // imageViewCC là ID của ImageView trong layout
                    } else
                        binding.ivCCCD1.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_report_image));

                    if (!anhCC2.equals("N/A")) {
                        Glide.with(getContext()).load(anhCC2) // Tải ảnh từ URL
                                .into(binding.ivCCCD2); // imageViewCC2 là ID của ImageView trong layout
                    } else
                        binding.ivCCCD2.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_report_image));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("Owner_NhanVienDetail", "Database error: " + databaseError.getMessage());
            }
        });
    }

    // SỰ KIỆN BUTTONs
    private void setupAuthentButtons() {
        // Lắng nghe sự kiện khi nhấn vào ivAuthenticated
        binding.ivAuthenticated.setOnClickListener(v -> {
            // Hiển thị hộp thoại xác nhận
            new AlertDialog.Builder(getContext())
                    .setTitle("Xác Thực Khách Hàng")
                    .setMessage("Bạn có muốn thay đổi trạng thái xác thực của khách hàng này không?")
                    .setPositiveButton("Xác Thực", (dialog, which) -> updateAuthenticationStatus("1")) // "1" cho Xác Thực
                    .setNegativeButton("Hủy Xác Thực", (dialog, which) -> updateAuthenticationStatus("0")) // "0" cho Hủy Xác Thực
                    .show();
        });
    }

    private void setupCallButton() {
        binding.btnCall.setOnClickListener(view -> {
            // Tạo AlertDialog để xác nhận
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Xác nhận hành động")
                    .setMessage("Bạn muốn gọi hay sao chép số điện thoại?");

            // Nút "Gọi"
            builder.setPositiveButton("Gọi", (dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + khachHang.getSdt()));
                startActivity(intent);
            });

            // Nút "Sao chép"
            builder.setNegativeButton("Sao chép", (dialog, which) -> {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Số điện thoại", khachHang.getSdt());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Đã sao chép số điện thoại", Toast.LENGTH_SHORT).show();
            });

            // Hiển thị dialog
            builder.show();
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
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }
}