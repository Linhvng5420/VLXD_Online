package com.tdc.vlxdonline.Activity;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.ThongTinChu;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentAdminCuahangDetailBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Admin_CuaHangDetailFragment extends Fragment {
    FragmentAdminCuahangDetailBinding binding;
    String LoginUserID = LoginActivity.accountID;
    String LoginUserEmail = LoginActivity.idUser;
    String cuahangID; // Key Firebase thongtinchu, không phải cccd

    ThongTinChu cuahang;

    // Mã yêu cầu cho việc chọn ảnh
    private static final int PICK_IMAGE_AVATA_ID = 1;
    private static final int PICK_IMAGE_FRONT_ID = 2;
    private static final int PICK_IMAGE_BACK_ID = 3;
    // Uri để lưu trữ đường dẫn đến ảnh được chọn (biến này không lưu lại link ảnh từ firebase tải về)
    private Uri uriAvata, uriAnhCCTruoc, uriAnhCCSau;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cuahangID = getArguments().getSerializable("idCH").toString();
        Log.d("l.d", "[l.d] [l.d] onCreate: getAgruments cuahangID: " + cuahangID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminCuahangDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setTieuDeStatusNav();
        getData();
        setupAuthentButtons();

//        setupCallButton();
//        setupEditButtons();
//        checkPermissions();

        return view;
    }

    private void setTieuDeStatusNav() {
        // Đổi tiêu đề từ Fragment
        if (requireActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Thông Tin Cửa Hàng");
            }
        }
    }

    private void getData() {
        if (cuahangID != null) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("thongtinchu/" + cuahangID);
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        cuahang = dataSnapshot.getValue(ThongTinChu.class);
                        cuahang.setId(cuahangID);
                        Log.d("l.d", "[l.d] [l.d] getData: " + cuahang.toString());

                        if (cuahang != null) {
                            binding.etID.setText(cuahang.getId());
                            binding.etTenKH.setText(cuahang.getTen());
                            binding.etTenCuaHang.setText(dataSnapshot.child("cuahang").getValue(String.class));
                            binding.etSDT.setText(cuahang.getSdt());
                            binding.etEmail.setText(cuahang.getEmail());
                            binding.etCCCD.setText(dataSnapshot.child("cccd").getValue(String.class));
                            binding.etDiaChi.setText(cuahang.getDiaChi());

                            getStatus();
                        } else {
                            Log.d("l.d", "[l.d] Cửa Hàng không tồn tại trong cơ sở dữ liệu.");
                        }
                    } else {
                        Log.d("l.d", "[l.d] Không tìm thấy Cửa Hàng với ID: " + cuahangID);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("l.d", "[l.d] Lỗi truy xuất thông tin Cửa Hàng từ firebase: " + databaseError.getMessage());
                }
            });

            // ẢNH CCCD
            hienthiAnhCCCD();
        } else {
            Log.d("l.d", "[l.d] nhanIDcuahangTuBundle: Lỗi truyền bundle từ fragment qua Detail");
        }
    }

    private void hienthiAnhCCCD() {
        // Lấy dữ liệu của nhân viên từ Firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("thongtinchu/" + cuahangID);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String anhCC1 = dataSnapshot.child("cccdtruoc").getValue(String.class);
                    String anhCC2 = dataSnapshot.child("cccdsau").getValue(String.class);

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

    private void getStatus() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("account/" + cuahangID);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy giá trị từ snapshot
                    Boolean lock = snapshot.child("trangthai/lock").getValue(Boolean.class);
                    Boolean online = snapshot.child("trangthai/online").getValue(Boolean.class);
                    String locktime = snapshot.child("trangthai/locktime").getValue(String.class);
                    String locktype = snapshot.child("trangthai/locktype").getValue(String.class);

                    // Kiểm tra giá trị null của "lock"
                    if (lock == null) {
                        Toast.makeText(binding.getRoot().getContext(), "Lỗi truy xuất Status", Toast.LENGTH_SHORT).show();
                        binding.ivAuthenticated.setVisibility(View.GONE);
                        return;
                    }

                    // Xử lý logic hiển thị hình ảnh dựa vào "locktype"
                    if ("chuaduyet".equals(locktype)) {
                        binding.ivAuthenticated.setImageResource(R.drawable.baseline_fingerprint_24);
                    } else if ("vinhvien".equals(locktype)) {
                        binding.ivAuthenticated.setImageResource(R.drawable.baseline_lock_reset_24);
                        binding.tvAuthenticated.setText("Khóa Vĩnh Viễn");
                        binding.tvAuthenticated.setVisibility(View.VISIBLE);
                    } else if ("tamthoi".equals(locktype)) {
                        binding.ivAuthenticated.setImageResource(R.drawable.baseline_lock_reset_24);

                        // Cập nhật thời gian khóa và hiển thị
                        if (locktime != null) {
                            binding.tvAuthenticated.setText(locktime);
                            binding.tvAuthenticated.setVisibility(View.VISIBLE);
                        } else {
                            binding.ivAuthenticated.setImageResource(android.R.drawable.ic_lock_lock);
                            binding.tvAuthenticated.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        binding.ivAuthenticated.setImageResource(android.R.drawable.ic_lock_lock);
                        binding.tvAuthenticated.setVisibility(View.INVISIBLE);
                    }
                } else
                    Log.d("l.d", "[l.d] Không tìm thấy cua hàng với ID: " + cuahangID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("l.d", "[l.d] Đã xảy ra lỗi khi truy cập database: " + error.getMessage());
            }
        });
    }

    // SỰ KIỆN BUTTONs
    private void setupAuthentButtons() {
        binding.ivAuthenticated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("account/" + cuahangID);
                dbRef.child("trangthai").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            boolean isLocked = snapshot.child("lock").getValue(Boolean.class);
                            String lockType = snapshot.child("locktype").getValue(String.class);

                            if ("chuaduyet".equals(lockType)) {
                                // Nếu trạng thái là "chuaduyet", cập nhật thành xác thực
                                MoKhoaCuaHang(dbRef);
                            } else if ("".equals(lockType)) {
                                // Hiển thị dialog để nhập lý do khóa
                                showLockDialog(dbRef);
                            } else if ("vinhvien".equals(lockType) || "tamthoi".equals(lockType)) {
                                // Hiển thị dialog để mở khóa tài khoản
                                showUnlockDialog(dbRef);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu trạng thái!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Hiển thị dialog nhập lý do khóa
    private void showLockDialog(DatabaseReference dbRef) {
        // Tạo EditText để nhập lý do khóa
        EditText input = new EditText(getContext());
        input.setHint("Nhập lý do khóa...");

        new AlertDialog.Builder(getContext())
                .setTitle("Khóa Tài Khoản")
                .setMessage("Vui lòng nhập lý do khóa tài khoản:")
                .setView(input)
                .setPositiveButton("Tiếp Tục", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    Log.d("l.d", "[l.d] showLockDialog: " + reason);

                    if (!reason.isEmpty()) {
                        DatabaseReference reasonRef = FirebaseDatabase.getInstance().getReference("lydokhoatk/" + cuahangID);
                        reasonRef.setValue(reason).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                showLockTypeDialog(dbRef);
                            } else {
                                Toast.makeText(getContext(), "Lỗi khi lưu lý do khóa!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Lý do khóa không được để trống!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }


    // Hiển thị dialog chọn loại khóa
    private void showLockTypeDialog(DatabaseReference dbRef) {
        String[] options = {"Khóa Vĩnh Viễn", "Khóa Tạm Thời"};
        new AlertDialog.Builder(getContext())
                .setTitle("Chọn Loại Khóa")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Khóa vĩnh viễn
                        lockAccount(dbRef, "vinhvien", "");
                    } else {
                        // Hiển thị danh sách ngày khóa tạm thời
                        showTemporaryLockDialog(dbRef);
                    }
                })
                .show();
    }

    // Hiển thị dialog chọn ngày khóa tạm thời
    private void showTemporaryLockDialog(DatabaseReference dbRef) {
        String[] daysOptions = {"7 ngày", "25 ngày", "30 ngày", "60 ngày", "90 ngày"};
        new AlertDialog.Builder(getContext())
                .setTitle("Chọn Thời Gian Khóa")
                .setItems(daysOptions, (dialog, which) -> {
                    int[] days = {7, 25, 30, 60, 90};
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, days[which]);

                    // Lấy ngày khóa tạm thời
                    String lockTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

                    lockAccount(dbRef, "tamthoi", lockTime);
                })
                .show();
    }

    // Hàm khóa tài khoản
    private void lockAccount(DatabaseReference dbRef, String lockType, String lockTime) {
        dbRef.child("trangthai").child("lock").setValue(true);
        dbRef.child("trangthai").child("locktype").setValue(lockType);
        dbRef.child("trangthai").child("locktime").setValue(lockTime);
        dbRef.child("trangthai").child("online").setValue(false);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("lydokhoatk");
        db.child(cuahangID).setValue("Khóa tài khoản");

        Toast.makeText(getContext(), "Tài khoản đã bị khóa!", Toast.LENGTH_SHORT).show();
    }

    // Hàm mở khóa tài khoản
    private void MoKhoaCuaHang(DatabaseReference dbRef) {
        dbRef.child("trangthai").child("lock").setValue(false);
        dbRef.child("trangthai").child("locktype").setValue("");
        dbRef.child("trangthai").child("locktime").setValue("");
        dbRef.child("trangthai").child("online").setValue(true);

        binding.tvAuthenticated.setVisibility(View.GONE);
        binding.ivAuthenticated.setImageResource(android.R.drawable.ic_lock_lock);

        Toast.makeText(getContext(), "Tài khoản đã Xác Thực!", Toast.LENGTH_SHORT).show();
    }

    // Hiển thị dialog mở khóa
    private void showUnlockDialog(DatabaseReference dbRef) {
        new AlertDialog.Builder(getContext())
                .setTitle("Mở Khóa Tài Khoản")
                .setMessage("Bạn có chắc muốn mở khóa tài khoản này không?")
                .setPositiveButton("Mở Khóa", (dialog, which) -> MoKhoaCuaHang(dbRef))
                .setNegativeButton("Hủy", null)
                .show();
    }

/*
    // Hiển thị Dialog với ListView để liệt kê danh sách chủ cửa hàng
    private void showOwnersDialog(List<String> listOwners) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_xac_thuc, null);

        TextView tv = dialogView.findViewById(R.id.tvTitle);
        TextView btnDong = dialogView.findViewById(R.id.tvDong);
        ListView listView = dialogView.findViewById(R.id.lv_xac_thuc);

        // Chuyển danh sách chủ cửa hàng thành một mảng để đưa vào Dialog
        String[] ownersArray = listOwners.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (ownersArray.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, ownersArray);
            listView.setAdapter(adapter);
        } else {
            tv.setText("Không Có Cửa Hàng Đã Xác Thực");
            listView.setVisibility(View.GONE);
        }

        btnDong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setupCallButton() {
        binding.btnCall.setOnClickListener(view -> {
            // Tạo AlertDialog để xác nhận
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Xác nhận hành động")
                    .setMessage("Bạn muốn gọi hay sao chép số điện thoại?");

            // Nút "Gọi"
            builder.setPositiveButton("Gọi", (dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + cuahang.getSdt()));
                startActivity(intent);
            });

            // Nút "Sao chép"
            builder.setNegativeButton("Sao chép", (dialog, which) -> {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Số điện thoại", cuahang.getSdt());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Đã sao chép số điện thoại", Toast.LENGTH_SHORT).show();
            });

            // Hiển thị dialog
            builder.show();
        });
    }

    private void setupEditButtons() {
        // Khi nhấn nút "Chỉnh sửa"
        binding.btnEdit.setOnClickListener(v -> {
            // Hiển thị các trường để chỉnh sửa
            enableEditingFields();
            // Ẩn nút chỉnh sửa, hiển thị nút hủy và lưu
            binding.btnEdit.setVisibility(View.GONE);
            binding.btnSave.setVisibility(View.VISIBLE);
            binding.btnCancel.setVisibility(View.VISIBLE);

            // Cho phép người dùng chọn ảnh khi nhấn vào các ImageView
            binding.ivAvata.setOnClickListener(v1 -> openImagePicker(PICK_IMAGE_AVATA_ID));
            binding.ivCCCD1.setOnClickListener(v1 -> openImagePicker(PICK_IMAGE_FRONT_ID));
            binding.ivCCCD2.setOnClickListener(v1 -> openImagePicker(PICK_IMAGE_BACK_ID));
        });

        // Khi nhấn nút "Hủy"
        binding.btnCancel.setOnClickListener(v -> {
            // Hiển thị AlertDialog xác nhận
            new AlertDialog.Builder(getContext())
                    .setTitle("Hủy chỉnh sửa")
                    .setMessage("Bạn có chắc chắn muốn hủy thay đổi không?")
                    .setPositiveButton("Hủy", (dialog, which) -> {
                        // Hủy việc chỉnh sửa, hiển thị lại các giá trị cũ và ẩn nút hủy và lưu
                        disableEditingFields();
                        binding.btnEdit.setVisibility(View.VISIBLE);
                        binding.btnSave.setVisibility(View.GONE);
                        binding.btnCancel.setVisibility(View.GONE);

                        // Khóa chọn ảnh
                        binding.ivAvata.setOnClickListener(null);
                        binding.ivCCCD1.setOnClickListener(null);
                        binding.ivCCCD2.setOnClickListener(null);

                        getData();
                    })
                    .setNegativeButton("Quay lại", null)  // Nếu nhấn Quay lại sẽ đóng hộp thoại
                    .show();
        });

        // Khi nhấn nút "Lưu"
        binding.btnSave.setOnClickListener(v -> {
            // Hiển thị AlertDialog xác nhận
            new AlertDialog.Builder(getContext())
                    .setTitle("Lưu thay đổi")
                    .setMessage("Bạn có chắc chắn muốn lưu thay đổi không?")
                    .setPositiveButton("Lưu", (dialog, which) -> {
                        // Lưu thông tin vào Firebase
                        luuVaoFireBase();
                        // Ẩn nút lưu và hủy, hiển thị nút chỉnh sửa
                        binding.btnEdit.setVisibility(View.VISIBLE);
                        binding.btnSave.setVisibility(View.GONE);
                        binding.btnCancel.setVisibility(View.GONE);
                        disableEditingFields();
                    })
                    .setNegativeButton("Quay lại", null)  // Nếu nhấn Quay lại sẽ đóng hộp thoại
                    .show();
        });
    }

    private void enableEditingFields() {
        binding.etTen.setEnabled(true);
        binding.etSDT.setEnabled(true);
        binding.etCCCD.setEnabled(true);
        binding.etDiaChi.setEnabled(true);
        Toast.makeText(getContext(), "Chỉnh sửa thông tin", Toast.LENGTH_SHORT).show();
    }

    private void disableEditingFields() {
        binding.etTen.setEnabled(false);
        binding.etSDT.setEnabled(false);
        binding.etCCCD.setEnabled(false);
        binding.etDiaChi.setEnabled(false);
    }

    private void luuVaoFireBase() {
        // Tạo và hiển thị ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tải ảnh lên...");
        progressDialog.setCancelable(false);  // Ngăn người dùng đóng dialog khi đang tải
        progressDialog.show();

        // Gọi hàm upload ảnh và cập nhật nhanVien sau khi ảnh đã upload
        uploadAnh(() -> {
            // Đóng ProgressDialog sau khi upload hoàn tất
            progressDialog.dismiss();

            String ten = binding.etTen.getText().toString();
            String sdt = binding.etSDT.getText().toString();
            String soCCCD = binding.etCCCD.getText().toString();
            String diaChi = binding.etDiaChi.getText().toString();

            // Cập nhật thông tin vào Firebase
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("customers");
            db.child(idKH).child("ten").setValue(ten);
            db.child(idKH).child("sdt").setValue(sdt);
            db.child(idKH).child("soCCCD").setValue(soCCCD);
            db.child(idKH).child("avata").setValue(cuahang.getAvata());
            db.child(idKH).child("cccdMatTruoc").setValue(cuahang.getCccdMatTruoc());
            db.child(idKH).child("cccdMatSau").setValue(cuahang.getCccdMatSau());
            db.child(idKH).child("diaChi").setValue(diaChi).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Snackbar.make(getView(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }, progressDialog);  // Truyền ProgressDialog vào hàm uploadAnh
    }

    private void setupEditButtonsVisibility() {
        // Hiển thị nút chỉnh sửa nếu idChuLogin trùng với idKH
        if (idLogin.equals(idKH)) {
            binding.btnEdit.setVisibility(View.VISIBLE);
        } else {
            binding.btnEdit.setVisibility(View.GONE);
        }
    }

*//*
    // ẢNH: HÀM ĐỂ HIỂN THỊ ẢNH CC
    private void hienThiAnhCCCD() {
        // Lấy dữ liệu của nhân viên từ Firebase
        DatabaseReference dbNhanVien = FirebaseDatabase.getInstance().getReference("nhanvien");
        dbNhanVien.child(idKH).addListenerForSingleValueEvent(new ValueEventListener() {
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
*//*

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
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("cuahang");

        if (uriAvata != null) {
            StorageReference avataImageRef = storageRef.child(idLogin + "_Avata.jpg");

            avataImageRef.putFile(uriAvata).addOnSuccessListener(taskSnapshot -> avataImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                cuahang.setAvata(uri.toString());
                uploadCount[0]++;  // Tăng biến đếm khi tải lên ảnh trước thành công

                // Kiểm tra nếu cả ba ảnh đều đã tải xong
                if (uploadCount[0] == 3) {
                    onUploadComplete.run();
                }
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Upload ảnh avata thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            // Tăng biến đếm nếu ảnh trước không có
            uploadCount[0]++;
        }
        if (uriAnhCCTruoc != null) {
            StorageReference frontImageRef = storageRef.child(idLogin + "_Truoc.jpg");

            frontImageRef.putFile(uriAnhCCTruoc).addOnSuccessListener(taskSnapshot -> frontImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                cuahang.setCccdMatTruoc(uri.toString());
                uploadCount[0]++;  // Tăng biến đếm khi tải lên ảnh trước thành công

                // Kiểm tra nếu cả ba ảnh đều đã tải xong
                if (uploadCount[0] == 3) {
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
            StorageReference backImageRef = storageRef.child(idLogin + "_Sau.jpg");

            backImageRef.putFile(uriAnhCCSau).addOnSuccessListener(taskSnapshot2 -> backImageRef.getDownloadUrl().addOnSuccessListener(uri2 -> {
                cuahang.setCccdMatSau(uri2.toString());
                uploadCount[0]++;  // Tăng biến đếm khi tải lên ảnh sau thành công

                // Kiểm tra nếu cả ba ảnh đều đã tải xong
                if (uploadCount[0] == 3) {
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
    }*/
}