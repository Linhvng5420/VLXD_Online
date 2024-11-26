package com.tdc.vlxdonline.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerSettingDetailBinding;

public class Owner_SettingDetailFragment extends Fragment {
    private FragmentOwnerSettingDetailBinding binding;
    private DatabaseReference databaseReference; // Đối tượng để truy cập Firebase
    String ownerId = LoginActivity.idUser.substring(0, LoginActivity.idUser.indexOf("@"));
    String anhCC2, anhCC1;

    // Mã yêu cầu cho việc chọn ảnh
    private static final int PICK_IMAGE_FRONT_ID = 2;
    private static final int PICK_IMAGE_BACK_ID = 3;
    // Uri để lưu trữ đường dẫn đến ảnh được chọn (biến này không lưu lại link ảnh từ firebase tải về)
    private Uri uriAnhCCTruoc, uriAnhCCSau;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo tham chiếu đến Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("thongtinchu");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Thiết lập Toolbar cho Fragment
        setupToolbar(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho Fragment và liên kết với binding
        binding = FragmentOwnerSettingDetailBinding.inflate(inflater, container, false);
        checkPermissions();

        // Gọi hàm để đọc dữ liệu từ Firebase
        loadOwnerData(ownerId);

        setupEditButton();
        setupCancelButton();
        setupSaveButton();

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

            // Cho phép người dùng chọn ảnh khi nhấn vào các ImageView
            binding.ivCCCD1.setOnClickListener(v1 -> openImagePicker(PICK_IMAGE_FRONT_ID));
            binding.ivCCCD2.setOnClickListener(v1 -> openImagePicker(PICK_IMAGE_BACK_ID));
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

                    hienthiAnhCCCD(id);
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
            new AlertDialog.Builder(getContext()).setTitle("Xác Nhận").setMessage("Bạn có chắc chắn muốn lưu thay đổi không?").setPositiveButton("Lưu", (dialog, which) -> {
                String ten = binding.etTen.getText().toString();
                String sdt = binding.etSDT.getText().toString();
                String diaChi = binding.etDiaChi.getText().toString();

                // Tạo và hiển thị ProgressDialog
                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Đang tải ảnh lên...");
                progressDialog.setCancelable(false);  // Ngăn người dùng đóng dialog khi đang tải
                progressDialog.show();

                // Gọi hàm upload ảnh và cập nhật nhanVien sau khi ảnh đã upload
                uploadAnh(() -> {
                    // Đóng ProgressDialog sau khi upload hoàn tất
                    progressDialog.dismiss();

                    // Cập nhật thông tin chủ cụ thể bằng ID
                    databaseReference.child(ownerId).child("ten").setValue(ten);
                    databaseReference.child(ownerId).child("sdt").setValue(sdt);
                    databaseReference.child(ownerId).child("diaChi").setValue(diaChi);

                    // Cập nhật ảnh cccd
                    databaseReference.child(ownerId).child("cccdtruoc").setValue(anhCC1);
                    databaseReference.child(ownerId).child("cccdsau").setValue(anhCC2);
                }, progressDialog);  // Truyền ProgressDialog vào hàm uploadAnh


                // Vô hiệu hóa các trường chỉnh sửa sau khi lưu
                binding.etTen.setEnabled(false);
                binding.etSDT.setEnabled(false);
                binding.etDiaChi.setEnabled(false);
                binding.btnLuuLai.setVisibility(View.INVISIBLE);
                binding.btnHuy.setVisibility(View.INVISIBLE);
                binding.btnChinhSua.setVisibility(View.VISIBLE);


                // Hiển thị thông báo thành công
                Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
            }).setNegativeButton("Không Lưu", null).show();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // ẢNH: XỬ LÝ ẢNH SAU KHI CHỌN
        super.onActivityResult(requestCode, resultCode, data);
        // Kiểm tra kết quả trả về từ hoạt động chọn ảnh
        if (resultCode == Activity.RESULT_OK && data != null) {
            // Lấy Uri của ảnh đã chọn từ Intent data
            Uri selectedImageUri = data.getData();

            // Dựa vào mã yêu cầu để xác định ảnh nào đã được chọn
            if (requestCode == PICK_IMAGE_FRONT_ID) {
                // Lưu Uri ảnh CMND trước và hiển thị ảnh trong ImageView
                uriAnhCCTruoc = selectedImageUri;
                binding.ivCCCD1.setImageURI(uriAnhCCTruoc); // Hiển thị ảnh CMND trước
            } else if (requestCode == PICK_IMAGE_BACK_ID) {
                // Lưu Uri ảnh CMND sau và hiển thị ảnh trong ImageView
                uriAnhCCSau = selectedImageUri;
                binding.ivCCCD2.setImageURI(uriAnhCCSau); // Hiển thị ảnh CMND sau
            }
        }
    }

    private void uploadAnh(Runnable onUploadComplete, ProgressDialog progressDialog) {
        // Biến đếm số lượng ảnh đã tải thành công
        final int[] uploadCount = {0};
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("CCCD");

        if (uriAnhCCTruoc != null) {
            StorageReference frontImageRef = storageRef.child(ownerId + "_Truoc.jpg");

            frontImageRef.putFile(uriAnhCCTruoc).addOnSuccessListener(taskSnapshot -> frontImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                anhCC1 = uri.toString(); // Hiển thị ảnh CMND trước
                uploadCount[0]++;  // Tăng biến đếm khi tải lên ảnh trước thành công

                // Kiểm tra nếu cả hai ảnh đều đã tải xong
                if (uploadCount[0] == 2) {
                    onUploadComplete.run();
                }
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Upload ảnh trước thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            // Tăng biến đếm nếu ảnh trước không có
            uploadCount[0]++;
        }

        if (uriAnhCCSau != null) {
            StorageReference backImageRef = storageRef.child(ownerId + "_Sau.jpg");

            backImageRef.putFile(uriAnhCCSau).addOnSuccessListener(taskSnapshot2 -> backImageRef.getDownloadUrl().addOnSuccessListener(uri2 -> {
                anhCC2 = uri2.toString(); // Hiển thị ảnh CMND sau
                uploadCount[0]++;  // Tăng biến đếm khi tải lên ảnh sau thành công

                // Kiểm tra nếu cả hai ảnh đều đã tải xong
                if (uploadCount[0] == 2) {
                    onUploadComplete.run();
                }
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Upload ảnh sau thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            // Tăng biến đếm nếu ảnh sau không có
            uploadCount[0]++;
        }

        onUploadComplete.run();
    }

    private void hienthiAnhCCCD(String id) {
        // Lấy dữ liệu của nhân viên từ Firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("thongtinchu/" + id);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    anhCC1 = dataSnapshot.child("cccdtruoc").getValue(String.class);
                    anhCC2 = dataSnapshot.child("cccdsau").getValue(String.class);

                    // Hiển thị hình ảnh
                    try {
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
                    } catch (Resources.NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("l.d", "hienthiAnhCCCD: " + databaseError.getMessage());
            }
        });
    }

    private void checkPermissions() {
        // Kiểm tra xem ứng dụng có quyền truy cập vào bộ nhớ ngoài hay không
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa có quyền, yêu cầu quyền truy cập từ người dùng
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_FRONT_ID);
        }
    }

    private void openImagePicker(int requestCode) {
        // ẢNH: MỞ TRÌNH CHỌN ẢNH
        //ACTION_GET_CONTENT: cho phép chọn một tệp từ bất kỳ nguồn nào, bao gồm cả trình quản lý tệp và các ứng dụng khác.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // Chỉ định loại tệp là hình ảnh
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Thêm thể loại này để đảm bảo rằng trình quản lý tệp hiển thị các tệp có thể mở được.
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), requestCode); // Mở trình hộp thoại chọn ảnh
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