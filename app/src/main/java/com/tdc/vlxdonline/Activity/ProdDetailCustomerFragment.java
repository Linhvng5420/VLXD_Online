package com.tdc.vlxdonline.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.ImageAdapter;
import com.tdc.vlxdonline.Model.AnhSanPham;
import com.tdc.vlxdonline.Model.CartItem;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentProdDetailCustomerBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class ProdDetailCustomerFragment extends Fragment {
    FragmentProdDetailCustomerBinding binding;
    private String idKhach;
    // Id product duoc chon
    private String idProd = "";
    private Products prod;
    private int soLuong = 1;
    // Danh sach anh mo ta cua san pham dc chon
    ArrayList<String> dataAnh = new ArrayList<>();
    ImageAdapter imageAdapter;

    private DatabaseReference referDetailProd;
    ValueEventListener event;

    public ProdDetailCustomerFragment(String idProduct) {
        idProd = idProduct;

        // TODO 1 NGVlinh: Admin đăng nhập, viết dòng này để tránh UD Crash
        if (Customer_HomeActivity.info == null) {
            idKhach = "N/A";
        } else
            // Khách hàng đang đăng nhập
            idKhach = Customer_HomeActivity.info.getID();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Admin đọc ds Khách Hàng Khiếu Nại về sản phẩm hiện tại
        if (Customer_HomeActivity.info == null)
            Admin_KhachHangKhieuNai();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProdDetailCustomerBinding.inflate(inflater, container, false);

        // TODO 2 NGVlinh: Admin đăng nhập, ẩn các nút mua, giỏ hàng, đánh giá
        if (Customer_HomeActivity.info == null) {
            idKhach = "N/A";
            binding.btnDatHangNgay.setText("Xem Khiếu Nại Của Khách Hàng");
            binding.btnDatHangNgay.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            binding.btnDatHangNgay.setTextColor(Color.WHITE);
            binding.btnDatHangNgay.setBackgroundColor(Color.RED);
            binding.lnGioHang.setVisibility(View.INVISIBLE);
        }

        // Khách hàng đăng nhập
        setAdapterAnh();
        setUpDisplay();

        // TODO NGVLinh: Bắt sự kiện xem thông tin cửa hàng hoặc xem sản phẩm của cửa hàng
        binding.tvCuaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hiển thị dialog chọn xem thông tin cửa hàng hoặc sản phẩm của cửa hàng
                new AlertDialog.Builder(getContext())
                        .setTitle(binding.tvCuaHang.getText().toString()).setPositiveButton("Xem Thông Tin", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                HienThiThongTinCuaHang();
                            }
                        }).setNeutralButton("Xem Sản Phẩm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Tạo bundle để chuyển sang trang danh sách sản phẩm của cửa hàng Admin_CuaHangSanPhamFragment
                                Bundle bundle = new Bundle();
                                bundle.putString("idCH", prod.getIdChu());
                                Admin_CuaHangSanPhamFragment fragment = new Admin_CuaHangSanPhamFragment();
                                fragment.setArguments(bundle);
                                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                            }
                        }).setIcon(R.drawable.baseline_store_24).show();
            }
        });

        // TODO 3 NGVLinh: Đặt Hàng Ngay(KH) / Admin đọc ds Khách Hàng Khiếu Nại về sản phẩm hiện tại
        binding.btnDatHangNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Customer_HomeActivity.info != null) {
                    if (soLuong > 0)
                        ((Customer_HomeActivity) getActivity()).ReplaceFragment(new DatHangNgayFragment(idProd, soLuong));
                    else
                        Toast.makeText(getActivity(), "Hiện Tại Sản Phẩm Đã Bán Hết!", Toast.LENGTH_SHORT).show();
                } else {
                    Admin_KhachHangKhieuNai();
                }
            }
        });

        // TODO Thiên: Khiếu Nại
        binding.btnKhieunai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KH_ShowDialogKhieuNai();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Su Kien Tang Giam SL
        binding.btnGiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soLuong > 1) {
                    binding.edtSoLuong.setText(soLuong - 1 + "");
                }
                checkSoLuong();
            }
        });
        binding.btnTang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.edtSoLuong.setText(soLuong + 1 + "");
                checkSoLuong();
            }
        });
        // Su kien nhap sl
        binding.edtSoLuong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!binding.edtSoLuong.getText().toString().isEmpty()) {
                    checkSoLuong();
                } else {
                    binding.edtSoLuong.setText("1");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // Su kien Add Cart
        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
        // Xem danh gia
        binding.lnXemDg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Nếu Customer_HomeActivity đang chạy
                if (getActivity() instanceof Customer_HomeActivity) {
                    ((Customer_HomeActivity) getActivity()).ReplaceFragment(new DaDanhGiaFragment(1, idProd));
                } else {
                    DaDanhGiaFragment fragment = new DaDanhGiaFragment(1, idProd);
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
            }
        });
    }

    private void addToCart() {
        referDetailProd.child("carts").child(idKhach).child(idProd).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    CartItem temp = snapshot.getValue(CartItem.class);
                    int soLuongLuu = 0;
                    if (temp != null) soLuongLuu = soLuong + temp.getSoLuong();
                    else soLuongLuu = soLuong;

                    int tk = Integer.parseInt(prod.getTonKho());
                    if (soLuongLuu > tk) {
                        soLuongLuu = tk;
                        Toast.makeText(getActivity(), "Đã Điều Chỉnh Số Lượng Phù Hợp Với Số Sản Phẩm Tồn Kho!", Toast.LENGTH_LONG).show();
                    }

                    referDetailProd.child("carts").child(idKhach).child(idProd).child("soLuong").setValue(soLuongLuu);
                    Toast.makeText(getActivity(), "Đã Thêm Vào Giỏ Hàng!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkSoLuong() {
        soLuong = Integer.parseInt(binding.edtSoLuong.getText().toString());
        int kho = Integer.parseInt(prod.getTonKho());
        if (soLuong > kho) {
            Toast.makeText(getActivity(), "Số Lượng Bạn Nhập Lớn Hơn Tồn Kho!", Toast.LENGTH_SHORT).show();
            soLuong = kho;
            binding.edtSoLuong.setText(soLuong + "");
        }
    }

    private void setUpDisplay() {
        referDetailProd = FirebaseDatabase.getInstance().getReference();
        readProdFromDatabase();
        setHienThiAnh();
    }

    private void setAdapterAnh() {
        // Adapter Anh Mo Ta
        imageAdapter = new ImageAdapter(getActivity(), dataAnh);
        imageAdapter.setOnItemImageClick(new ImageAdapter.OnItemImageClick() {
            @Override
            public void onItemClick(int position) {
                Glide.with(getActivity()).load(dataAnh.get(position)).into(binding.imgDetail);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rcAnhSp.setLayoutManager(linearLayoutManager);
        binding.rcAnhSp.setAdapter(imageAdapter);
    }

    private void setHienThiAnh() {
        referDetailProd.child("ProdImages").child(idProd).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dataAnh.clear(); // Xóa danh sách cũ trước khi cập nhật

                    int itemCount = (int) dataSnapshot.getChildrenCount();
                    AtomicInteger atomicInteger = new AtomicInteger(0);
                    // Duyệt qua từng User trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AnhSanPham image = snapshot.getValue(AnhSanPham.class);
                        dataAnh.add(image.getAnh());
                        if (atomicInteger.incrementAndGet() == itemCount)
                            imageAdapter.notifyDataSetChanged();
                    }

                    Glide.with(getActivity()).load(dataAnh.get(0)).into(binding.imgDetail);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readProdFromDatabase() {
        // Kiểm tra nếu event chưa được khởi tạo
        if (event == null) {
            event = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        // Lấy thông tin sản phẩm từ snapshot và gán vào biến Products
                        Products product = dataSnapshot.getValue(Products.class);
                        if (product != null) {
                            // Gán sản phẩm vào biến toàn cục 'prod'
                            prod = product;

                            // Hiển thị hình ảnh của sản phẩm sử dụng Glide
                            Glide.with(getActivity()).load(product.getAnh()).into(binding.ivAnhChinh);

                            // Hiển thị thông tin chi tiết của sản phẩm trên giao diện
                            binding.tvTenSpDetail.setText(product.getTen());
                            binding.tvGiaSpDetail.setText(chuyenChuoi(product.getGiaBan()) + " VND");
                            binding.tvTonKhoDetail.setText("Kho: " + product.getTonKho());
                            binding.tvSoSao.setText(product.getSoSao());

                            // Kiểm tra số lượng tồn kho, nếu bằng 0 thì đặt soLuong = 0
                            if (product.getTonKho().equals("0")) {
                                soLuong = 0;
                            }
                            // Hiển thị số lượng sản phẩm
                            binding.edtSoLuong.setText(soLuong + "");
                            binding.tvDaBanDetail.setText("Đã Bán: " + product.getDaBan());
                            binding.tvDonViDetail.setText(product.getDonVi());
                            binding.tvMoTaDetail.setText(product.getMoTa());
                        } else {
                            // Hiển thị thông báo khi sản phẩm đã bị xóa
                            ShowWar();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    } catch (Exception e) {
                        // Bắt lỗi và hiển thị thông báo khi xảy ra vấn đề
                        Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
                    }

                    // TODO: Hiển thị tên cửa hàng từ dữ liệu của chủ sản phẩm
                    if (prod != null) {
                        String idChu = prod.getIdChu(); // Lấy id của chủ sản phẩm
                        referDetailProd = FirebaseDatabase.getInstance().getReference();
                        referDetailProd.child("thongtinchu").child(idChu).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    // Kiểm tra nếu binding và dữ liệu tồn tại
                                    if (binding != null && snapshot.exists()) {
                                        // Lấy tên chủ cửa hàng từ snapshot
                                        String tenChu = snapshot.child("ten").getValue(String.class);
                                        // Hiển thị tên cửa hàng trên giao diện
                                        binding.tvCuaHang.setText("Cửa Hàng " + tenChu);
                                        binding.tvCuaHang.setVisibility(View.VISIBLE);
                                    }
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Xử lý khi việc lấy dữ liệu bị hủy (nếu cần)
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        }

        // Lấy tham chiếu đến dữ liệu sản phẩm và thêm lắng nghe sự kiện
        referDetailProd.child("products").child(idProd).addValueEventListener(event);
    }

    // Ham them dau cham cho gia ban
    private StringBuilder chuyenChuoi(String soTien) {
        StringBuilder chuoi = new StringBuilder(soTien);
        if (chuoi.length() > 3) {
            int dem = 0;
            int doDai = chuoi.length() - 1;
            for (int i = doDai; i > 0; i--) {
                dem = dem + 1;
                if (dem == 3) {
                    chuoi.insert(i, '.');
                    dem = 0;
                }
            }
        }
        return chuoi;
    }

    // Admin đọc ds Khách Hàng Khiếu Nại về sản phẩm hiện tại
    private void Admin_KhachHangKhieuNai() {
        // Hiển thị danh sách khiếu nại của khách hàng trong dialog
        DatabaseReference dbKhieuNai_LyDo = FirebaseDatabase.getInstance().getReference("khieunai/" + idProd);
        dbKhieuNai_LyDo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Tạo danh sách để hiển thị khiếu nại
                    List<String> complaintList = new ArrayList<>();
                    List<String> customerKeys = new ArrayList<>(); // Danh sách mã khách hàng

                    // Lấy dữ liệu từ Firebase
                    for (DataSnapshot snapshot : dataSnapshot.child("lydo").getChildren()) {
                        String customerId = snapshot.getKey(); // Mã khách hàng
                        String complaint = snapshot.getValue(String.class); // Lời khiếu nại

                        if (customerId != null && complaint != null) {
                            complaintList.add("[" + customerId + "] " + complaint);
                            customerKeys.add(customerId);
                        }
                    }

                    // Nếu không có khiếu nại
                    if (complaintList.isEmpty()) {
                        complaintList.add("Không có khiếu nại nào.");
                    }

                    // Hiển thị dialog với ListView
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Danh sách khiếu nại");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, complaintList);

                    builder.setAdapter(adapter, (dialog, which) -> {
                        if (which < customerKeys.size()) {
                            String selectedCustomerId = customerKeys.get(which);
                            // Chuyển sang trang thông tin chi tiết của khách hàng
                            Bundle bundle = new Bundle();
                            bundle.putString("idKH", selectedCustomerId); // Gửi mã khách hàng qua bundle

                            Owner_KhachHangDetailFragment fragment = new Owner_KhachHangDetailFragment();
                            fragment.setArguments(bundle);

                            // Mở fragment chi tiết và tắt thanh điều hướng của fragment đó
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });

                    // Xóa SP Vi Phạm
                    builder.setPositiveButton("Xóa Sản Phẩm", (dialog, which) -> {
                        // Tạo EditText để nhập lý do khóa/mở
                        EditText input = new EditText(getContext());
                        input.setHint("Sản Phẩm Vi Phạm Chính Sách CTY");

                        // Hiển thị hộp thoại yêu cầu nhập lý do xóa
                        new AlertDialog.Builder(getContext())
                                .setTitle("Xóa Sản Phẩm Vi Phạm")
                                .setMessage("Nhập Lý Do Xóa Sản Phẩm Vi Phạm")
                                .setView(input)
                                .setPositiveButton("Xóa", (dialog1, which1) -> {
                                    String lydo = input.getText().toString().trim();

                                    // Nếu lý do trống, gán lý do mặc định
                                    if (lydo.isBlank()) {
                                        lydo = LoginActivity.accountID + " - Sản Phẩm Vi Phạm Chính Sách CTY";
                                    }

                                    // Lấy thời gian hiện tại làm key
                                    String key = new SimpleDateFormat("ssmmHH-ddMMyy", Locale.getDefault()).format(new Date());
                                    key += " XSP_" + prod.getId();

                                    // Lưu lý do vào Firebase
                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("lydokhoatk/" + prod.getIdChu());
                                    db.child(key).setValue(lydo).addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Lỗi khi xử lý Firebase!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Snackbar.make(getView(), "Đã lưu lý do!", Toast.LENGTH_SHORT)
                                                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                                                    .show();
                                        }
                                    });

                                    // Tiến hành xóa sản phẩm trong Firebase
                                    DatabaseReference dbSanPham = FirebaseDatabase.getInstance().getReference("products/" + idProd);
                                    dbSanPham.removeValue().addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            DatabaseReference dbStore = FirebaseDatabase.getInstance().getReference();
                                            dbStore.child("ProdImages").child(idProd).removeValue(); // Xóa ảnh trong bảng "ProdImages"

                                            // Xóa khiếu nại liên quan đến sản phẩm
                                            dbKhieuNai_LyDo.removeValue();

                                            // Quay lại màn hình trước và thông báo xóa thành công
                                            getActivity().getSupportFragmentManager().popBackStack();
                                            Snackbar.make(getView(), "Xóa sản phẩm thành công", Snackbar.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(), "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                })
                                .setNegativeButton("Hủy", null)
                                .show();
                    });

                    // Kiểm tra xem khiếu nại của sp đã đc xem xét hay chưa
                    Boolean daxem = dataSnapshot.child("daxem").getValue(Boolean.class);
                    if (!daxem) {
                        builder.setNeutralButton("Tiếp Nhận Khiếu Nại", (dialog, which) -> {
                            dbKhieuNai_LyDo.child("daxem").setValue(true);
                            Snackbar.make(binding.getRoot(), "Admin Đã Xem Và Ghi Nhận Khiếu Nại", Snackbar.LENGTH_SHORT).show();
                        });
                    }

                    builder.setPositiveButton("Đóng", null).show();

                } else {
                    binding.btnDatHangNgay.setText("Không Có Khiếu Nại");
                    Snackbar.make(binding.getRoot(), "Sản Phẩm Hiện Không có khiếu nại nào.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu khiếu nại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Khách Hàng Khiếu Nại
    private void KH_ShowDialogKhieuNai() {
        // Tạo EditText để nhập lý do
        EditText input = new EditText(getContext());
        input.setHint("Nhập lý do khiếu nại");
        input.setPadding(20, 20, 20, 20);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Khiếu Nại").setView(input).setPositiveButton("Gửi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String reason = input.getText().toString().trim();
                if (!reason.isEmpty()) {
                    String lydo = input.getText().toString().trim();

                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("khieunai/" + idProd);
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int solan = dataSnapshot.child("solan").getValue(Integer.class);
                                dbRef.child("solan").setValue(solan + 1);
                            } else {
                                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                dbRef.child("idchu").setValue(prod.getIdChu());
                                dbRef.child("idsp").setValue(prod.getId());
                                dbRef.child("lydo/" + idKhach).setValue(date + ": " + lydo);
                                dbRef.child("solan").setValue(1);
                                dbRef.child("daxem").setValue(false);
                            }

                            Snackbar.make(binding.getRoot(), "Đã gửi khiếu nại: " + reason, Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("FirebaseComplaint", "Lỗi khi đọc dữ liệu: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Vui lòng nhập lý do khiếu nại!", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Hủy", null).create().show();
    }

    private void ShowWar() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Thông Báo!").setMessage("Sản Phẩm Bạn Chọn Đã Bị Xóa!");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Drawable drawableIcon = getResources().getDrawable(android.R.drawable.ic_delete);
        drawableIcon.setTint(Color.RED);
        builder.setIcon(drawableIcon);
        Drawable drawableBg = getResources().getDrawable(R.drawable.bg_item_lg);
        drawableBg.setTint(Color.rgb(100, 220, 255));
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(drawableBg);
        alertDialog.show();
    }

    private void HienThiThongTinCuaHang() {
        String idChu = prod.getIdChu();
        referDetailProd = FirebaseDatabase.getInstance().getReference();
        referDetailProd.child("thongtinchu").child(idChu).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
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
                                try {
                                    if (dataSnapshot.exists()) {
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
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        referDetailProd.child("thongtinchustk").child(idChu).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
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
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        scrollView.addView(layout);
                        dialog.setContentView(scrollView);
                        dialog.show();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        referDetailProd.child("products").child(idProd).removeEventListener(event);
        referDetailProd = null;
        event = null;
    }
}