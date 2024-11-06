package com.tdc.vlxdonline.Activity;

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
    String idKH;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    }

    // NHẬN ID TỪ BUNDLE, TRUY XUẤT FIREBASE VÀ HIỂN THỊ THÔNG TIN LÊN GIAO DIỆN
    private void getDataKhachHang() {
        // getArguments() trả về Bundle chứa thông tin được truyền từ Fragment trước
        if (getArguments() != null) // Kiểm tra xem Bundle có tồn tại hay không
        {
            // Lấy thông tin khách hàng từ Bundle
            idKH = getArguments().getSerializable("idKH").toString();

            // Hiển thị thông tin ID khách hàng lên giao diện
            Toast.makeText(getContext(), "ID Khach Hang\n" + idKH, Toast.LENGTH_SHORT).show();
            Log.d("l.d", "nhanIDKhachHangTuBundle: " + idKH.toString());

            // Lấy thông tin khách hàng từ firebase thông qua ID
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("customers");

            db.child(idKH).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Lấy thông tin khách hàng từ firebase và ánh xạ vào đối tượng KhachHang
                        khachHang = dataSnapshot.getValue(KhachHang.class);
                        khachHang.setID(dataSnapshot.getKey());
                        Log.d("l.d", "nhanIDKhachHangTuBundle: " + khachHang.toString());

                        if (khachHang != null) {
                            binding.etID.setText(khachHang.getID());
                            binding.etTen.setText(khachHang.getTen());
                            binding.etSDT.setText(khachHang.getSdt());
                            binding.etEmail.setText(khachHang.getEmail());
                            binding.etCCCD.setText(khachHang.getSoCCCD());
                            binding.etDiaChi.setText(khachHang.getDiaChi());

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
                        Glide.with(getContext())
                                .load(anhAvata) // Tải ảnh từ URL
                                .into(binding.ivAvata); // imageViewCC là ID của ImageView trong layout
                    } else
                        binding.ivAvata.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_report_image));

                    if (!anhCC1.equals("N/A")) {
                        Glide.with(getContext())
                                .load(anhCC1) // Tải ảnh từ URL
                                .into(binding.ivCCCD1); // imageViewCC là ID của ImageView trong layout
                    } else
                        binding.ivCCCD1.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_report_image));

                    if (!anhCC2.equals("N/A")) {
                        Glide.with(getContext())
                                .load(anhCC2) // Tải ảnh từ URL
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