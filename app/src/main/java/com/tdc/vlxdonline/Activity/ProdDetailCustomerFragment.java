package com.tdc.vlxdonline.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.ImageAdapter;
import com.tdc.vlxdonline.Model.AnhSanPham;
import com.tdc.vlxdonline.Model.CartItem;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.databinding.FragmentProdDetailCustomerBinding;

import java.util.ArrayList;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProdDetailCustomerBinding.inflate(inflater, container, false);
        // TODO 2 NGVlinh: Admin đăng nhập, ẩn các nút mua, giỏ hàng
        if (Customer_HomeActivity.info == null) {
            idKhach = "N/A";
            binding.btnDatHangNgay.setText("Xóa Sản Phẩm Vi Phạm!");
            binding.btnDatHangNgay.setTextColor(Color.WHITE);
            binding.btnDatHangNgay.setBackgroundColor(Color.RED);
            binding.lnGioHang.setVisibility(View.INVISIBLE);
        }

        // Khách hàng đăng nhập
        setAdapterAnh();
        setUpDisplay();

        // TODO 3 NGVLinh: Bắt sự kiện xem thông tin cửa hàng
        binding.tvCuaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idChu = prod.getIdChu();
                referDetailProd = FirebaseDatabase.getInstance().getReference();
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
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
            }
        });

        // Su Kien Mua Ngay hoac Xoa SP
        binding.btnDatHangNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Customer_HomeActivity.info != null) {
                    if (soLuong > 0)
                        ((Customer_HomeActivity) getActivity()).ReplaceFragment(new DatHangNgayFragment(idProd, soLuong));
                    else
                        Toast.makeText(getActivity(), "Hiện Tại Sản Phẩm Đã Bán Hết!", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(idProd);
                    new AlertDialog.Builder(getContext()).setTitle("Xóa Sản Phẩm").setMessage("").setPositiveButton("Xóa", (dialog, which) -> {
                        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference dbStore = FirebaseDatabase.getInstance().getReference();
                                    dbStore.child("ProdImages").child(idProd).removeValue(); // Xóa ảnh trong bảng "ProdImages")
                                    Toast.makeText(getActivity(), "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }).setNegativeButton("Hủy", null).show();
                }
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

                    // Duyệt qua từng User trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AnhSanPham image = snapshot.getValue(AnhSanPham.class);
                        dataAnh.add(image.getAnh());
                    }

                    Glide.with(getActivity()).load(dataAnh.get(0)).into(binding.imgDetail);

                    imageAdapter.notifyDataSetChanged();
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
        referDetailProd.child("products").child(idProd).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Products product = dataSnapshot.getValue(Products.class);
                    if (product != null) {
                        prod = product;
                        Glide.with(getActivity()).load(product.getAnh()).into(binding.ivAnhChinh);
                        binding.tvTenSpDetail.setText(product.getTen());
                        binding.tvGiaSpDetail.setText(chuyenChuoi(product.getGiaBan()) + " VND");
                        binding.tvTonKhoDetail.setText("Kho: " + product.getTonKho());
                        if (product.getTonKho().equals("0")) {
                            soLuong = 0;
                        }
                        binding.edtSoLuong.setText(soLuong + "");
                        binding.tvDaBanDetail.setText("Đã Bán: " + product.getDaBan());
                        binding.tvDonViDetail.setText(product.getDonVi());
                        binding.tvMoTaDetail.setText(product.getMoTa());
                    } else {
                        Toast.makeText(getActivity(), "Sản Phẩm Đã Bị Xóa!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }


                // TODO 5 NGVLinh: Hiển Thị Tên Cửa Hàng
                String idChu = prod.getIdChu();
                referDetailProd = FirebaseDatabase.getInstance().getReference();
                referDetailProd.child("thongtinchu").child(idChu).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (binding != null && snapshot.exists()) {
                                String tenChu = snapshot.child("ten").getValue(String.class);
                                binding.tvCuaHang.setText("Cửa Hàng " + tenChu);
                                binding.tvCuaHang.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}