package com.tdc.vlxdonline.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
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
    String cuahangID; // Key Firebase thongtinchu, ID chủ cửa hàng, không phải cccd

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
        setTopNavBarTitle();
        getData();

        setupAuthentButtons();
        setupDSSanPhamButton();


        // Bắt sự kiện xem thông tin cửa hàng
        binding.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idChu = cuahangID;
                DatabaseReference referDetailProd = FirebaseDatabase.getInstance().getReference();
                referDetailProd.child("thongtinchu").child(idChu).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String tenChu = snapshot.child("ten").getValue(String.class);
                            String diaChi = snapshot.child("diaChi").getValue(String.class);
                            String email = snapshot.child("email").getValue(String.class);
                            String sdt = snapshot.child("sdt").getValue(String.class);

                            Dialog dialog = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            ScrollView scrollView = new ScrollView(getContext());
                            LinearLayout layout = new LinearLayout(getContext());
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setPadding(20, 20, 20, 20);

                            Button btnClose = new Button(getContext());
                            btnClose.setText("Đóng");
                            btnClose.setTextSize(16);
                            btnClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            layout.addView(btnClose);

                            // Tạo các TextView và cho phép chọn để copy với kiểu chữ đậm
                            TextView tvTenChu = new TextView(getContext());
                            tvTenChu.setText("Tên: " + tenChu);
                            tvTenChu.setTextSize(16);
                            tvTenChu.setPadding(0, 10, 0, 10);
                            tvTenChu.setTextIsSelectable(true);
                            tvTenChu.setTypeface(null, Typeface.BOLD);  // Đặt kiểu chữ đậm
                            layout.addView(tvTenChu);

                            TextView tvDiaChi = new TextView(getContext());
                            tvDiaChi.setText("Địa chỉ: " + diaChi);
                            tvDiaChi.setTextSize(16);
                            tvDiaChi.setPadding(0, 10, 0, 10);
                            tvDiaChi.setTextIsSelectable(true);
                            tvDiaChi.setTypeface(null, Typeface.BOLD);  // Đặt kiểu chữ đậm
                            layout.addView(tvDiaChi);

                            TextView tvEmail = new TextView(getContext());
                            tvEmail.setText("Email: " + email);
                            tvEmail.setTextSize(16);
                            tvEmail.setPadding(0, 10, 0, 10);
                            tvEmail.setTextIsSelectable(true);
                            tvEmail.setTypeface(null, Typeface.BOLD);  // Đặt kiểu chữ đậm
                            layout.addView(tvEmail);

                            TextView tvSdt = new TextView(getContext());
                            tvSdt.setText("Số điện thoại: " + sdt);
                            tvSdt.setTextSize(16);
                            tvSdt.setPadding(0, 10, 0, 10);
                            tvSdt.setTextIsSelectable(true);
                            tvSdt.setTypeface(null, Typeface.BOLD);  // Đặt kiểu chữ đậm
                            layout.addView(tvSdt);

                            referDetailProd.child("thongtinchusdc").child(idChu).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        TextView tvCSKH = new TextView(getContext());
                                        tvCSKH.setText("Phone CSKH:");
                                        tvCSKH.setTextSize(18);
                                        tvCSKH.setTypeface(null, Typeface.BOLD);
                                        tvCSKH.setPadding(0, 15, 0, 10);
                                        tvCSKH.setTextIsSelectable(true);
                                        layout.addView(tvCSKH);

                                        for (DataSnapshot cuaHangSnapshot : dataSnapshot.child("cskh").getChildren()) {
                                            String diaChiCuaHang = cuaHangSnapshot.getValue(String.class);
                                            TextView tvCuaHangItem = new TextView(getContext());
                                            tvCuaHangItem.setText("- " + diaChiCuaHang);
                                            tvCuaHangItem.setTextSize(16);
                                            tvCuaHangItem.setPadding(0, 5, 0, 5);
                                            tvCuaHangItem.setTextIsSelectable(true);
                                            layout.addView(tvCuaHangItem);
                                        }

                                        TextView tvCuaHang = new TextView(getContext());
                                        tvCuaHang.setText("Danh sách cửa hàng:");
                                        tvCuaHang.setTextSize(18);
                                        tvCuaHang.setTypeface(null, Typeface.BOLD);
                                        tvCuaHang.setPadding(0, 15, 0, 10);
                                        tvCuaHang.setTextIsSelectable(true);
                                        layout.addView(tvCuaHang);

                                        for (DataSnapshot cuaHangSnapshot : dataSnapshot.child("cuahang").getChildren()) {
                                            String diaChiCuaHang = cuaHangSnapshot.getValue(String.class);
                                            TextView tvCuaHangItem = new TextView(getContext());
                                            tvCuaHangItem.setText("- " + diaChiCuaHang);
                                            tvCuaHangItem.setTextSize(16);
                                            tvCuaHangItem.setPadding(0, 5, 0, 5);
                                            tvCuaHangItem.setTextIsSelectable(true);
                                            layout.addView(tvCuaHangItem);
                                        }

                                        TextView tvKho = new TextView(getContext());
                                        tvKho.setText("Danh sách kho:");
                                        tvKho.setTypeface(null, Typeface.BOLD);
                                        tvKho.setTextSize(18);
                                        tvKho.setPadding(0, 15, 0, 10);
                                        tvKho.setTextIsSelectable(true);
                                        layout.addView(tvKho);

                                        for (DataSnapshot khoSnapshot : dataSnapshot.child("kho").getChildren()) {
                                            String diaChiKho = khoSnapshot.getValue(String.class);
                                            TextView tvKhoItem = new TextView(getContext());
                                            tvKhoItem.setText("- " + diaChiKho);
                                            tvKhoItem.setTextSize(16);
                                            tvKhoItem.setPadding(0, 5, 0, 5);
                                            tvKhoItem.setTextIsSelectable(true);
                                            layout.addView(tvKhoItem);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                            referDetailProd.child("thongtinchustk").child(idChu).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        TextView tvTaiKhoan = new TextView(getContext());
                                        tvTaiKhoan.setText("Danh sách tài khoản:");
                                        tvTaiKhoan.setTextSize(18);
                                        tvTaiKhoan.setTypeface(null, Typeface.BOLD);
                                        tvTaiKhoan.setPadding(0, 15, 0, 10);
                                        tvTaiKhoan.setTextIsSelectable(true);
                                        layout.addView(tvTaiKhoan);

                                        for (DataSnapshot taiKhoanSnapshot : dataSnapshot.getChildren()) {
                                            String thongTinTaiKhoan = taiKhoanSnapshot.getValue(String.class);
                                            if (thongTinTaiKhoan != null) {
                                                TextView tvTaiKhoanItem = new TextView(getContext());
                                                tvTaiKhoanItem.setText("- " + thongTinTaiKhoan.toUpperCase());
                                                tvTaiKhoanItem.setTextSize(16);
                                                tvTaiKhoanItem.setPadding(0, 5, 0, 5);
                                                tvTaiKhoanItem.setTextIsSelectable(true);
                                                layout.addView(tvTaiKhoanItem);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                            scrollView.addView(layout);
                            dialog.setContentView(scrollView);
                            dialog.show();
                        } else Snackbar.make(view, "Không tìm thấy thông tin cửa hàng!", BaseTransientBottomBar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
            }
        });

        return view;
    }

    //-HIỂN THỊ
    private void setTopNavBarTitle() {
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

                            getButtonStatus();
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

    //-BUTTON DUYỆT-KHÓA-MỞ KHÓA
    private void getButtonStatus() {
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
                    binding.ivAuthenticated.setVisibility(View.VISIBLE);
                    binding.tvAuthenticated.setVisibility(View.VISIBLE);

                    if ("chuaduyet".equals(locktype)) {
                        binding.ivAuthenticated.setVisibility(View.VISIBLE);
                        binding.ivAuthenticated.setImageResource(R.drawable.baseline_fingerprint_24);
                        binding.tvAuthenticated.setText("Chưa Duyệt");
                    } else if ("vinhvien".equals(locktype)) {
                        binding.ivAuthenticated.setVisibility(View.VISIBLE);
                        binding.ivAuthenticated.setImageResource(R.drawable.baseline_lock_reset_24);
                        binding.tvAuthenticated.setText("Khóa Vĩnh Viễn");
                    } else if ("tamthoi".equals(locktype)) {
                        binding.ivAuthenticated.setVisibility(View.VISIBLE);
                        binding.ivAuthenticated.setImageResource(R.drawable.baseline_lock_reset_24);
                        // Cập nhật thời gian khóa và hiển thị
                        if (locktime != null) {
                            binding.tvAuthenticated.setText("Khóa Tới " + locktime);
                        } else {
                            binding.tvAuthenticated.setText("Lock Time Not Found");
                        }
                    } else if (online) {
                        binding.ivAuthenticated.setVisibility(View.VISIBLE);
                        binding.ivAuthenticated.setImageResource(android.R.drawable.ic_lock_lock);
                        binding.tvAuthenticated.setText("Online");
                    } else {
                        binding.ivAuthenticated.setVisibility(View.INVISIBLE);
                        binding.tvAuthenticated.setText("Offline");
                    }
                } else Log.d("l.d", "[l.d] Không tìm thấy cua hàng với ID: " + cuahangID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("l.d", "[l.d] Đã xảy ra lỗi khi truy cập database: " + error.getMessage());
            }
        });
    }

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

        new AlertDialog.Builder(getContext()).setTitle("Khóa Tài Khoản").setMessage("Vui lòng nhập lý do khóa tài khoản:").setView(input).setPositiveButton("Tiếp Tục", (dialog, which) -> {
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
        }).setNegativeButton("Hủy", null).show();
    }

    // Hiển thị dialog chọn loại khóa
    private void showLockTypeDialog(DatabaseReference dbRef) {
        String[] options = {"Khóa Vĩnh Viễn", "Khóa Tạm Thời"};
        new AlertDialog.Builder(getContext()).setTitle("Chọn Loại Khóa").setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Khóa vĩnh viễn
                lockAccount(dbRef, "vinhvien", "");
            } else {
                // Hiển thị danh sách ngày khóa tạm thời
                showTemporaryLockDialog(dbRef);
            }
        }).show();
    }

    // Hiển thị dialog chọn ngày khóa tạm thời
    private void showTemporaryLockDialog(DatabaseReference dbRef) {
        String[] daysOptions = {"7 ngày", "25 ngày", "30 ngày", "60 ngày", "90 ngày"};
        new AlertDialog.Builder(getContext()).setTitle("Chọn Thời Gian Khóa").setItems(daysOptions, (dialog, which) -> {
            int[] days = {7, 25, 30, 60, 90};
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, days[which]);

            // Lấy ngày khóa tạm thời
            String lockTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

            lockAccount(dbRef, "tamthoi", lockTime);
        }).show();
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
        new AlertDialog.Builder(getContext()).setTitle("Xác Nhận Duyệt Tài Khoản Cửa Hàng").setMessage("Bạn có chắc muốn duyệt tài khoản này không?").setPositiveButton("Duyệt", (dialog, which) -> {
            dbRef.child("trangthai").child("lock").setValue(false);
            dbRef.child("trangthai").child("locktype").setValue("");
            dbRef.child("trangthai").child("locktime").setValue("");
            dbRef.child("trangthai").child("online").setValue(true);

            binding.ivAuthenticated.setImageResource(android.R.drawable.ic_lock_lock);

            Toast.makeText(getContext(), "Tài khoản đã Xác Thực!", Toast.LENGTH_SHORT).show();
        }).setNegativeButton("Hủy", null).show();
    }

    // Hiển thị dialog mở khóa
    private void showUnlockDialog(DatabaseReference dbRef) {
        new AlertDialog.Builder(getContext()).setTitle("Mở Khóa Tài Khoản").setMessage("Bạn có chắc muốn mở khóa tài khoản này không?").setPositiveButton("Mở Khóa", (dialog, which) -> MoKhoaCuaHang(dbRef)).setNegativeButton("Hủy", null).show();
    }

    //-BUTTON DANH SÁCH SẢN PHẨM CỦA CỬA HÀNG
    private void setupDSSanPhamButton() {
        binding.btnDSSanPham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final long[] soluongSP = {0};

                //Nếu cửa hàng không có sp thì show dialog
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("products");
                db.orderByChild("idChu").equalTo(cuahangID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            soluongSP[0] = snapshot.getChildrenCount();
                            Toast.makeText(getContext(), "Cửa hàng hiện có " + soluongSP[0] + " sản phẩm", Toast.LENGTH_SHORT).show();

                            // chuyển sang fragment danh sách sản phẩm
                            Bundle bundle = new Bundle();
                            bundle.putString("idCH", cuahangID);
                            Admin_CuaHangSanPhamFragment sanPhamFragment = new Admin_CuaHangSanPhamFragment();
                            sanPhamFragment.setArguments(bundle);
                            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, sanPhamFragment).addToBackStack(null).commit();
                        } else
                            Toast.makeText(getContext(), "Cửa hàng không có sản phẩm", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar.make(getView(), "Lỗi khi lấy dữ liệu sản phẩm!", Toast.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE).show();
                    }
                });
            }
        });
    }
}