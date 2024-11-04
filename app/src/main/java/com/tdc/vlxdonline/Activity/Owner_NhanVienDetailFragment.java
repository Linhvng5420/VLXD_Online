package com.tdc.vlxdonline.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.tdc.vlxdonline.Model.ChucVu;
import com.tdc.vlxdonline.Model.NhanVien;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerNhanvienDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class Owner_NhanVienDetailFragment extends Fragment {
    private FragmentOwnerNhanvienDetailBinding binding;

    private NhanVien nhanVien;

    //ID nhân viên đc truyền từ Fragment trước qua
    private String selectedIDNhanVien;
    //Danh sách chức vụ từ Firebase
    private List<ChucVu> listChucVuFireBase = new ArrayList<>();

    //Spinner và list item chức vụ
    private Spinner spinnerChucVu;
    private ArrayAdapter<String> chucVuAdapter;
    private ArrayList<String> listChucVuSpinner = new ArrayList<>();
    private String luuLaiTenChucVu = "N/A";

    // Mã yêu cầu cho việc chọn ảnh
    private static final int PICK_IMAGE_FRONT_ID = 2;
    private static final int PICK_IMAGE_BACK_ID = 3;

    // Uri để lưu trữ đường dẫn đến ảnh được chọn
    private Uri anhCCCDTruoc, anhCCCDSau;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nó sẽ nằm ở đây, vì giao diện spinner hiển thị chức vụ INVISIBLE
        layTatCaDSChucVuTuFirebase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Gán binding cho layout fragment_owner_nhanvien_detail.xml bằng cách sử dụng phương thức inflate()
        binding = FragmentOwnerNhanvienDetailBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Trả về toàn bộ giao diện của fragment
    }

    //TODO: HÀM XỬ LÝ CHỨC NĂNG CỦA VIEW APP
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Thiết lập Toolbar cho Fragment
        setupToolbar(view);

        // Khởi tạo Spinner và Adapter
        chucVuAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listChucVuSpinner);
        chucVuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerChucVu.setAdapter(chucVuAdapter);

        // Lấy danh sách chức vụ từ Firebase và cập nhật vào Spinner
        setEventSpinner();

        // Lấy ID nhân viên từ Bundle rồi truy xuất thông tin nhân viên từ firebase và Hiển thị lên giao diện
        getDataNhanVien();

        // Bắt sự kiện các Button
        setupEditButton();
        setupSaveButton();
        setupDeleteButton(selectedIDNhanVien);
        setupCancelButton();
    }

    // LẤY TẤT CẢ DANH SÁCH CHỨC VỤ TỪ FIREBASE THEO THỜI GIAN THỰC
    private void layTatCaDSChucVuTuFirebase() {
        DatabaseReference dbChucVu = FirebaseDatabase.getInstance().getReference("chucvu");

        dbChucVu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Xóa danh sách tên chức vụ trước khi thêm dữ liệu mới
                listChucVuSpinner.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChucVu chucVu = new ChucVu();
                    String idChucVu = snapshot.getKey();
                    String tenChucVu = snapshot.child("ten").getValue(String.class);

                    chucVu.setIdChucVu(idChucVu);
                    chucVu.setTenChucVu(tenChucVu);
                    listChucVuFireBase.add(chucVu);

                    // Tạo chuỗi theo định dạng "id - tên" và thêm vào danh sách
                    String displayText = tenChucVu;
                    listChucVuSpinner.add(displayText);

                    // Thông báo cho adapter cập nhật dữ liệu cho Spinner
                    chucVuAdapter.notifyDataSetChanged();

                    Log.d("l.d", "layTatCaDSChucVuTuFirebase: " + chucVu.toString());
                    Log.d("l.d", "layTatCaDSChucVuTuFirebase: Display: " + chucVu.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("l.d", "Lỗi khi lấy dữ liệu chức vụ", databaseError.toException());
            }
        });
    }

    // NHẬN ID TỪ BUNDLE, TRUY XUẤT FIREBASE VÀ HIỂN THỊ THÔNG TIN LÊN GIAO DIỆN
    private void getDataNhanVien() {
        // getArguments() trả về Bundle chứa thông tin được truyền từ Fragment trước
        if (getArguments() != null) // Kiểm tra xem Bundle có tồn tại hay không
        {
            // Lấy thông tin nhân viên từ Bundle
            selectedIDNhanVien = getArguments().getSerializable("selectedIDNhanVien").toString();

            // Hiển thị thông tin ID nhân viên lên giao diện
            Toast.makeText(getContext(), "ID Nhân Viên\n" + selectedIDNhanVien, Toast.LENGTH_SHORT).show();
            Log.d("l.d", "nhanIDNhanVienTuBundle: " + selectedIDNhanVien.toString());

            // Lấy thông tin nhân viên từ firebase thông qua ID
            DatabaseReference dbNhanVien = FirebaseDatabase.getInstance().getReference("nhanvien");

            dbNhanVien.child(selectedIDNhanVien).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Lấy thông tin nhân viên từ firebase và ánh xạ vào đối tượng NhanVien
                        nhanVien = dataSnapshot.getValue(NhanVien.class);
                        nhanVien.setCccd(dataSnapshot.getKey());

                        if (nhanVien != null) {
                            binding.etTenNhanVien.setText(nhanVien.getTennv());
                            binding.etSDT.setText(nhanVien.getSdt());
                            binding.etEmail.setText(nhanVien.getEmailnv());
                            binding.etCCCD.setText(nhanVien.getCccd());

                            // Lấy tên chức vụ và gán nó vào Spinner
                            docTenChucVuBangID(nhanVien.getChucvu());

                        } else {
                            Log.d("l.d", "Nhân viên không tồn tại trong cơ sở dữ liệu.");
                        }
                    } else {
                        Log.d("l.d", "Không tìm thấy nhân viên với ID: " + selectedIDNhanVien);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("l.d", "Lỗi truy xuất thông tin nhân viên từ firebase: " + databaseError.getMessage());
                }
            });

            hienthiAnhCCCD();
        } else {
            Log.d("l.d", "nhanIDNhanVienTuBundle: Lỗi truyền bundle từ fragment qua Detail");
        }
    }

    // TÌM VÀ ĐỌC TÊN CHỨC VỤ TRONG listChucVuFireBase BẰNG ID CHỨC VỤ TRUYỀN VÀO
    private void docTenChucVuBangID(String chucVuId) {
        if (chucVuId == null || chucVuId.isEmpty()) {
            // Nếu chucVuId null hoặc rỗng, hiển thị thông báo lỗi
            binding.etChucVu.setText("Lỗi Database [chucvu], Liên hệ Adminstrator.");
            Log.d("l.d", "Lỗi Database không có Field Chức Vụ.");
            return;
        }

        // Tìm chức vụ có ID trùng khớp trong danh sách
        if (listChucVuFireBase != null) {
            String tenChucVu = null;
            for (ChucVu chucVu : listChucVuFireBase) {
                if (chucVu.getIdChucVu().equals(chucVuId)) {
                    tenChucVu = chucVu.getTenChucVu();
                    break;
                }
            }

            if (tenChucVu != null) {
                luuLaiTenChucVu = tenChucVu;
                binding.etChucVu.setText(tenChucVu);
                Log.d("l.d", "docDuLieuChucVu: Lấy được chức vụ với ID = " + chucVuId + ", Tên = " + tenChucVu + ", luuLaiTenChucVu = " + luuLaiTenChucVu);
            } else {
                binding.etChucVu.setText("Chức vụ Mã \"" + chucVuId + "\" không có trong CSDL.");
                Log.d("l.d", "Không tìm thấy chức vụ với ID: " + chucVuId);
            }
        } else Log.d("l.d", "docDuLieuChucVu: listChucVuFireBase NULL, idcv: " + chucVuId);
    }

    // BẮT SỰ KIỆN SPINNER
    private void setEventSpinner() {
        binding.spinnerChucVu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy tên chức vụ được chọn
                String selectedChucVu = listChucVuSpinner.get(position);
                // Xử lý sau khi người dùng chọn chức vụ
                Log.d("Spinner", "Chức vụ được chọn: " + selectedChucVu);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý khi không có gì được chọn
            }
        });

    }

    // THIẾT LẬP SỰ KIỆN CHO CÁC NÚT
    private void setupEditButton() {
        binding.btnChinhSua.setOnClickListener(v -> {
            // Kích hoạt chỉnh sửa cho các trường thông tin
            binding.etTenNhanVien.setEnabled(true);
            binding.etSDT.setEnabled(true);

            // Hiển thị Spinner chọn chức vụ
            binding.tilChucVu.setVisibility(View.INVISIBLE);
            binding.etChucVu.setVisibility(View.INVISIBLE);
            binding.spinnerChucVu.setVisibility(View.VISIBLE);
            binding.tvChucVu.setVisibility(View.VISIBLE);
            binding.tvChucVu.setText("Chức Vụ");

            // Hiển thị nút Lưu Lại, Xóa, Hủy. Ẩn nút Chỉnh Sửa
            binding.btnLuuLai.setVisibility(View.VISIBLE);
            binding.btnXoa.setVisibility(View.VISIBLE);
            binding.btnHuy.setVisibility(View.VISIBLE);
            binding.btnChinhSua.setVisibility(View.INVISIBLE);

            // Cho phép người dùng chọn ảnh khi nhấn vào các ImageView
            binding.ivCCCD1.setOnClickListener(v1 -> openImagePicker(PICK_IMAGE_FRONT_ID));
            binding.ivCCCD2.setOnClickListener(v1 -> openImagePicker(PICK_IMAGE_BACK_ID));
        });
    }

    private void setupCancelButton() {
        binding.btnHuy.setOnClickListener(v -> {
            // Tạo hộp thoại xác nhận
            new AlertDialog.Builder(getContext()).setTitle("Xác Nhận").setMessage("Bạn có chắc chắn muốn hủy thay đổi không?").setPositiveButton("Hủy", (dialog, which) -> {

                        // Vô hiệu hóa các trường chỉnh sửa sau khi hủy
                        binding.etTenNhanVien.setEnabled(false);
                        binding.etSDT.setEnabled(false);

                        // Ẩn Spinner và hiển thị TextView cho chức vụ
                        binding.tilChucVu.setVisibility(View.VISIBLE);
                        binding.etChucVu.setVisibility(View.VISIBLE);
                        binding.spinnerChucVu.setVisibility(View.INVISIBLE);
                        binding.tvChucVu.setVisibility(View.INVISIBLE);

                        // Ẩn nút Lưu Lại, Xóa, Hủy và Hiển thị nút Sửa sau khi Hủy
                        binding.btnLuuLai.setVisibility(View.INVISIBLE);
                        binding.btnHuy.setVisibility(View.VISIBLE);
                        binding.btnXoa.setVisibility(View.INVISIBLE);
                        binding.btnHuy.setVisibility(View.INVISIBLE);
                        binding.btnChinhSua.setVisibility(View.VISIBLE);

                        // Khóa chọn ảnh
                        binding.ivCCCD1.setOnClickListener(null);
                        binding.ivCCCD2.setOnClickListener(null);

                        getDataNhanVien();
                    }).setNegativeButton("Không Hủy", null) // Hiển thị hộp thoại
                    .show();
        });
    }

    private void setupDeleteButton(String idNhanVien) {
        binding.btnXoa.setOnClickListener(view -> {
            // Tạo hộp thoại xác nhận
            new AlertDialog.Builder(getContext())
                    .setTitle("Xác Nhận")
                    .setMessage("Bạn có chắc chắn muốn ẩn nhân viên không?")
                    .setPositiveButton("Có", (dialog, which) -> {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        // Mã key mới bạn muốn đổi
                        String newKey = "@" + idNhanVien;
                        String oldKey = idNhanVien;

                        // Xóa Account
                        databaseReference.child("account").child(oldKey).removeValue();

                        // Đổi mã nhân viên
                        databaseReference.child("nhanvien").child(oldKey).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DataSnapshot dataSnapshot = task.getResult();
                                if (dataSnapshot.exists()) {
                                    // Ghi dữ liệu vào key mới
                                    databaseReference.child("nhanvien").child(newKey).setValue(dataSnapshot.getValue()).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Xóa key cũ
                                            databaseReference.child("nhanvien").child(oldKey).removeValue();

                                            Toast.makeText(getContext(), "Ẩn NV thành công", Toast.LENGTH_SHORT).show();

                                            // Quay lại màn hình quản lý nhân viên sau khi xóa
                                            getParentFragmentManager().popBackStack();
                                        } else
                                            Toast.makeText(getContext(), "Ẩn NV không thành công", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        });


                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    private void setupSaveButton() {
        binding.btnLuuLai.setOnClickListener(v -> {
            nhanVien.setTennv(binding.etTenNhanVien.getText().toString());
            nhanVien.setChucvu(setIDChucVuNhanVien(binding.spinnerChucVu.getSelectedItem().toString()));
            nhanVien.setSdt(binding.etSDT.getText().toString());
            nhanVien.setEmailchu(nhanVien.getEmailchu());
            nhanVien.setEmailnv(nhanVien.getEmailnv());
            String cccd = nhanVien.getCccd();

            if (batDieuKienDuLieuDauVao(nhanVien) == true) {
                new AlertDialog.Builder(getContext()).setTitle("Xác Nhận")
                        .setMessage("Bạn có chắc chắn muốn lưu thay đổi không?")
                        .setPositiveButton("Có", (dialog, which) -> {

                            // Tạo và hiển thị ProgressDialog
                            ProgressDialog progressDialog = new ProgressDialog(getContext());
                            progressDialog.setMessage("Đang tải ảnh lên...");
                            progressDialog.setCancelable(false);  // Ngăn người dùng đóng dialog khi đang tải
                            progressDialog.show();

                            // Gọi hàm upload ảnh và cập nhật nhanVien sau khi ảnh đã upload
                            uploadAnh(() -> {
                                // Đóng ProgressDialog sau khi upload hoàn tất
                                progressDialog.dismiss();

                                // Sau khi upload thành công, cập nhật thông tin nhân viên trong Firebase
                                DatabaseReference dbNhanVien = FirebaseDatabase.getInstance().getReference("nhanvien");

                                // Trong fb không có field cccd
                                nhanVien.setCccd(null);

                                dbNhanVien.child(cccd).setValue(nhanVien)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                            binding.etTenNhanVien.setEnabled(false);
                                            binding.etSDT.setEnabled(false);
                                            binding.tilChucVu.setVisibility(View.VISIBLE);
                                            binding.etChucVu.setVisibility(View.VISIBLE);
                                            binding.spinnerChucVu.setVisibility(View.INVISIBLE);
                                            binding.tvChucVu.setVisibility(View.INVISIBLE);
                                            binding.btnLuuLai.setVisibility(View.INVISIBLE);
                                            binding.btnHuy.setVisibility(View.VISIBLE);
                                            binding.btnXoa.setVisibility(View.INVISIBLE);
                                            binding.btnHuy.setVisibility(View.INVISIBLE);
                                            binding.btnChinhSua.setVisibility(View.VISIBLE);
                                            binding.ivCCCD1.setOnClickListener(null);
                                            binding.ivCCCD2.setOnClickListener(null);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }, progressDialog);  // Truyền ProgressDialog vào hàm uploadAnh
                        })
                        .setNegativeButton("Không", null)
                        .show();
            }
        });
    }

    private void uploadAnh(Runnable onUploadComplete, ProgressDialog progressDialog) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("CCCD");
        StorageReference frontImageRef = storageRef.child(nhanVien.getCccd() + "_Truoc.jpg");
        StorageReference backImageRef = storageRef.child(nhanVien.getCccd() + "_Sau.jpg");

        frontImageRef.putFile(anhCCCDTruoc)
                .addOnSuccessListener(taskSnapshot -> frontImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    nhanVien.setAnhcc1(uri.toString());

                    backImageRef.putFile(anhCCCDSau)
                            .addOnSuccessListener(taskSnapshot2 -> backImageRef.getDownloadUrl().addOnSuccessListener(uri2 -> {
                                nhanVien.setAnhcc2(uri2.toString());
                                onUploadComplete.run();  // Gọi callback sau khi cả hai ảnh đã upload xong
                            }))
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Upload ảnh sau thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Upload ảnh trước thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // TÌM VÀ ĐỌC ID CHỨC VỤ TRONG listChucVuFireBase BẰNG TÊN CHỨC VỤ TRUYỀN VÀO
    private String setIDChucVuNhanVien(String tenChucVu) {
        if (tenChucVu == null || tenChucVu.isEmpty()) {
            Log.d("l.d", "docIDChucVuBangTen: Item Spinner.");
            return "Lỗi: docIDChucVuBangTen";
        }

        // Tìm ID chức vụ có tên trùng khớp trong danh sách
        if (listChucVuFireBase != null) {
            String idChucVu = "Lỗi icChucVu docIDChucVuBangTen";
            for (ChucVu chucVu : listChucVuFireBase) {
                if (chucVu.getTenChucVu().equals(tenChucVu)) {
                    idChucVu = chucVu.getIdChucVu();
                    break;
                }
            }

            if (idChucVu != null) {
                Log.d("l.d", "docDuLieuChucVu: ID = " + idChucVu + ", Tên = " + tenChucVu + ", nhanVien.getChucvu() = " + nhanVien.getChucvu());
                return idChucVu;
            } else {
                Log.d("l.d", "Không tìm thấy chức vụ với tên: " + tenChucVu);
                return "Lỗi: docIDChucVuBangTen";
            }
        } else Log.d("l.d", "docIDChucVuBangTen: listChucVuFireBase NULL, tên cv: " + tenChucVu);

        return "Lỗi: docIDChucVuBangTen";
    }

    // ẢNH: HÀM ĐỂ HIỂN THỊ ẢNH CC
    private void hienthiAnhCCCD() {
        // Lấy dữ liệu của nhân viên từ Firebase
        DatabaseReference dbNhanVien = FirebaseDatabase.getInstance().getReference("nhanvien");
        dbNhanVien.child(selectedIDNhanVien).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra nếu có dữ liệu
                if (dataSnapshot.exists()) {
                    // Lấy dữ liệu của nhân viên
                    String anhCC1 = dataSnapshot.child("anhcc1").getValue(String.class);
                    String anhCC2 = dataSnapshot.child("anhcc2").getValue(String.class);

                    // Hiển thị hình ảnh
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
                        binding.ivCCCD1.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_report_image));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("Owner_NhanVienDetail", "Database error: " + databaseError.getMessage());
            }
        });
    }

    // ẢNH: MỞ TRÌNH CHỌN ẢNH
    private void openImagePicker(int requestCode) {
        //ACTION_GET_CONTENT: cho phép chọn một tệp từ bất kỳ nguồn nào, bao gồm cả trình quản lý tệp và các ứng dụng khác.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // Chỉ định loại tệp là hình ảnh
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Thêm thể loại này để đảm bảo rằng trình quản lý tệp hiển thị các tệp có thể mở được.
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), requestCode); // Mở trình hộp thoại chọn ảnh
    }

    // ẢNH: XỬ LÝ ẢNH SAU KHI CHỌN
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Kiểm tra kết quả trả về từ hoạt động chọn ảnh
        if (resultCode == Activity.RESULT_OK && data != null) {
            // Lấy Uri của ảnh đã chọn từ Intent data
            Uri selectedImageUri = data.getData();

            // Dựa vào mã yêu cầu để xác định ảnh nào đã được chọn
            if (requestCode == PICK_IMAGE_FRONT_ID) {
                // Lưu Uri ảnh CMND trước và hiển thị ảnh trong ImageView
                anhCCCDTruoc = selectedImageUri;
                binding.ivCCCD1.setImageURI(anhCCCDTruoc); // Hiển thị ảnh CMND trước
            } else if (requestCode == PICK_IMAGE_BACK_ID) {
                // Lưu Uri ảnh CMND sau và hiển thị ảnh trong ImageView
                anhCCCDSau = selectedImageUri;
                binding.ivCCCD2.setImageURI(anhCCCDSau); // Hiển thị ảnh CMND sau
            }
        }
    }

    // ẢNH: KIỂM TRA VÀ YÊU CẦU QUYỀN TRUY CẬP
    private void checkPermissions() {
        // Kiểm tra xem ứng dụng có quyền truy cập vào bộ nhớ ngoài hay không
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa có quyền, yêu cầu quyền truy cập từ người dùng
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_FRONT_ID);
        }
    }

    // CUỐI: BẮT ĐIỀU KIỆN DỮ LIỆU ĐẦU VÀO
    private boolean batDieuKienDuLieuDauVao(NhanVien nhanVienUpdate) {
        // kiểm tra có ký tự số trong tên nhân viên
        String tenNhanVien = binding.etTenNhanVien.getText().toString();
        for (int i = 0; i < tenNhanVien.length(); i++) {
            if (Character.isDigit(tenNhanVien.charAt(i))) {
                binding.etTenNhanVien.setError("Vui lòng không nhập số vào tên nhân viên");
                return false;
            }
        }

        if (binding.etTenNhanVien.getText().toString().isEmpty()) {
            binding.etTenNhanVien.setError("Vui lòng nhập đủ họ tên nhân viên");
            return false;
        }

        if (binding.etSDT.getText().toString().isEmpty() || binding.etSDT.getText().toString().length() != 10) {
            binding.etSDT.setError("Vui lòng nhập số điện thoại 10 số");
            return false;
        }

        if (binding.etEmail.getText().toString().isEmpty() || !binding.etEmail.getText().toString().contains("@") || !binding.etEmail.getText().toString().contains(".")) {
            binding.etEmail.setError("Vui lòng nhập đúng email");
            return false;
        }

        // Yêu cầu user phải chọn ảnh cccd
        if (binding.ivCCCD1.getDrawable() == null || binding.ivCCCD2.getDrawable() == null) {
            binding.tvCCCD1.setError("Chưa chọn ảnh CCCD Trước");
            binding.tvCCCD2.setError("Chưa chọn ảnh CCCD Sau");
            return false;
        } else {
            binding.tvCCCD1.setError(null);
            binding.tvCCCD2.setError(null);
        }

        // Kiểm tra trùng lặp SDT
        DatabaseReference dbNhanVien = FirebaseDatabase.getInstance().getReference("nhanvien");
        dbNhanVien.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean sdtExists = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Bỏ qua nhân viên hiện tại
                    if (snapshot.getKey().equals(nhanVienUpdate.getCccd())) {
                        continue;
                    }

                    // Lấy thông tin NhanVien
                    NhanVien nhanVien = snapshot.getValue(NhanVien.class);
                    if (nhanVien != null) {
                        // Kiểm tra trùng lặp sdt và email
                        if (nhanVien.getSdt().equals(nhanVienUpdate.getSdt())) {
                            sdtExists = true;
                            break;
                        }
                    }
                }

                if (sdtExists) {
                    binding.etSDT.setError("Số điện thoại đã được sử dụng");
                    Toast.makeText(getContext(), "Số điện thoại đã được sử dụng.", Toast.LENGTH_SHORT).show();
                }

                // Nếu không có trùng lặp, tiến hành cập nhật dữ liệu
                if (!sdtExists) {
                    binding.etSDT.setError(null);
                    Log.d("l.d", "kiemTraSDTEmailTruocKhiLuu: " + nhanVienUpdate.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi khi kiểm tra trùng lặp: " + databaseError.getMessage());
            }
        });

        if (binding.ivCCCD1.getDrawable() == null || binding.ivCCCD2.getDrawable() == null) {
            binding.tvCCCD1.setError("Chưa chọn ảnh CCCD Trước");
            binding.tvCCCD2.setError("Chưa chọn ảnh CCCD Sau");
            return false;
        } else {
            binding.tvCCCD1.setError(null);
            binding.tvCCCD2.setError(null);
        }

        return true;
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

