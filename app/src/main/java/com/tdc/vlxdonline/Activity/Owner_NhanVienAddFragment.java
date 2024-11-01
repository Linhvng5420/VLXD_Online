package com.tdc.vlxdonline.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.ChucVu;
import com.tdc.vlxdonline.Model.NhanVien;
import com.tdc.vlxdonline.Model.Users;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerNhanVienAddBinding;

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
        //layTatCaDSChucVuTuFirebase();
        setEventSpinner();

        // Bắt sự kiện các Button
        setupAddButton();

        // Bắt sự kiện nhập dữ liệu đầu vào
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

            // Hộp thoại xác nhận
            new AlertDialog.Builder(getContext()).setTitle("Xác nhận").setMessage("Bạn có muốn thêm nhân viên này?").setPositiveButton("Có", (dialog, which) -> {
                // Lưu giá trị Chức vụ từ Spinner
                String tenChucVuMoi = binding.spinnerChucVu.getSelectedItem().toString();
                Log.d("l.e", "setupSaveButton: tenChucVuMoi Spinner = " + tenChucVuMoi);
                docIDChucVuBangTen(tenChucVuMoi);

                // Tạo mã nhân viên mới bằng timestamp
                long timestamp = System.currentTimeMillis();
                String maNhanVien = "nv" + timestamp;

                nhanVien.setIdnv(maNhanVien);
                nhanVien.setTennv(binding.etTenNhanVien.getText().toString());
                nhanVien.setSdt(binding.etSDT.getText().toString());
                nhanVien.setEmailchu(loginEmailUser);
                nhanVien.setEmailnv(binding.etEmail.getText().toString());
                nhanVien.setCccd(binding.etCCCD.getText().toString());

                // Lưu nhân viên vào Firebase với key
                DatabaseReference dbrfNhanvien = FirebaseDatabase.getInstance().getReference("nhanvien");
                dbrfNhanvien.child(maNhanVien).setValue(nhanVien) //Thêm nv mới vào firebase với Key(document)=idnv, các value còn lại tự thêm.
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                            //getParentFragmentManager().popBackStack(); // Quay lại Fragment trước
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Thêm nhân viên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                // Thêm nhân viên vào Firebase với mã ngẫu nhiên (Dài và xấu)
                // databaseReference.push().setValue(nhanVien);

                // Tạo tài khoản mật khẩu 6 số ngẫu nhiên cho nhân viên
                String userNhanVien = nhanVien.getEmailnv();
                String passwordNhanVien = String.valueOf((int) (Math.random() * 1000000)); // ép kiểu int để chỉ lấy số nguyên
                Users usersNhanVienMoi = new Users(userNhanVien, passwordNhanVien, "nv");

                DatabaseReference dbrfAccount = FirebaseDatabase.getInstance().getReference("account");
                dbrfAccount.child(maNhanVien).setValue(usersNhanVienMoi)
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

    // Truy xuất FireBase: Lưu ID chức vụ được chọn cho nhân viên
    private void docIDChucVuBangTen(String tenChucVu) {
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