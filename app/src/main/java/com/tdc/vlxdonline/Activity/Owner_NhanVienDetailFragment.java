package com.tdc.vlxdonline.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tdc.vlxdonline.Model.ChucVu;
import com.tdc.vlxdonline.Model.NhanVien;
import com.tdc.vlxdonline.Model.Users;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerNhanvienDetailBinding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Owner_NhanVienDetailFragment extends Fragment {
    private FragmentOwnerNhanvienDetailBinding binding;

    private NhanVien nhanVien = new NhanVien();

    //ID nhân viên đc truyền từ Fragment trước qua
    private String idNhanVien;

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
    // Uri để lưu trữ đường dẫn đến ảnh được chọn (biến này không lưu lại link ảnh từ firebase tải về)
    private Uri uriAnhCCTruoc, uriAnhCCSau;

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
        // Kiểm tra quyền đọc bộ nhớ
        checkPermissions();
        //Thiết lập Toolbar cho Fragment
        setupToolbar(view);

        // Khởi tạo Spinner và Adapter
        chucVuAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listChucVuSpinner);
        chucVuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerChucVu.setAdapter(chucVuAdapter);

        // Lấy danh sách chức vụ từ Firebase và cập nhật vào Spinner
        setupSpinner();

        // Lấy ID nhân viên từ Bundle rồi truy xuất thông tin nhân viên từ firebase và Hiển thị lên giao diện
        getDataNhanVien();

        // Bắt sự kiện các Button
        setupEditButton();
        setupSaveButton();
        setupHiddenButton();
        setupCancelButton();
        setupResetPassWord();

        // Bắt nhập dữ liệu đầu vào
        binding.etSDT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnLuuLai.setEnabled(false);
                String vietnamPhoneRegex = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$";

                // Kiểm tra định dạng sdt mỗi khi có thay đổi
                String phone = s.toString();
                if (phone.matches(vietnamPhoneRegex) == true) // Kiểm tra định dạng phone
                {
                    setVisibilitySaveButton(true);

                    // Bắt đầu kiểm tra tính duy nhất nếu phone hợp lệ
                    kiemTraSDTDuyNhat(phone);
                } else {
                    binding.etSDT.setError("Phone không hợp lệ");
                    setVisibilitySaveButton(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etTenNhanVien.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String tenNhanVien = binding.etTenNhanVien.getText().toString();

                // Kiểm tra điều kiện độ dài tên
                if (tenNhanVien.isEmpty() || tenNhanVien.length() < 2) {
                    binding.etTenNhanVien.setError("Vui lòng nhập đủ họ tên nhân viên");
                    setVisibilitySaveButton(false);
                    return;
                }

                // Kiểm tra ký tự chỉ cho phép chữ cái và khoảng trắng (bao gồm cả ký tự có dấu tiếng Việt)
                if (!tenNhanVien.matches("^[\\p{L} ]+$")) {
                    binding.etTenNhanVien.setError("Không nhập số hoặc ký tự đặc biệt");
                    setVisibilitySaveButton(false);
                    return;
                }

                // Tên hợp lệ
                setVisibilitySaveButton(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
            idNhanVien = getArguments().getSerializable("idNhanVien").toString();

            // Lấy thông tin nhân viên từ firebase thông qua ID
            DatabaseReference dbNhanVien = FirebaseDatabase.getInstance().getReference("nhanvien");
            dbNhanVien.child(idNhanVien).addValueEventListener(new ValueEventListener() {
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

                            // Xử lý trường hợp nhân viên đã ẩn
                            if (nhanVien.getCccd().startsWith("@")) {
                                binding.btnResetPassWord.setEnabled(false);
                            } else binding.btnResetPassWord.setEnabled(true);

                        } else {
                            Log.d("l.d", "Nhân viên không tồn tại trong cơ sở dữ liệu.");
                        }
                    } else {
                        Log.d("l.d", "Không tìm thấy nhân viên với ID: " + idNhanVien);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("l.d", "Lỗi truy xuất thông tin nhân viên từ firebase: " + databaseError.getMessage());
                }
            });

            hienThiAnhCCCD();
        } else {
            Snackbar.make(getView(), "Không Lấy Được ID Nhân Viên", Toast.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
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
    private void setupSpinner() {
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
            binding.etCCCD.setEnabled(false);

            // Hiển thị nút Lưu Lại, Hủy.
            binding.btnHuy.setVisibility(View.VISIBLE);
            binding.btnLuuLai.setVisibility(View.VISIBLE);

            // Neu Nhan Vien An Thi Bo Qua: Nut An, Nut ResetPassWord
            if (idNhanVien.startsWith("@")) {
                binding.btnResetPassWord.setVisibility(View.INVISIBLE);
                binding.btnAn.setVisibility(View.INVISIBLE);
                binding.btnLuuLai.setText("Mở Khóa");
            } else {
                binding.btnResetPassWord.setVisibility(View.VISIBLE);
                binding.btnAn.setVisibility(View.VISIBLE);
                binding.btnLuuLai.setText("Lưu Lại");

                // Hiển thị Spinner chọn chức vụ
                binding.tilChucVu.setVisibility(View.INVISIBLE);
                binding.etChucVu.setVisibility(View.INVISIBLE);
                binding.spinnerChucVu.setVisibility(View.VISIBLE);
                binding.tvChucVu.setVisibility(View.VISIBLE);
                binding.tvChucVu.setText("Chức Vụ");
            }

            // Ẩn nút Chỉnh Sửa
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
                        binding.etCCCD.setEnabled(false);
                        binding.etSDT.setEnabled(false);

                        // Ẩn Spinner và hiển thị TextView cho chức vụ
                        binding.tilChucVu.setVisibility(View.VISIBLE);
                        binding.etChucVu.setVisibility(View.VISIBLE);
                        binding.spinnerChucVu.setVisibility(View.INVISIBLE);
                        binding.tvChucVu.setVisibility(View.INVISIBLE);
                        binding.btnResetPassWord.setVisibility(View.INVISIBLE);

                        // Ẩn nút Lưu Lại, Xóa, Hủy
                        binding.btnLuuLai.setVisibility(View.INVISIBLE);
                        binding.btnHuy.setVisibility(View.VISIBLE);
                        binding.btnAn.setVisibility(View.INVISIBLE);
                        binding.btnHuy.setVisibility(View.INVISIBLE);

                        // Hiển thị nút Sửa sau khi Hủy
                        binding.btnChinhSua.setVisibility(View.VISIBLE);

                        // Khóa chọn ảnh
                        binding.ivCCCD1.setOnClickListener(null);
                        binding.ivCCCD2.setOnClickListener(null);

                        getDataNhanVien();
                    }).setNegativeButton("Không Hủy", null) // Hiển thị hộp thoại
                    .show();
        });
    }

    private void setupHiddenButton() {
        binding.btnAn.setOnClickListener(view -> {
            // Tạo hộp thoại xác nhận
            new AlertDialog.Builder(getContext()).setTitle("Xác Nhận").setMessage("Bạn có chắc chắn muốn ẩn nhân viên không?").setPositiveButton("Có", (dialog, which) -> {

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
                                    // Xóa data cũ
                                    databaseReference.child("nhanvien").child(oldKey).removeValue();
                                    Snackbar.make(getView(), "Ẩn NV thành công", Toast.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                                    // Quay lại màn hình quản lý nhân viên sau khi ẩn
                                    getParentFragmentManager().popBackStack();
                                } else
                                    Toast.makeText(getContext(), "Ẩn NV không thành công", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });


            }).setNegativeButton("Không", null).show();
        });
    }

    private void setupSaveButton() {
        // Mở khóa nhân viên
        if (idNhanVien.startsWith("@")) {
            setupUnHiddenButton();

        } else {
            // Lưu lại thông tin nv
            binding.btnLuuLai.setOnClickListener(v -> {
                nhanVien.setTennv(binding.etTenNhanVien.getText().toString());
                nhanVien.setChucvu(setIDChucVuNhanVien(binding.spinnerChucVu.getSelectedItem().toString()));
                nhanVien.setSdt(binding.etSDT.getText().toString());
                nhanVien.setEmailchu(nhanVien.getEmailchu());
                nhanVien.setEmailnv(nhanVien.getEmailnv());
                String cccd = nhanVien.getCccd();

                if (batDieuKienDuLieuDauVao(nhanVien) == true) {
                    new AlertDialog.Builder(getContext()).setTitle("Xác Nhận").setMessage("Bạn có chắc chắn muốn lưu thay đổi không?").setPositiveButton("Có", (dialog, which) -> {

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

                            dbNhanVien.child(cccd).setValue(nhanVien).addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                binding.etTenNhanVien.setEnabled(false);
                                binding.etSDT.setEnabled(false);
                                binding.tilChucVu.setVisibility(View.VISIBLE);
                                binding.etChucVu.setVisibility(View.VISIBLE);
                                binding.spinnerChucVu.setVisibility(View.INVISIBLE);
                                binding.tvChucVu.setVisibility(View.INVISIBLE);
                                binding.btnLuuLai.setVisibility(View.INVISIBLE);
                                binding.btnHuy.setVisibility(View.VISIBLE);
                                binding.btnAn.setVisibility(View.INVISIBLE);
                                binding.btnHuy.setVisibility(View.INVISIBLE);
                                binding.btnChinhSua.setVisibility(View.VISIBLE);
                                binding.ivCCCD1.setOnClickListener(null);
                                binding.ivCCCD2.setOnClickListener(null);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }, progressDialog);  // Truyền ProgressDialog vào hàm uploadAnh
                    }).setNegativeButton("Không", null).show();
                }
            });
        }
    }

    private void setupUnHiddenButton() {
        binding.btnLuuLai.setOnClickListener(view -> {
            new AlertDialog.Builder(getContext()).setTitle("Xác Nhận").setMessage("Bạn có chắc chắn muốn Mở Khóa nhân viên không?").setPositiveButton("Có", (dialog, which) -> {

                // Xóa @ để tạo key mới
                String newIDNhanVien = idNhanVien.substring(1, idNhanVien.length() - 1);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                // Tao Account
                createAccountForNhanVien(newIDNhanVien);

                // Đổi mã nhân viên
                databaseReference.child("nhanvien").child(idNhanVien).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        if (dataSnapshot.exists()) {
                            // Ghi dữ liệu vào key mới
                            databaseReference.child("nhanvien").child(newIDNhanVien).setValue(dataSnapshot.getValue()).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    // Xóa key cũ
                                    databaseReference.child("nhanvien").child(idNhanVien).removeValue();
                                    Toast.makeText(getContext(), "Mở Khóa NV thành công", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(getContext(), "Mở Khóa NV không thành công", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
            }).setNegativeButton("Không", null).show();
        });
    }

    private void setupResetPassWord() {
        binding.btnResetPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo tài khoản với mật khẩu ngẫu nhiên và mã hóa
                String emailNVLogin = nhanVien.getEmailnv();
                String passNVLogin = String.valueOf((int) (Math.random() * 1000000)); // Tạo mật khẩu ngẫu nhiên 6 chữ số
                String hashedPassword = hashPassword(passNVLogin); // Mã hóa mật khẩu

                DatabaseReference dbrfAccount = FirebaseDatabase.getInstance().getReference("account");
                dbrfAccount.child(nhanVien.getCccd()).child("pass").setValue(hashedPassword).addOnSuccessListener(unused -> {
                    Snackbar.make(getView(), "Reset mật khẩu cho nhân viên thành công", Toast.LENGTH_SHORT).show();
                    // Hiển thị hộp thoại thông tin tài khoản
                    showAccountInfoDialog(emailNVLogin, passNVLogin);
                }).addOnFailureListener(e -> {
                    Snackbar.make(getView(), "Reset mật khẩu cho nhân viên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
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

    // TẠO MẬT KHẨU VÀ HIỂN THỊ ACCOUNT MỚI TẠO
    private void createAccountForNhanVien(String cccd) {
        // Tạo tài khoản với mật khẩu ngẫu nhiên và mã hóa
        String userNhanVien = nhanVien.getEmailnv();
        String passwordNhanVien = String.valueOf((int) (Math.random() * 1000000)); // Tạo mật khẩu ngẫu nhiên 6 chữ số
        String hashedPassword = hashPassword(passwordNhanVien); // Mã hóa mật khẩu

        Users usersNhanVienMoi = new Users(userNhanVien, hashedPassword, "nv");

        DatabaseReference dbrfAccount = FirebaseDatabase.getInstance().getReference("account");
        dbrfAccount.child(cccd).setValue(usersNhanVienMoi).addOnSuccessListener(unused -> {
            Snackbar.make(getView(), "Tạo tài khoản cho nhân viên thành công", Toast.LENGTH_SHORT).show();
            // Hiển thị hộp thoại thông tin tài khoản
            showAccountInfoDialog(userNhanVien, passwordNhanVien);
        }).addOnFailureListener(e -> {
            Snackbar.make(getView(), "Tạo TK nhân viên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showAccountInfoDialog(String userNhanVien, String passwordNhanVien) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thông tin tài khoản nhân viên").setMessage("User: " + userNhanVien + "\nPassword: " + passwordNhanVien).setPositiveButton("OK", (dialogInterface, i) -> {
            getParentFragmentManager().popBackStack(); // Quay lại Fragment trước
        }).setNeutralButton("Copy", (dialogInterface, i) -> {
            // Sao chép User và Password vào Clipboard
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("User & Password", userNhanVien + "\n" + passwordNhanVien);
            clipboard.setPrimaryClip(clip);
            getParentFragmentManager().popBackStack(); // Quay lại Fragment trước
            Snackbar.make(getView(), "Đã sao chép User và Password vào clipboard", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    // ẢNH: HÀM ĐỂ HIỂN THỊ ẢNH CC
    private void hienThiAnhCCCD() {
        // Lấy dữ liệu của nhân viên từ Firebase
        DatabaseReference dbNhanVien = FirebaseDatabase.getInstance().getReference("nhanvien");
        dbNhanVien.child(idNhanVien).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra nếu có dữ liệu
                if (dataSnapshot.exists()) {
                    // Lấy dữ liệu của nhân viên
                    // Nếu trong csdl mất trường fiel thì sẽ gây crash app, phải fix lỗi này
                    String anhCC1 = dataSnapshot.child("anhcc1").getValue(String.class);
                    anhCC1 = anhCC1 == null ? "" : anhCC1;
                    String anhCC2 = dataSnapshot.child("anhcc2").getValue(String.class);
                    anhCC2 = anhCC2 == null ? "" : anhCC2;

                    // Hiển thị hình ảnh
                    if (!anhCC1.equals("N/A") && !anhCC1.equals("")) {
                        Glide.with(getContext()).load(anhCC1) // Tải ảnh từ URL
                                .into(binding.ivCCCD1); // imageViewCC là ID của ImageView trong layout
                    } else
                        binding.ivCCCD1.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_report_image));

                    if (!anhCC2.equals("N/A") && !anhCC2.equals("")) {
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
            StorageReference frontImageRef = storageRef.child(nhanVien.getCccd() + "_Truoc.jpg");

            frontImageRef.putFile(uriAnhCCTruoc).addOnSuccessListener(taskSnapshot -> frontImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                nhanVien.setAnhcc1(uri.toString());
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
            StorageReference backImageRef = storageRef.child(nhanVien.getCccd() + "_Sau.jpg");

            backImageRef.putFile(uriAnhCCSau).addOnSuccessListener(taskSnapshot2 -> backImageRef.getDownloadUrl().addOnSuccessListener(uri2 -> {
                nhanVien.setAnhcc2(uri2.toString());
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

        // Kiểm tra ảnh CCCD trước
        if (binding.ivCCCD1.getDrawable() == null ||
                binding.ivCCCD1.getDrawable().getConstantState().equals(getResources().getDrawable(android.R.drawable.ic_menu_report_image).getConstantState())) {

            binding.tvCCCD1.setError("Chưa chọn ảnh CCCD Trước");
            return false;
        } else {
            binding.tvCCCD1.setError(null);  // Xóa lỗi nếu ảnh CCCD Trước đã chọn
        }

        // Kiểm tra ảnh CCCD sau
        if (binding.ivCCCD2.getDrawable() == null ||
                binding.ivCCCD2.getDrawable().getConstantState().equals(getResources().getDrawable(android.R.drawable.ic_menu_report_image).getConstantState())) {

            binding.tvCCCD2.setError("Chưa chọn ảnh CCCD Sau");
            return false;
        } else {
            binding.tvCCCD2.setError(null);  // Xóa lỗi nếu ảnh CCCD Sau đã chọn
        }

        return true;
    }

    // CUỐI: TRẠNG THÁI NÚT THÊM NHÂN VIÊN
    private void setVisibilitySaveButton(Boolean visibility) {
        if (visibility == false) {
            String text = "Lưu Lại";
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new StrikethroughSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.btnLuuLai.setText(spannableString);
            binding.btnLuuLai.setEnabled(false);
            return;
        }

        if (visibility == true) {
            String text = "Lưu Lại";
            binding.btnLuuLai.setText(text);
            binding.btnLuuLai.setEnabled(true);
            return;
        }
    }

    private void kiemTraSDTDuyNhat(String value) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("nhanvien");
        databaseReference.orderByChild("sdt").equalTo(value).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean sdtExists = false;

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // Kiểm tra nếu key của nhân viên trùng với idNhanVien hiện tại thì bỏ qua
                    if (childSnapshot.getKey().equals(idNhanVien)) {
                        continue;
                    }
                    sdtExists = true;
                    break;
                }

                if (sdtExists) {
                    binding.etSDT.setError("SDT đã tồn tại.");
                    setVisibilitySaveButton(false);
                } else {
                    binding.etSDT.setError(null);  // Xóa lỗi nếu nhập vào hợp lệ
                    setVisibilitySaveButton(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi khi kiểm tra trùng số điện thoại: " + databaseError.getMessage());
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

