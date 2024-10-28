package com.tdc.vlxdonline.Activity;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.ChucVu;
import com.tdc.vlxdonline.Model.NhanVien;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerNhanVienAddBinding;

import java.util.ArrayList;
import java.util.List;

public class Owner_NhanVienAddFragment extends Fragment {
    FragmentOwnerNhanVienAddBinding addBinding;

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
        addBinding = FragmentOwnerNhanVienAddBinding.inflate(inflater, container, false);
        return addBinding.getRoot();
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
        addBinding.spinnerChucVu.setAdapter(chucVuAdapter);

        // Lấy danh sách chức vụ từ Firebase và cập nhật vào Spinner
        //layTatCaDSChucVuTuFirebase();
        setEventSpinner();

        // Bắt sự kiện các Button
        setupSaveButton();

        // Bắt lỗi trong khi nhập email
        addBinding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addBinding.btnThemNhanVien.setEnabled(false);

                // Kiểm tra định dạng email mỗi khi có thay đổi
                String email = s.toString();
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == true) // Kiểm tra định dạng Email
                {
                    // Bắt đầu kiểm tra tính duy nhất nếu email hợp lệ
                    checkEmailUniqueness(email);
                } else {
                    addBinding.btnThemNhanVien.setEnabled(false);
                    addBinding.etEmail.setError("Email không hợp lệ");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Bắt lỗi trong khi nhập SDT
        addBinding.etSDT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addBinding.btnThemNhanVien.setEnabled(false);
                String vietnamPhoneRegex = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$";

                // Kiểm tra định dạng sdt mỗi khi có thay đổi
                String phone = s.toString();
                if (phone.matches(vietnamPhoneRegex) == true) // Kiểm tra định dạng phone
                {
                    // Bắt đầu kiểm tra tính duy nhất nếu phone hợp lệ
                    checkEmailUniqueness(phone);
                } else {
                    addBinding.btnThemNhanVien.setEnabled(false);
                    addBinding.etSDT.setError("Phone không hợp lệ");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        addBinding.etTenNhanVien.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (addBinding.etTenNhanVien.getText().toString().isEmpty()) {
                    addBinding.btnThemNhanVien.setEnabled(false);
                    addBinding.etTenNhanVien.setError("Vui lòng nhập đủ họ tên nhân viên");
                } else addBinding.btnThemNhanVien.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
        addBinding.spinnerChucVu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void setupSaveButton() {
        addBinding.btnThemNhanVien.setOnClickListener(v -> {
            // Bắt điều kiện dữ liệu đầu vào
            if (!batDieuKienDuLieuDauVao()) return;

            // Hộp thoại xác nhận
            new AlertDialog.Builder(getContext())
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có muốn thêm nhân viên này?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        // Lưu giá trị Chức vụ từ Spinner
                        String tenChucVuMoi = addBinding.spinnerChucVu.getSelectedItem().toString();
                        Log.d("l.e", "setupSaveButton: tenChucVuMoi Spinner = " + tenChucVuMoi);

                        docIDChucVuBangTen(tenChucVuMoi);

                        nhanVien.setTennv(addBinding.etTenNhanVien.getText().toString());
                        nhanVien.setSdt(addBinding.etSDT.getText().toString());
                        nhanVien.setEmailchu(loginEmailUser);
                        nhanVien.setCccd(addBinding.etCCCD.getText().toString());

                        // Tạo mã nhân viên mới bằng timestamp
                        long timestamp = System.currentTimeMillis();
                        String maNhanVien = "nv" + timestamp;

                        // Lưu nhân viên vào Firebase với key
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("nhanvien");
                        databaseReference.child(maNhanVien).setValue(nhanVien) //Thêm nv mới vào firebase với Key(document)=idnv, các value còn lại tự thêm.
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                                    getParentFragmentManager().popBackStack(); // Quay lại Fragment trước
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Thêm nhân viên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                        // Thêm nhân viên vào Firebase với mã ngẫu nhiên (Dài và xấu)
                        // databaseReference.push().setValue(nhanVien);
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    // Phương thức kiểm tra tính duy nhất của addEmail
    private void checkEmailUniqueness(String addEmail) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("account");
        databaseReference.orderByChild("mail").equalTo(addEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            addBinding.etEmail.setError("Email đã tồn tại");
                        } else {
                            addBinding.btnThemNhanVien.setEnabled(true);
                            addBinding.etEmail.setError(null); // Xóa lỗi nếu addEmail hợp lệ
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("l.d", "Lỗi khi kiểm tra addEmail: " + error.getMessage());
                    }
                });
    }

    // Phương thức kiểm tra tính duy nhất của phone
    private void checkPhoneUniqueness(String phone) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("nhanvien");
        databaseReference.orderByChild("sdt").equalTo(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            addBinding.etSDT.setError("SĐT đã tồn tại");
                        } else {
                            addBinding.etSDT.setError(null); // Xóa lỗi nếu phone hợp lệ
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("l.d", "checkPhoneUniqueness: Lỗi khi kiểm tra SĐT: " + error.getMessage());
                    }
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
        } else
            Log.d("l.e", "docIDChucVuBangTen: listChucVuFireBase NULL, tên cv: " + tenChucVu);
    }

    // CUỐI: BẮT ĐIỀU KIỆN DỮ LIỆU ĐẦU VÀO
    private boolean batDieuKienDuLieuDauVao() {
        if (loginEmailUser == null) {
            // Hiển thị thông báo nếu cần
            Toast.makeText(getContext(), "Email đăng nhập không hợp lệ \nThoát ứng dụng.", Toast.LENGTH_LONG).show();

            // Loại bỏ Fragment khỏi stack nếu đang sử dụng `FragmentManager`
//            requireActivity().getSupportFragmentManager().popBackStack();

            // Thoát ứng dụng
            requireActivity().finishAffinity();
        }


        // kiểm tra có ký tự số trong tên nhân viên
        String tenNhanVien = addBinding.etTenNhanVien.getText().toString();
        for (int i = 0; i < tenNhanVien.length(); i++) {
            if (Character.isDigit(tenNhanVien.charAt(i))) {
                addBinding.etTenNhanVien.setError("Vui lòng không nhập số vào tên nhân viên");
                return false;
            }
        }

        if (addBinding.etTenNhanVien.getText().toString().isEmpty() || addBinding.etTenNhanVien.getText().toString().length() < 2) {
            addBinding.etTenNhanVien.setError("Vui lòng nhập đủ họ tên nhân viên");
            return false;
        }

        if (addBinding.etSDT.getText().toString().isEmpty() || addBinding.etSDT.getText().toString().length() != 10) {
            addBinding.etSDT.setError("Vui lòng nhập số điện thoại 10 số");
            return false;
        }

        if (addBinding.etEmail.getText().toString().isEmpty() || !addBinding.etEmail.getText().toString().contains("@") || !addBinding.etEmail.getText().toString().contains(".")) {
            addBinding.etEmail.setError("Vui lòng nhập đúng email");
            return false;
        }

        if (addBinding.etCCCD.getText().toString().isEmpty() || addBinding.etCCCD.getText().toString().length() != 10) {
            addBinding.etCCCD.setError("Vui lòng nhập CCCD (10 số)");
            return false;
        }

//        if (addBinding.etPass.getText().toString().isEmpty() || addBinding.etPass.getText().toString().length() < 6) {
//            addBinding.etPass.setError("Vui lòng nhập mật khẩu (từ 6 ký tự)");
//            return false;
//        }

//        if (addBinding.spinnerChucVu.getSelectedItemPosition() == 0) {
//            Toast.makeText(getContext(), "Vui lòng chọn chức vụ", Toast.LENGTH_SHORT).show();
//            return false;
//        }

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