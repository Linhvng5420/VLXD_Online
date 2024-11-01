package com.tdc.vlxdonline.Activity;

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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.ChucVu;
import com.tdc.vlxdonline.Model.NhanVien;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerNhanvienDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class Owner_NhanVienDetailFragment extends Fragment {
    private FragmentOwnerNhanvienDetailBinding nhanvienDetailBinding;

    //Khai báo DatabaseReference để kết nối với Firebase
    private DatabaseReference databaseReference;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo databaseReference với tên nhánh cần truy cập trong Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("nhanVien");

        // Nó sẽ nằm ở đây, vì giao diện spinner hiển thị chức vụ INVISIBLE
        layTatCaDSChucVuTuFirebase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Gán binding cho layout fragment_owner_nhanvien_detail.xml bằng cách sử dụng phương thức inflate()
        nhanvienDetailBinding = FragmentOwnerNhanvienDetailBinding.inflate(inflater, container, false);
        return nhanvienDetailBinding.getRoot(); // Trả về toàn bộ giao diện của fragment
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
        nhanvienDetailBinding.spinnerChucVu.setAdapter(chucVuAdapter);

        // Lấy danh sách chức vụ từ Firebase và cập nhật vào Spinner
        setEventSpinner();

        // Lấy ID nhân viên từ Bundle rồi truy xuất thông tin nhân viên từ firebase và Hiển thị lên giao diện
        //Set Spinner Item theo Chức Vụ của nhân viên
        nhanIDNhanVienTuBundle();
        hienthiAnhCCCD();

        // Bắt sự kiện các Button
        setupEditButton();
        setupSaveButton();
        setupDeleteButton(selectedIDNhanVien);
        setupCancelButton();
    }

    // LẤY TẤT CẢ DANH SÁCH CHỨC VỤ TỪ FIREBASE THEO THỜI GIAN THỰC
    private void layTatCaDSChucVuTuFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("chucvu");

        databaseReference.addValueEventListener(new ValueEventListener() {
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

    // NHẬN DỮ LIỆU TỪ BUNDLE, TRUY XUẤT FIREBASE VÀ HIỂN THỊ THÔNG TIN LÊN GIAO DIỆN
    private void nhanIDNhanVienTuBundle() {
        // getArguments() trả về Bundle chứa thông tin được truyền từ Fragment trước
        if (getArguments() != null) // Kiểm tra xem Bundle có tồn tại hay không
        {
            // Lấy thông tin nhân viên từ Bundle
            selectedIDNhanVien = getArguments().getSerializable("selectedIDNhanVien").toString();

            // Hiển thị thông tin ID nhân viên lên giao diện
            Toast.makeText(getContext(), "ID Nhân Viên\n" + selectedIDNhanVien, Toast.LENGTH_SHORT).show();
            Log.d("l.d", "nhanIDNhanVienTuBundle: " + selectedIDNhanVien.toString());

            // Lấy thông tin nhân viên từ firebase thông qua ID
            databaseReference = FirebaseDatabase.getInstance().getReference("nhanvien");

            databaseReference.child(selectedIDNhanVien).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Lấy thông tin nhân viên từ firebase và ánh xạ vào đối tượng NhanVien
                        nhanVien = dataSnapshot.getValue(NhanVien.class);
                        nhanVien.setCccd(dataSnapshot.getKey());

                        if (nhanVien != null) {
                            nhanvienDetailBinding.etTenNhanVien.setText(nhanVien.getTennv());
                            nhanvienDetailBinding.etSDT.setText(nhanVien.getSdt());
                            nhanvienDetailBinding.etEmail.setText(nhanVien.getEmailnv());
                            nhanvienDetailBinding.etCCCD.setText(nhanVien.getCccd());

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

        } else {
            Log.d("l.d", "nhanIDNhanVienTuBundle: Lỗi truyền bundle từ fragment qua Detail");
        }
    }

    // TÌM VÀ ĐỌC TÊN CHỨC VỤ TRONG listChucVuFireBase BẰNG ID CHỨC VỤ TRUYỀN VÀO
    private void docTenChucVuBangID(String chucVuId) {
        if (chucVuId == null || chucVuId.isEmpty()) {
            // Nếu chucVuId null hoặc rỗng, hiển thị thông báo lỗi
            nhanvienDetailBinding.etChucVu.setText("Lỗi Database [chucvu], Liên hệ Adminstrator.");
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
                nhanvienDetailBinding.etChucVu.setText(tenChucVu);
                Log.d("l.d", "docDuLieuChucVu: Lấy được chức vụ với ID = " + chucVuId + ", Tên = " + tenChucVu + ", luuLaiTenChucVu = " + luuLaiTenChucVu);
            } else {
                nhanvienDetailBinding.etChucVu.setText("Chức vụ Mã \"" + chucVuId + "\" không có trong CSDL.");
                Log.d("l.d", "Không tìm thấy chức vụ với ID: " + chucVuId);
            }
        } else Log.d("l.d", "docDuLieuChucVu: listChucVuFireBase NULL, idcv: " + chucVuId);
    }

    // BẮT SỰ KIỆN SPINNER
    private void setEventSpinner() {
        nhanvienDetailBinding.spinnerChucVu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        nhanvienDetailBinding.btnChinhSua.setOnClickListener(v -> {
            // Kích hoạt chỉnh sửa cho các trường thông tin
            nhanvienDetailBinding.etTenNhanVien.setEnabled(true);
            nhanvienDetailBinding.etSDT.setEnabled(true);
            nhanvienDetailBinding.etCCCD.setEnabled(true);

            // Hiển thị Spinner chọn chức vụ
            nhanvienDetailBinding.tilChucVu.setVisibility(View.INVISIBLE);
            nhanvienDetailBinding.etChucVu.setVisibility(View.INVISIBLE);
            nhanvienDetailBinding.spinnerChucVu.setVisibility(View.VISIBLE);
            nhanvienDetailBinding.tvChucVu.setVisibility(View.VISIBLE);
            nhanvienDetailBinding.tvChucVu.setText("Chức Vụ");

            // Hiển thị nút Lưu Lại, Xóa, Hủy. Ẩn nút Chỉnh Sửa
            nhanvienDetailBinding.btnLuuLai.setVisibility(View.VISIBLE);
            nhanvienDetailBinding.btnXoa.setVisibility(View.VISIBLE);
            nhanvienDetailBinding.btnHuy.setVisibility(View.VISIBLE);
            nhanvienDetailBinding.btnChinhSua.setVisibility(View.INVISIBLE);
        });
    }

    private void setupCancelButton() {
        nhanvienDetailBinding.btnHuy.setOnClickListener(v -> {
            // Tạo hộp thoại xác nhận
            new AlertDialog.Builder(getContext()).setTitle("Xác Nhận").setMessage("Bạn có chắc chắn muốn hủy thay đổi không?").setPositiveButton("Hủy", (dialog, which) -> {

                        // Vô hiệu hóa các trường chỉnh sửa sau khi hủy
                        nhanvienDetailBinding.etTenNhanVien.setEnabled(false);
                        nhanvienDetailBinding.etSDT.setEnabled(false);
                        nhanvienDetailBinding.etCCCD.setEnabled(false);

                        // Ẩn Spinner và hiển thị TextView cho chức vụ
                        nhanvienDetailBinding.tilChucVu.setVisibility(View.VISIBLE);
                        nhanvienDetailBinding.etChucVu.setVisibility(View.VISIBLE);
                        nhanvienDetailBinding.spinnerChucVu.setVisibility(View.INVISIBLE);
                        nhanvienDetailBinding.tvChucVu.setVisibility(View.INVISIBLE);

                        // Ẩn nút Lưu Lại, Xóa, Hủy và Hiển thị nút Sửa sau khi Hủy
                        nhanvienDetailBinding.btnLuuLai.setVisibility(View.INVISIBLE);
                        nhanvienDetailBinding.btnHuy.setVisibility(View.VISIBLE);
                        nhanvienDetailBinding.btnXoa.setVisibility(View.INVISIBLE);
                        nhanvienDetailBinding.btnHuy.setVisibility(View.INVISIBLE);
                        nhanvienDetailBinding.btnChinhSua.setVisibility(View.VISIBLE);

                        nhanIDNhanVienTuBundle();
                    }).setNegativeButton("Không Hủy", null) // Hiển thị hộp thoại
                    .show();
        });
    }

    private void setupDeleteButton(String idNhanVien) {
        nhanvienDetailBinding.btnXoa.setOnClickListener(view -> {
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
        nhanvienDetailBinding.btnLuuLai.setOnClickListener(v -> {
            // Kiểm tra trước khi hiện xác nhận
            NhanVien nhanVienUpdate = new NhanVien();

            String newSDT = nhanvienDetailBinding.etSDT.getText().toString();
            String newCCCD = nhanvienDetailBinding.etCCCD.getText().toString();

            // Lưu giá trị Chức vụ từ Spinner
            String tenChucVuMoi = nhanvienDetailBinding.spinnerChucVu.getSelectedItem().toString();
            Log.d("l.d", "setupSaveButton: tenChucVuMoi Spinner = " + tenChucVuMoi);
            nhanVienUpdate.setChucvu(docIDChucVuBangTen(tenChucVuMoi));

            nhanVienUpdate.setTennv(nhanvienDetailBinding.etTenNhanVien.getText().toString());
            nhanVienUpdate.setEmailnv(nhanVien.getEmailnv());
            nhanVienUpdate.setEmailchu(nhanVien.getEmailchu());
            nhanVienUpdate.setSdt(nhanVien.getSdt());

            if (batDieuKienDuLieuDauVao(nhanVienUpdate, newSDT, newCCCD) == true && nhanvienDetailBinding.etSDT.getError() == null && nhanvienDetailBinding.etCCCD.getError() == null) {
                // Tạo hộp thoại xác nhận
                new AlertDialog.Builder(getContext()).setTitle("Xác Nhận").setMessage("Bạn có chắc chắn muốn lưu thay đổi không?").setPositiveButton("Có", (dialog, which) -> {

                    // Cập nhật thông tin nhân viên trong Firebase (Realtime Database)
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("nhanvien");
                    dbRef.child(nhanVien.getCccd()).setValue(nhanVienUpdate).addOnSuccessListener(aVoid -> {
                        // Xử lý thành công
                        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                        // Vô hiệu hóa các trường chỉnh sửa sau khi lưu
                        nhanvienDetailBinding.etTenNhanVien.setEnabled(false);
                        nhanvienDetailBinding.etSDT.setEnabled(false);

                        // Ẩn Spinner và hiển thị TextView cho chức vụ
                        nhanvienDetailBinding.tilChucVu.setVisibility(View.VISIBLE);
                        nhanvienDetailBinding.etChucVu.setVisibility(View.VISIBLE);
                        nhanvienDetailBinding.spinnerChucVu.setVisibility(View.INVISIBLE);
                        nhanvienDetailBinding.tvChucVu.setVisibility(View.INVISIBLE);

                        // Ẩn nút Lưu Lại, Xóa, Hủy và Hiển thị nút Sửa sau khi lưu
                        nhanvienDetailBinding.btnLuuLai.setVisibility(View.INVISIBLE);
                        nhanvienDetailBinding.btnHuy.setVisibility(View.VISIBLE);
                        nhanvienDetailBinding.btnXoa.setVisibility(View.INVISIBLE);
                        nhanvienDetailBinding.btnHuy.setVisibility(View.INVISIBLE);
                        nhanvienDetailBinding.btnChinhSua.setVisibility(View.VISIBLE);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).setNegativeButton("Không", null).show();
            }
        });
    }

    // TÌM VÀ ĐỌC ID CHỨC VỤ TRONG listChucVuFireBase BẰNG TÊN CHỨC VỤ TRUYỀN VÀO
    private String docIDChucVuBangTen(String tenChucVu) {
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

    // Hàm để hiển thị ảnh CC
    private void hienthiAnhCCCD() {
        // Lấy dữ liệu của nhân viên từ Firebase
        databaseReference.child(selectedIDNhanVien).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra nếu có dữ liệu
                if (dataSnapshot.exists()) {
                    // Lấy dữ liệu của nhân viên
                    String anhCC1 = dataSnapshot.child("anhcc1").getValue(String.class);
                    String anhCC2 = dataSnapshot.child("anhcc2").getValue(String.class);

                    // Hiển thị hình ảnh
                    Glide.with(getContext())
                            .load(anhCC1) // Tải ảnh từ URL
                            .into(nhanvienDetailBinding.ivCCCD1); // imageViewCC là ID của ImageView trong layout
                    Glide.with(getContext())
                            .load(anhCC2) // Tải ảnh từ URL
                            .into(nhanvienDetailBinding.ivCCCD2); // imageViewCC2 là ID của ImageView trong layout
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("Owner_NhanVienDetail", "Database error: " + databaseError.getMessage());
            }
        });
    }

    // CUỐI: BẮT ĐIỀU KIỆN DỮ LIỆU ĐẦU VÀO
    private boolean batDieuKienDuLieuDauVao(NhanVien nhanVienUpdate, String newSDT, String newCCD) {
        // kiểm tra có ký tự số trong tên nhân viên
        String tenNhanVien = nhanvienDetailBinding.etTenNhanVien.getText().toString();
        for (int i = 0; i < tenNhanVien.length(); i++) {
            if (Character.isDigit(tenNhanVien.charAt(i))) {
                nhanvienDetailBinding.etTenNhanVien.setError("Vui lòng không nhập số vào tên nhân viên");
                return false;
            }
        }

        if (nhanvienDetailBinding.etTenNhanVien.getText().toString().isEmpty()) {
            nhanvienDetailBinding.etTenNhanVien.setError("Vui lòng nhập đủ họ tên nhân viên");
            return false;
        }

        if (nhanvienDetailBinding.etSDT.getText().toString().isEmpty() || nhanvienDetailBinding.etSDT.getText().toString().length() != 10) {
            nhanvienDetailBinding.etSDT.setError("Vui lòng nhập số điện thoại 10 số");
            return false;
        }

        if (nhanvienDetailBinding.etEmail.getText().toString().isEmpty() || !nhanvienDetailBinding.etEmail.getText().toString().contains("@") || !nhanvienDetailBinding.etEmail.getText().toString().contains(".")) {
            nhanvienDetailBinding.etEmail.setError("Vui lòng nhập đúng email");
            return false;
        }

        if (nhanvienDetailBinding.etCCCD.getText().toString().isEmpty() || nhanvienDetailBinding.etCCCD.getText().toString().length() != 10) {
            nhanvienDetailBinding.etCCCD.setError("Vui lòng nhập CCCD (10 số)");
            return false;
        }

//        if (addBinding.spinnerChucVu.getSelectedItemPosition() == 0) {
//            Toast.makeText(getContext(), "Vui lòng chọn chức vụ", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        // Kiểm tra trùng lặp SDT và CCCD
        kiemTraSDTEmailTruocKhiLuu(nhanVienUpdate, newSDT, newCCD);
        Log.d("l.d", "batDieuKienDuLieuDauVao: " + nhanVienUpdate.toString());

        return true;
    }

    private void kiemTraSDTEmailTruocKhiLuu(NhanVien nhanVienUpdate, String newSDT, String newCCCD) {
        DatabaseReference nhanVienRef = FirebaseDatabase.getInstance().getReference("nhanvien");

        nhanVienRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean sdtExists = false;
                boolean cccdExists = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Bỏ qua nhân viên hiện tại
                    String currentId = snapshot.getKey();
                    if (currentId.equals(nhanVienUpdate.getCccd())) {
                        continue;
                    }

                    // Lấy thông tin NhanVien
                    NhanVien nhanVien = snapshot.getValue(NhanVien.class);
                    if (nhanVien != null) {
                        // Kiểm tra trùng lặp sdt và email
                        if (newSDT.equals(nhanVien.getSdt())) {
                            sdtExists = true;
                        }
                        if (newCCCD.equals(nhanVien.getCccd())) {
                            cccdExists = true;
                        }
                    }

                    // Dừng vòng lặp sớm nếu đã phát hiện trùng lặp
                    if (sdtExists || cccdExists) {
                        break;
                    }
                }

                if (sdtExists) {
                    nhanvienDetailBinding.etSDT.setError("Số điện thoại đã được sử dụng");
                    Toast.makeText(getContext(), "Số điện thoại đã được sử dụng.", Toast.LENGTH_SHORT).show();
                }
                if (cccdExists) {
                    nhanvienDetailBinding.etCCCD.setError("CCCD đã được sử dụng");
                    Toast.makeText(getContext(), "CCCD đã được sử dụng.", Toast.LENGTH_SHORT).show();
                }

                // Nếu không có trùng lặp, tiến hành cập nhật dữ liệu
                if (!sdtExists && !cccdExists) {
                    nhanVien.setSdt(newSDT);
                    nhanVien.setCccd(newCCCD);
                    Log.d("l.d", "kiemTraSDTEmailTruocKhiLuu: " + nhanVienUpdate.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi khi kiểm tra trùng lặp: " + databaseError.getMessage());
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

