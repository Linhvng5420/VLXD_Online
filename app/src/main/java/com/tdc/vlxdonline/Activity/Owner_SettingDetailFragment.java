package com.tdc.vlxdonline.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.tdc.vlxdonline.databinding.FragmentOwnerSettingDetailBinding;

public class Owner_SettingDetailFragment extends Fragment {
    private FragmentOwnerSettingDetailBinding binding;
    private DatabaseReference databaseReference; // Đối tượng để truy cập Firebase
    String ownerId = LoginActivity.idUser.substring(0, LoginActivity.idUser.indexOf("@"));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo tham chiếu đến Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("thongtinchu");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho Fragment và liên kết với binding
        binding = FragmentOwnerSettingDetailBinding.inflate(inflater, container, false);

        // Gọi hàm để đọc dữ liệu từ Firebase
        loadOwnerData(ownerId);

        setupEditButton();
        setupCancelButton();

        return binding.getRoot();
    }

    private void setupEditButton() {
        binding.btnChinhSua.setOnClickListener(v -> {
            // Kích hoạt chỉnh sửa cho các trường thông tin
            binding.etTen.setEnabled(true);
            binding.etSDT.setEnabled(true);
            binding.etDiaChi.setEnabled(true);

            // Hiển thị nút Lưu Lại, Hủy.
            binding.btnHuy.setVisibility(View.VISIBLE);
            binding.btnLuuLai.setVisibility(View.VISIBLE);
            binding.btnLuuLai.setText("Lưu Lại");

            // Ẩn nút Chỉnh Sửa
            binding.btnChinhSua.setVisibility(View.INVISIBLE);
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
                    String ten = dataSnapshot.child("ten").getValue(String.class);
                    String diaChi = dataSnapshot.child("diaChi").getValue(String.class);
                    String sdt = dataSnapshot.child("sdt").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String cccd = dataSnapshot.child("cccd").getValue(String.class);

                    // Hiển thị dữ liệu lên các TextView trong giao diện
                    binding.etID.setText(id);
                    binding.etTen.setText(ten);
                    binding.etDiaChi.setText(diaChi);
                    binding.etSDT.setText(sdt);
                    binding.etCCCD.setText(cccd);
                    binding.etEmail.setText(email);
                } else {
                    // Xử lý khi ID chủ không tồn tại
                    Toast.makeText(getContext(), "Không tìm thấy thông tin chủ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi khi truy cập Firebase thất bại
                Toast.makeText(getContext(), "Lỗi truy xuất dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCancelButton() {
        binding.btnHuy.setOnClickListener(v -> {
            // Tạo hộp thoại xác nhận
            new AlertDialog.Builder(getContext()).setTitle("Xác Nhận").setMessage("Bạn có chắc chắn muốn hủy thay đổi không?").setPositiveButton("Hủy", (dialog, which) -> {

                        // Vô hiệu hóa các trường chỉnh sửa sau khi hủy
                        binding.etTen.setEnabled(false);
                        binding.etSDT.setEnabled(false);
                        binding.etDiaChi.setEnabled(false);

                        // Ẩn nút Lưu Lại, Xóa, Hủy
                        binding.btnLuuLai.setVisibility(View.INVISIBLE);
                        binding.btnHuy.setVisibility(View.INVISIBLE);

                        // Hiển thị nút Sửa sau khi Hủy
                        binding.btnChinhSua.setVisibility(View.VISIBLE);

                        loadOwnerData(ownerId);
                    }).setNegativeButton("Không Hủy", null) // Hiển thị hộp thoại
                    .show();
        });
    }

    private void setupSaveButton() {
        // Lưu lại thông tin nv
        binding.btnLuuLai.setOnClickListener(v -> {
            
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Thiết lập Toolbar cho Fragment
        setupToolbar(view);
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