package com.tdc.vlxdonline.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.tdc.vlxdonline.Model.LyDoKhoaTK;
import com.tdc.vlxdonline.Model.ThongTinChu;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentAdminCuahangDetailBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Admin_CuaHangDetailFragment extends Fragment {
    FragmentAdminCuahangDetailBinding binding;
    String LoginUserID = LoginActivity.accountID;
    String LoginUserEmail = LoginActivity.idUser;
    String cuahangID; // Key Firebase thongtinchu, ID chủ cửa hàng, không phải cccd

    ThongTinChu cuahang;
    String anhCC2, anhCC1;
    ArrayAdapter<LyDoKhoaTK> adapter;
    List<LyDoKhoaTK> lyDoKhoaList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cuahangID = getArguments().getSerializable("idCH").toString();
        Log.d("l.d", " onCreate: getAgruments cuahangID: " + cuahangID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminCuahangDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setTopNavBarTitle();
        getData();

        // Lấy lý do khóa nếu có
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, lyDoKhoaList);
        binding.lvLyDoKhoa.setAdapter(adapter);
        layLyDoBiKhoa();

        setupAuthentButton();
        setupDSSanPhamButton();
        setupCallButton();

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
                                        tvTaiKhoan.setText("Danh sách cửa hàng:");
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
                        } else
                            Snackbar.make(view, "Không tìm thấy thông tin cửa hàng!", BaseTransientBottomBar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
            }
        });

        binding.ivCCCD1.setOnClickListener(v -> showImageView(anhCC1));
        binding.ivCCCD2.setOnClickListener(v -> showImageView(anhCC2));

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
                        Log.d("l.d", " getData: " + cuahang.toString());

                        if (cuahang != null) {
                            binding.etID.setText(cuahang.getId());
                            binding.etTenKH.setText(cuahang.getTen());
                            binding.etTenCuaHang.setText(dataSnapshot.child("cuahang").getValue(String.class));
                            binding.etSDT.setText(cuahang.getSdt());
                            binding.etEmail.setText(cuahang.getEmail());
                            binding.etCCCD.setText(dataSnapshot.child("cccd").getValue(String.class));
                            binding.etDiaChi.setText(cuahang.getDiaChi());

                            getAuthentButtonStatu();
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

    //-BUTTON DUYỆT-KHÓA-MỞ KHÓA
    private void getAuthentButtonStatu() {
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
                        binding.tvAuthenticated.setVisibility(View.GONE);
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
                        binding.ivAuthenticated.setVisibility(View.VISIBLE);
                        binding.ivAuthenticated.setImageResource(R.drawable.baseline_offline_24);
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

    private void setupAuthentButton() {
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
                                Toast.makeText(getContext(), "Cửa hàng đã Duyệt và Online!", Toast.LENGTH_SHORT).show();
                            } else if ("".equals(lockType)) {
                                // Hiển thị dialog chọn loại khóa
                                showLockTypeDialog(dbRef);
                            } else if ("vinhvien".equals(lockType) || "tamthoi".equals(lockType)) {
                                // Hiển thị dialog để mở khóa cửa hàng
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

    private void layLyDoBiKhoa() {
        DatabaseReference dbLyDoKhoa = FirebaseDatabase.getInstance().getReference("lydokhoatk/" + cuahangID);
        dbLyDoKhoa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lyDoKhoaList.clear();
                long countLock = 0;
                long countUnLock = 0;

                // Duyệt qua dữ liệu từ Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy key (ngày) và value (lý do)
                    String key = snapshot.getKey();
                    String lyDo = snapshot.getValue(String.class);

                    // Tạo đối tượng LyDoKhoa và thêm vào danh sách
                    lyDoKhoaList.add(new LyDoKhoaTK(key, lyDo));

                    if (key.indexOf("UL") == -1) {
                        countLock++;
                    } else countUnLock++;
                }
                binding.tvLichSuKhoa.setText("Lịch Sử Khóa (Lock " + countLock + " lần, UnLock " + countUnLock + " lần)");
                Log.d("LyDoKhoa", "Số lượng item: " + lyDoKhoaList.size() + lyDoKhoaList.toString());

                // cập nhật listview
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("LyDoKhoa", "Lỗi khi đọc dữ liệu: " + databaseError.getMessage());
            }
        });
    }

    // Hiển thị dialog chọn loại khóa
    private void showLockTypeDialog(DatabaseReference dbRef) {
        String[] options = {"Khóa Vĩnh Viễn", "Khóa Tạm Thời"};
        new AlertDialog.Builder(getContext()).setTitle("Chọn Loại Khóa").setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Khóa vĩnh viễn
                showLyDoDialog("LVV");
                lockAccount(dbRef, "vinhvien", "");
            } else {
                // Hiển thị danh sách ngày khóa tạm thời
                showDateLockDialog(dbRef);
            }
        }).setNegativeButton("Hủy", null).show();
    }

    // Hiển thị dialog chọn ngày khóa tạm thời
    private void showDateLockDialog(DatabaseReference dbRef) {
        String[] daysOptions = {"7 ngày", "25 ngày", "30 ngày", "60 ngày", "90 ngày"};
        new AlertDialog.Builder(getContext()).setTitle("Chọn Thời Gian Khóa").setItems(daysOptions, (dialog, which) -> {
            //Snackbar.make(getView(), "Đã Chọn ngày khóa: " + daysOptions[which], Toast.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE).show();
            Toast.makeText(getContext(), "Đã Chọn ngày khóa: " + daysOptions[which], Toast.LENGTH_SHORT).show();

            int[] days = {7, 25, 30, 60, 90};
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, days[which]);

            // Lấy ngày khóa tạm thời
            String lockTime = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.getTime());

            // Truyền ngày được lựa chọn trong daysOptions dialog, chỉ lấy số
            String number = daysOptions[which].substring(0, daysOptions[which].indexOf(" ngày"));
            showLyDoDialog("L" + number);
            lockAccount(dbRef, "tamthoi", lockTime);
        }).show();
    }

    // Hiển thị dialog nhập lý do khóa/mở cửa hàng và lưu lý do vào Firebase
    private void showLyDoDialog(String messenger) {
        // Tạo EditText để nhập lý do khóa/mở
        EditText input = new EditText(getContext());
        input.setHint("Cửa Hàng Vi Phạm Chính Sách CTY");
        if (messenger.equals("UL"))
            input.setHint("Cửa Hàng Đã Cam Kết Không Tái Phạm");


        // Hiển thị hộp thoại yêu cầu nhập lý do khóa
        new AlertDialog.Builder(getContext()).setTitle("Nhập Lý Do").setMessage("Vui lòng nhập lý do khóa/mở cửa hàng:").setView(input).setPositiveButton("Tiếp Tục", (dialog, which) -> {
            String lydo = input.getText().toString().trim();

            if (lydo.isBlank())
                lydo = LoginUserID + " - Cửa Hàng Vi Phạm Chính Sách CTY";

            if (messenger.equals("UL"))
                lydo = LoginUserID + " - Cửa Hàng Đã Cam Kết Không Tái Phạm";

            // Lấy thời gian hiện tại làm key
            String key = new SimpleDateFormat("ssmmHH-ddMMyy", Locale.getDefault()).format(new Date());
            key += " " + messenger;

            // Tạo reference tới đúng vị trí trong Firebase
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("lydokhoatk/" + cuahangID);
            db.child(key).setValue(lydo).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(getContext(), "Lỗi khi xửa lý firebase!", Toast.LENGTH_SHORT).show();
                } else
                    Snackbar.make(getView(), "Đã lưu lý do!", Toast.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE).show();
            });
        }).show();
    }

    // Hiển thị dialog mở khóa
    private void showUnlockDialog(DatabaseReference dbRef) {
        new AlertDialog.Builder(getContext()).setTitle("Mở Khóa Cửa Hàng")
                .setMessage("Bạn có chắc muốn mở khóa cửa hàng này không?")
                .setPositiveButton("Mở Khóa", (dialog, which) -> {
                    showLyDoDialog("UL");

                    dbRef.child("trangthai").child("lock").setValue(false);
                    dbRef.child("trangthai").child("locktype").setValue("");
                    dbRef.child("trangthai").child("locktime").setValue("");
                    dbRef.child("trangthai").child("online").setValue(true);

                    binding.ivAuthenticated.setImageResource(android.R.drawable.ic_lock_lock);
                    Log.d("l.d", "showUnlockDialog: Mở khóa cửa hàng");
                }).setNegativeButton("Hủy", null).show();
    }

    // Hàm khóa cửa hàng vĩnh viễn
    private void lockAccount(DatabaseReference dbRef, String lockType, String lockTime) {
        dbRef.child("trangthai").child("lock").setValue(true);
        dbRef.child("trangthai").child("locktype").setValue(lockType);
        dbRef.child("trangthai").child("locktime").setValue(lockTime);
        dbRef.child("trangthai").child("online").setValue(false);
    }

    // Hàm mở khóa cửa hàng
    private void MoKhoaCuaHang(DatabaseReference dbRef) {
        new AlertDialog.Builder(getContext()).setTitle("Xác Nhận").setMessage("Bạn có chắc muốn cho cửa hàng Online?").setPositiveButton("Online", (dialog, which) -> {
            dbRef.child("trangthai").child("lock").setValue(false);
            dbRef.child("trangthai").child("locktype").setValue("");
            dbRef.child("trangthai").child("locktime").setValue("");
            dbRef.child("trangthai").child("online").setValue(true);

            binding.ivAuthenticated.setImageResource(android.R.drawable.ic_lock_lock);
        }).setNegativeButton("Hủy", null).show();
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
                        Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu sản phẩm!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showImageView(String imagePath) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_image_view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ImageView ivZoomedImage = dialog.findViewById(R.id.ivZoomedImage);
        ImageButton btnRotate = dialog.findViewById(R.id.btnRotate);

        // Load ảnh (dùng Glide)
        Glide.with(this).load(imagePath).into(ivZoomedImage);

        btnRotate.setOnClickListener(new View.OnClickListener() {
            private boolean isZoomedIn = false; // Biến kiểm tra trạng thái phóng to

            @Override
            public void onClick(View v) {
                // Kiểm tra trạng thái và thay đổi hành động
                if (isZoomedIn) {
                    // Quay về trạng thái ban đầu (fitcenter)
                    ivZoomedImage.setRotation(0);
                    ivZoomedImage.setScaleType(ImageView.ScaleType.CENTER); // Đảm bảo ảnh phủ kín màn hình
                    isZoomedIn = false;
                } else {
                    // Xoay ảnh 90 độ và làm sao cho ảnh phủ hết màn hình
                    ivZoomedImage.setRotation(90);
                    ivZoomedImage.setScaleType(ImageView.ScaleType.FIT_CENTER); // Đảm bảo ảnh phủ hết màn hình
                    isZoomedIn = true;
                }
            }
        });


        dialog.show();
    }

    private void setupCallButton() {
        binding.btncall.setOnClickListener(view -> {
            // Tạo AlertDialog để xác nhận
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Xác nhận hành động").setMessage("Bạn muốn gọi hay sao chép số điện thoại?");

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

}