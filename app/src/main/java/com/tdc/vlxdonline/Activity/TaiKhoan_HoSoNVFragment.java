package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentTaiKhoanHoSoNVBinding;
import com.tdc.vlxdonline.databinding.FragmentTaiKhoanNVBinding;


public class TaiKhoan_HoSoNVFragment extends Fragment {

    FragmentTaiKhoanHoSoNVBinding binding;
    private DatabaseReference databaseReference; // Đối tượng để truy cập Firebase
    String ownerId = LoginActivity.idUser.substring(0, LoginActivity.idUser.indexOf("@"));


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo tham chiếu đến Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("nhanvien");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTaiKhoanHoSoNVBinding.inflate(inflater, container, false);

        // Gọi hàm để đọc dữ liệu từ Firebase
        loadOwnerData(ownerId);

        setupEditButton();
        setupCancelButton();
        setupSaveButton();

        return binding.getRoot();
    }

    private void setupEditButton() {
        binding.btnSua.setOnClickListener(v -> {
            // Kích hoạt chỉnh sửa cho các trường thông tin
            binding.edtTen.setEnabled(true);
            binding.edtDiaChi.setEnabled(true);
            binding.edtSDT.setEnabled(true);
            binding.edtEmail.setEnabled(true);
            binding.edtCCCD.setEnabled(true);

            // Hiển thị nút Lưu Lại, Hủy.
            binding.btnHuy.setVisibility(View.VISIBLE);
            binding.btnLuu.setVisibility(View.VISIBLE);
            binding.btnLuu.setText("Lưu Lại");

            // Ẩn nút Chỉnh Sửa
            binding.btnSua.setVisibility(View.INVISIBLE);
        });
    }

    private void loadOwnerData(String ownerId) {
        // Truy cập vào thông tin chủ cụ thể bằng ID
        databaseReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Kiểm tra nếu dữ liệu tồn tại
                if (dataSnapshot.exists()) {
                    // Lấy thông tin chủ từ Firebase và ánh xạ vào đối tượng Owner
                    String id = dataSnapshot.getKey();
                    String ten = dataSnapshot.child("tennv").getValue(String.class);
                    String diaChi = dataSnapshot.child("diaChi").getValue(String.class);
                    String sdt = dataSnapshot.child("sdt").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String cccd = dataSnapshot.child("cccd").getValue(String.class);

                    // Hiển thị dữ liệu lên các TextView trong giao diện
                    binding.edtTen.setText(id);
                    binding.edtDiaChi.setText(ten);
                    binding.edtSDT.setText(diaChi);
                    binding.edtEmail.setText(sdt);
                    binding.edtCCCD.setText(cccd);
                } else {
                    // Xử lý khi ID chủ không tồn tại
                    Toast.makeText(getContext(), "Không tìm thấy thông tin chủ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi truy xuất dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void setupCancelButton() {
        binding.btnHuy.setOnClickListener(v -> {
            // Tạo hộp thoại xác nhận
            new AlertDialog.Builder(getContext()).setTitle("Xác Nhận").setMessage("Bạn có chắc chắn muốn hủy thay đổi không?").setPositiveButton("Hủy", (dialog, which) -> {
                        // Vô hiệu hóa các trường chỉnh sửa sau khi hủy
                        binding.edtTen.setEnabled(false);
                        binding.edtDiaChi.setEnabled(false);
                        binding.edtSDT.setEnabled(false);
                        binding.edtEmail.setEnabled(false);

                        // Ẩn nút Lưu Lại, Xóa, Hủy
                        binding.btnLuu.setVisibility(View.INVISIBLE);
                        binding.btnHuy.setVisibility(View.INVISIBLE);

                        // Hiển thị nút Sửa sau khi Hủy
                        binding.btnSua.setVisibility(View.VISIBLE);

                        loadOwnerData(ownerId);
                    }).setNegativeButton("Không Hủy", null) // Hiển thị hộp thoại
                    .show();
        });
    }

    private void setupSaveButton() {
        // Lưu lại thông tin nv
        binding.btnLuu.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext()).setTitle("Xác Nhận").setMessage("Bạn có chắc chắn muốn lưu thay đổi không?").setPositiveButton("Lưu", (dialog, which) -> {
                String ten = binding.edtTen.getText().toString();
                String sdt = binding.edtDiaChi.getText().toString();
                String diaChi = binding.edtSDT.getText().toString();
                String email = binding.edtEmail.getText().toString();

                // Cập nhật thông tin chủ cụ thể bằng ID
                databaseReference.child(ownerId).child("ten").setValue(ten);
                databaseReference.child(ownerId).child("sdt").setValue(sdt);
                databaseReference.child(ownerId).child("diaChi").setValue(diaChi);
                databaseReference.child(ownerId).child("email").setValue(email);


                // Hiển thị thông báo thành công
                Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();

                // Vô hiệu hóa các trường chỉnh sửa sau khi lưu
                binding.edtTen.setEnabled(false);
                binding.tilDiaChi.setEnabled(false);
                binding.edtSDT.setEnabled(false);
                binding.btnLuu.setVisibility(View.INVISIBLE);
                binding.btnHuy.setVisibility(View.INVISIBLE);
                binding.btnSua.setVisibility(View.VISIBLE);
            }).setNegativeButton("Không Lưu", null).show();
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