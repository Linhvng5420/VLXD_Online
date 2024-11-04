package com.tdc.vlxdonline.Activity;

import android.Manifest;
import android.app.Activity;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.tdc.vlxdonline.databinding.FragmentOwnerNhanVienAddBinding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Owner_NhanVienAddFragment extends Fragment {
    FragmentOwnerNhanVienAddBinding binding;

    private String loginEmailUser;
    private NhanVien nhanVien;

    //Danh sách chức vụ từ Firebase
    private List<ChucVu> listChucVuFireBase = new ArrayList<>();

    //Spinner và list item chức vụ
    private Spinner spinnerChucVu;
    private ArrayAdapter<String> chucVuAdapter;
    private ArrayList<String> listChucVuSpinner;

    // Mã yêu cầu cho việc chọn ảnh
    private static final int PICK_IMAGE_FRONT_ID = 2;
    private static final int PICK_IMAGE_BACK_ID = 3;

    // Uri để lưu trữ đường dẫn đến ảnh được chọn
    private Uri anhCCCDTruoc, anhCCCDSau;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy instance loginUser
        if (getArguments() != null) {
            loginEmailUser = getArguments().getString("loginEmailUser");
        } else Log.d("l.d", "onCreate: Lấy Instance thất bại");

        // Khởi tạo đối tượng nhân viên dùng chung và duy nhất trong toàn bộ Fragment
        nhanVien = new NhanVien();

        // Khởi tạo danh sách chức vụ
        listChucVuSpinner = new ArrayList<>();

        // Lấy danh sách chức vụ từ Firebase và cập nhật vào Spinner
        layTatCaDSChucVuTuFirebase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOwnerNhanVienAddBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    //TODO: THỰC HIỆN KHAI BÁO, KHỞI TẠO VÀ XỬ LÝ LOGIC TẠI ĐÂY
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

        // Bắt sự kiện các Button
        setupAddButton();

        // Bắt sự kiện nhập dữ liệu đầu vào
        binding.ivCCCD1.setOnClickListener(v1 -> openImagePicker(PICK_IMAGE_FRONT_ID));
        binding.ivCCCD2.setOnClickListener(v1 -> openImagePicker(PICK_IMAGE_BACK_ID));
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

                // Kiểm tra ký tự
                if (!tenNhanVien.matches("[a-zA-Z ]+")) {
                    binding.etTenNhanVien.setError("Không Nhập Ký Tự Khác A-Z");
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
        binding.etSDT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnThemNhanVien.setEnabled(false);
                String vietnamPhoneRegex = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$";

                // Kiểm tra định dạng sdt mỗi khi có thay đổi
                String phone = s.toString();
                if (phone.matches(vietnamPhoneRegex) == true) // Kiểm tra định dạng phone
                {
                    setVisibilitySaveButton(true);

                    // Bắt đầu kiểm tra tính duy nhất nếu phone hợp lệ
                    checkInputUniqueness("nhanvien", "sdt", phone);
                } else {
                    binding.etSDT.setError("Phone không hợp lệ");
                    setVisibilitySaveButton(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra định dạng email mỗi khi có thay đổi
                String emailInput = s.toString();
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches() == true) // Kiểm tra định dạng Email
                {
                    setVisibilitySaveButton(true);

                    // Bắt đầu kiểm tra tính duy nhất nếu email hợp lệ
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference accountRef = database.getReference("account");
                    DatabaseReference nhanVienRef = database.getReference("nhanvien");

                    // Kiểm tra email trong bảng account
                    accountRef.orderByChild("email").equalTo(emailInput).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                binding.etEmail.setError("Email đã bị đăng ký tài khoản");
                                setVisibilitySaveButton(false);

                            } else {
                                // Nếu không tồn tại trong account, kiểm tra tiếp trong bảng nhanvien
                                nhanVienRef.orderByChild("emailnv").equalTo(emailInput).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            binding.etEmail.setError("Email trùng với nhân viên khác");
                                            setVisibilitySaveButton(false);

                                        } else {
                                            // Email là duy nhất, có thể tiếp tục xử lý lưu
                                            binding.etEmail.setError(null);
                                            setVisibilitySaveButton(false);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Xử lý lỗi nếu có
                                        Toast.makeText(getContext(), "Lỗi kiểm tra email", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Xử lý lỗi nếu có
                            Toast.makeText(getContext(), "Lỗi kiểm tra email", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    binding.etEmail.setError("Email không hợp lệ");
                    setVisibilitySaveButton(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etCCCD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra định dạng email mỗi khi có thay đổi
                String cccd = s.toString();
                if (binding.etCCCD.getText().toString().isEmpty() == false && binding.etCCCD.getText().toString().length() == 10) // Kiểm tra định dạng Email
                {
                    setVisibilitySaveButton(true);

                    // Bắt đầu kiểm tra tính duy nhất nếu cccd hợp lệ
                    checkInputUniqueness("nhanvien", "cccd", cccd);
                } else {
                    binding.etCCCD.setError("Vui lòng nhập CCCD (10 số)");
                    setVisibilitySaveButton(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // Phương thức kiểm tra tính duy nhất của Dữ Liệu Đầu Vào
    private void checkInputUniqueness(String table, String field, String value) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(table);
        databaseReference.orderByChild(field).equalTo(value).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    switch (field) {
                        case "email":
                            binding.etEmail.setError("Email đã tồn tại");
                            break;
                        case "sdt":
                            binding.etSDT.setError("Số điện thoại đã tồn tại");
                            break;
                        case "cccd":
                            binding.etCCCD.setError("CCCD đã tồn tại");
                            break;
                    }

                    setVisibilitySaveButton(false);

                } else {
                    // Xóa Error nếu nhập vào hợp lệ
                    switch (field) {
                        case "email":
                            binding.etEmail.setError(null);
                            break;
                        case "sdt":
                            binding.etSDT.setError(null);
                            break;
                        case "cccd":
                            binding.etCCCD.setError(null);
                            break;
                    }

                    setVisibilitySaveButton(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("l.d", "Lỗi khi kiểm tra Value: " + error.getMessage());
            }
        });
    }

    // LẤY TẤT CẢ DANH SÁCH CHỨC VỤ TỪ FIREBASE THEO THỜI GIAN THỰC
    private void layTatCaDSChucVuTuFirebase() {
        DatabaseReference chucVuRef = FirebaseDatabase.getInstance().getReference("chucvu");

        chucVuRef.addValueEventListener(new ValueEventListener() {
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

                    // thêm vào danh sách spinner
                    String displayText = tenChucVu;
                    listChucVuSpinner.add(displayText);

                    // Thông báo cho adapter cập nhật dữ liệu cho Spinner
                    chucVuAdapter.notifyDataSetChanged();

                    Log.d("l.e", "layTatCaDSChucVuTuFirebase: " + chucVu.toString());
                    Log.d("l.e", "layTatCaDSChucVuTuFirebase: Display: " + chucVu.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("l.e", "Lỗi khi lấy dữ liệu chức vụ", databaseError.toException());
            }
        });
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

    private void setupAddButton() {
        binding.btnThemNhanVien.setOnClickListener(v -> {
            // Bắt điều kiện dữ liệu đầu vào
            if (!batDieuKienDuLieuDauVao()) return;

            // Up ảnh lên Firebase Storage
            nhanVien = new NhanVien();

            // Hộp thoại xác nhận
            new AlertDialog.Builder(getContext()).setTitle("Xác nhận").setMessage("Bạn có muốn thêm nhân viên này?").setPositiveButton("Có", (dialog, which) -> {
                // Lưu giá trị mới
                nhanVien.setTennv(binding.etTenNhanVien.getText().toString());
                setIDChucVuNhanVien(binding.spinnerChucVu.getSelectedItem().toString());
                nhanVien.setSdt(binding.etSDT.getText().toString());
                nhanVien.setEmailchu(loginEmailUser);
                nhanVien.setEmailnv(binding.etEmail.getText().toString());
                String cccd = binding.etCCCD.getText().toString();
                nhanVien.setCccd(cccd);
                uploadAnh();

                // Lưu nhân viên vào Firebase với key
                nhanVien.setCccd(null);
                DatabaseReference dbrfNhanvien = FirebaseDatabase.getInstance().getReference("nhanvien");
                dbrfNhanvien.child(cccd).setValue(nhanVien) //Thêm nv mới vào firebase với Key = nhanVien.getCccd(), các value còn lại tự thêm.
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Thêm nhân viên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        });

                // Tạo tài khoản với mật khẩu ngẫu nhiên và mã hóa
                String userNhanVien = nhanVien.getEmailnv();
                String passwordNhanVien = String.valueOf((int) (Math.random() * 1000000)); // Tạo mật khẩu ngẫu nhiên 6 chữ số
                String hashedPassword = hashPassword(passwordNhanVien); // Mã hóa mật khẩu

                Users usersNhanVienMoi = new Users(userNhanVien, hashedPassword, "nv");

                DatabaseReference dbrfAccount = FirebaseDatabase.getInstance().getReference("account");
                dbrfAccount.child(cccd).setValue(usersNhanVienMoi)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Tạo tài khoản cho nhân viên thành công", Toast.LENGTH_SHORT).show();

                                // Tạo hộp thoại hiển thị thông tin tài khoản và mật khẩu với nút copy
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Thông tin tài khoản nhân viên")
                                        .setMessage("User: " + userNhanVien + "\nPassword: " + passwordNhanVien)
                                        .setPositiveButton("OK", (dialogInterface, i) -> {
                                            getParentFragmentManager().popBackStack(); // Quay lại Fragment trước
                                        });

                                // Thêm nút Copy
                                builder.setNeutralButton("Copy", (dialogInterface, i) -> {
                                    // Sao chép User và Password vào Clipboard
                                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("User & Password", userNhanVien + "\n" + passwordNhanVien);
                                    clipboard.setPrimaryClip(clip);
                                    getParentFragmentManager().popBackStack(); // Quay lại Fragment trước
                                    Toast.makeText(getContext(), "Đã sao chép User và Password vào clipboard", Toast.LENGTH_SHORT).show();
                                });

                                // Hiển thị hộp thoại
                                builder.show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Tạo TK nhân viên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }).setNegativeButton("Không", null).show();
        });
    }

    private void uploadAnh() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("CCCD");
        StorageReference frontImageRef = storageRef.child(nhanVien.getCccd() + "_Truoc" + ".jpg");
        frontImageRef.putFile(anhCCCDTruoc)
                .addOnSuccessListener(taskSnapshot -> frontImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    nhanVien.setAnhcc1(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload ảnh trước thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        StorageReference backImageRef = storageRef.child(nhanVien.getCccd() + "_Sau" + ".jpg");
        backImageRef.putFile(anhCCCDSau)
                .addOnSuccessListener(taskSnapshot -> backImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    nhanVien.setAnhcc2(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload ảnh sau thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Truy xuất FireBase: Lưu ID chức vụ được chọn cho nhân viên
    private void setIDChucVuNhanVien(String tenChucVu) {
        if (tenChucVu == null || tenChucVu.isEmpty()) {
            Log.d("l.e", "docIDChucVuBangTen: Item Spinner.");
            return;
        }
        Log.d("l.e", "listChucVuFireBase: " + listChucVuFireBase.toString());

        // Tìm ID chức vụ có tên trùng khớp trong danh sách
        if (listChucVuFireBase != null) {
            String idChucVu = null;
            for (ChucVu chucVu : listChucVuFireBase) {
                if (chucVu.getTenChucVu().equals(tenChucVu)) {
                    idChucVu = chucVu.getIdChucVu();
                    Log.d("l.e", "docIDChucVuBangTen: Tìm ID chức vụ có tên trùng khớp trong danh sách, ID = " + idChucVu + ", Tên = " + tenChucVu);
                    break;
                }
            }

            if (idChucVu != null) {
                nhanVien.setChucvu(idChucVu);
                Log.d("l.e", "docDuLieuChucVu: ID = " + idChucVu + ", Tên = " + tenChucVu + ", nhanVien.getChucvu() = " + nhanVien.getChucvu());
            } else {
                Log.d("l.e", "Không tìm thấy chức vụ với tên: " + tenChucVu);
            }
        } else Log.d("l.e", "docIDChucVuBangTen: listChucVuFireBase NULL, tên cv: " + tenChucVu);
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

    // CUỐI: BẮT ĐIỀU KIỆN DỮ LIỆU ĐẦU VÀO
    private boolean batDieuKienDuLieuDauVao() {
        if (loginEmailUser == null) {
            // Hiển thị thông báo nếu cần
            Toast.makeText(getContext(), "Email đăng nhập không hợp lệ \nThoát ứng dụng.", Toast.LENGTH_LONG).show();

            // Loại bỏ Fragment khỏi stack nếu đang sử dụng `FragmentManager`
            // requireActivity().getSupportFragmentManager().popBackStack();

            // Thoát ứng dụng
            requireActivity().finishAffinity();
        }


        // kiểm tra có ký tự số trong tên nhân viên
        String tenNhanVien = binding.etTenNhanVien.getText().toString();
        for (int i = 0; i < tenNhanVien.length(); i++) {
            if (Character.isDigit(tenNhanVien.charAt(i))) {
                binding.etTenNhanVien.setError("Vui lòng không nhập số vào tên nhân viên");
                return false;
            }
        }

        if (binding.etTenNhanVien.getText().toString().isEmpty() || binding.etTenNhanVien.getText().toString().length() < 2) {
            binding.etTenNhanVien.setError("Vui lòng nhập đủ họ tên nhân viên");
            return false;
        }

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

    // CUỐI: TRẠNG THÁI NÚT THÊM NHÂN VIÊN
    private void setVisibilitySaveButton(Boolean visibility) {
        if (visibility == false) {
            String text = "Thêm Nhân Viên";
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new StrikethroughSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.btnThemNhanVien.setText(spannableString);
            binding.btnThemNhanVien.setEnabled(false);
            return;
        }

        if (visibility == true) {
            String text = "Thêm Nhân Viên";
            binding.btnThemNhanVien.setText(text);
            binding.btnThemNhanVien.setEnabled(true);
            return;
        }
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