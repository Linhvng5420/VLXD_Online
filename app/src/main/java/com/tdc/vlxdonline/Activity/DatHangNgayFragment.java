package com.tdc.vlxdonline.Activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Trace;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.ChiTietDon;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.Model.TraGop;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentDatHangNgayBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DatHangNgayFragment extends Fragment {

    private FragmentDatHangNgayBinding binding;
    // Bien luu tam don hang
    private DonHang donHang;
    // Bien luu tam chi tiet don, vi dat ngay nen chi co 1 chi tiet
    private ChiTietDon chiTietDon;
    // Thong tin san pham
    private String idProd;
    private Products prod;
    // Khac
    private int soLuong;
    DatabaseReference referDatHangNgay;
    // Thanh Toan
    ArrayList<String> dataThanhT = new ArrayList<>();
    ArrayAdapter adapterTT;
    int thanhToan = 0;

    public DatHangNgayFragment(String idProduct, int soLuong) {
        this.idProd = idProduct;
        this.soLuong = soLuong;
        donHang = new DonHang();
        donHang.setIdKhach(Customer_HomeActivity.info.getID());
        chiTietDon = new ChiTietDon();
        chiTietDon.setIdSanPham(idProduct);
        chiTietDon.setIdDon(donHang.getId());
        chiTietDon.setSoLuong(soLuong);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDatHangNgayBinding.inflate(inflater, container, false);
        referDatHangNgay = FirebaseDatabase.getInstance().getReference();
        KhoiTao();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Loai thanh toan
        binding.spThanhToan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                thanhToan = position;
                if (position == 0) {
                    binding.tWarn.setVisibility(View.GONE);
                } else {
                    binding.tWarn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Su kien nhap du lieu
        binding.edtTenNguoiNhan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                donHang.setTenKhach(binding.edtTenNguoiNhan.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.edtSdtNguoiNhan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                donHang.setSdt(binding.edtSdtNguoiNhan.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.edtDiaChiNhan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                donHang.setDiaChi(binding.edtDiaChiNhan.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // Su Kien Tang Giam SL
        binding.btnGiamDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soLuong > 1) {
                    soLuong = soLuong - 1;
                    binding.edtSlDat.setText(soLuong + "");
                    changeSoLuong();
                }
            }
        });
        binding.btnTangDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soLuong = soLuong + 1;
                binding.edtSlDat.setText(soLuong + "");
                changeSoLuong();
            }
        });
        // Su kien nhap so luong
        binding.edtSlDat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!binding.edtSlDat.getText().toString().isEmpty()) {
                    soLuong = Integer.parseInt(binding.edtSlDat.getText().toString());
                    changeSoLuong();
                } else {
                    binding.edtSlDat.setText("1");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // Su kien mua ngay
        binding.btnDatNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!donHang.getTenKhach().isEmpty() && !donHang.getSdt().isEmpty() && !donHang.getDiaChi().isEmpty()) {
                    addDataToFirebase();
                } else {
                    Toast.makeText(getActivity(), "Hãy Nhập Đủ Thông Tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeSoLuong() {
        int kho = Integer.parseInt(prod.getTonKho());
        if (soLuong > kho) {
            Toast.makeText(getActivity(), "Số Lượng Bạn Nhập Lớn Hơn Tồn Kho!", Toast.LENGTH_SHORT).show();
            soLuong = kho;
            binding.edtSlDat.setText(soLuong + "");
        }
        chiTietDon.setSoLuong(soLuong);
        int tong = Integer.parseInt(prod.getGiaBan()) * soLuong;
        donHang.setTongTien(tong);
        binding.tvTongDatNgay.setText(chuyenChuoi(tong));
    }

    // Ham them dau cham cho gia ban
    private StringBuilder chuyenChuoi(int soTien) {
        StringBuilder chuoi = new StringBuilder(soTien + "");
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

    private void KhoiTao() {
        // Xu ly phuong thuc thanh toan
        dataThanhT.clear();
        dataThanhT.add("Thanh Toán Trực Tiếp");
        dataThanhT.add("Trả Góp Một Tháng");
        dataThanhT.add("Trả Góp Hai Tháng");
        adapterTT = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, dataThanhT);
        binding.spThanhToan.setAdapter(adapterTT);

        binding.edtTenNguoiNhan.setText(Customer_HomeActivity.info.getTen());
        binding.edtSdtNguoiNhan.setText(Customer_HomeActivity.info.getSdt());
        binding.edtDiaChiNhan.setText(Customer_HomeActivity.info.getDiaChi());
        donHang.setTenKhach(Customer_HomeActivity.info.getTen());
        donHang.setSdt(Customer_HomeActivity.info.getSdt());
        donHang.setDiaChi(Customer_HomeActivity.info.getDiaChi());
        referDatHangNgay.child("products").child(idProd).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Products product = dataSnapshot.getValue(Products.class);
                    if (product != null) {
                        prod = product;
                        chiTietDon.setTen(product.getTen());
                        chiTietDon.setAnh(product.getAnh());
                        chiTietDon.setGia(Integer.parseInt(product.getGiaBan()));
                        donHang.setIdChu(product.getIdChu());
                        donHang.setAnh(product.getAnh());
                        donHang.setTongTien(Integer.parseInt(product.getGiaBan()) * soLuong);
                        Glide.with(getActivity()).load(product.getAnh()).into(binding.imgDatHangNgay);
                        binding.tvNameDatHangNgay.setText(product.getTen());
                        binding.tvGiaDatHangNgay.setText(chuyenChuoi(Integer.parseInt(product.getGiaBan())) + " đ");
                        binding.tvDesDatNgay.setText(product.getMoTa());
                        if (product.getTonKho().equals("0")) {
                            Toast.makeText(getActivity(), "Sản Phẩm Này Đã Hết Hàng!", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                        binding.edtSlDat.setText(soLuong + "");
                        binding.tvTongDatNgay.setText(chuyenChuoi(Integer.parseInt(product.getGiaBan()) * soLuong));
                    } else {
                        Toast.makeText(getActivity(), "Sản Phẩm Đã Bị Xóa!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void addDataToFirebase() {
        if (!prod.getTonKho().equals("0")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Xác Nhận Đơn Hàng!").setMessage("Hãy Chắc Chắn Bạn Sẽ Xác Nhận Đặt Mua Hàng!");

            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean checkTraGop = false;
                    if (thanhToan == 1) {
                        checkTraGop = true;
                        DaDuocDuyet(new DuyetCallback() {
                            @Override
                            public void onCallback(boolean isDuyet) {
                                if (isDuyet) {
                                    // Được duyệt
                                    donHang.setTrangThaiTT(1);
                                    donHang.setPhiTraGop(donHang.getTongTien() * 2 / 100);
                                    donHang.setTongTien(donHang.getTongTien() + donHang.getPhiTraGop());
                                    LocalDate nowDate = LocalDate.now();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                    for (int i = 0; i < 4; i++) {
                                        nowDate = nowDate.plusDays(7);
                                        TraGop tempTG = new TraGop(donHang.getId(), nowDate.format(formatter), (i + 1), donHang.getTongTien() / 4);
                                        referDatHangNgay.child("tragop").child(donHang.getId() + "").child((i + 1) + "").setValue(tempTG);
                                    }
                                    AddToFireBase();
                                } else {
                                    // Không được duyệt
                                    ShowWar();
                                }
                            }
                        });
                    } else if (thanhToan == 2) {
                        checkTraGop = true;
                        DaDuocDuyet(new DuyetCallback() {
                            @Override
                            public void onCallback(boolean isDuyet) {
                                if (isDuyet) {
                                    // Được duyệt
                                    donHang.setTrangThaiTT(1);
                                    donHang.setPhiTraGop(donHang.getTongTien() * 5 / 100);
                                    donHang.setTongTien(donHang.getTongTien() + donHang.getPhiTraGop());
                                    LocalDate nowDate = LocalDate.now();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                    for (int i = 0; i < 4; i++) {
                                        nowDate = nowDate.plusDays(15);
                                        TraGop tempTG = new TraGop(donHang.getId(), nowDate.format(formatter), (i + 1), donHang.getTongTien() / 4);
                                        referDatHangNgay.child("tragop").child(donHang.getId() + "").child((i + 1) + "").setValue(tempTG);
                                    }
                                    AddToFireBase();
                                } else {
                                    // Không được duyệt
                                    ShowWar();
                                }
                            }
                        });
                    }
                    if (!checkTraGop) {
                        AddToFireBase();
                    }

                }
            });
            builder.setNegativeButton(R.string.quay_lai, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            Drawable drawableIcon = getResources().getDrawable(android.R.drawable.ic_dialog_alert);
            drawableIcon.setTint(Color.RED);
            builder.setIcon(drawableIcon);
            Drawable drawableBg = getResources().getDrawable(R.drawable.bg_item_lg);
            drawableBg.setTint(Color.rgb(100, 220, 255));
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(drawableBg);
            alertDialog.show();
        } else {
            Toast.makeText(getActivity(), "Hiện Tại Sản Phẩm Này Đã Bán Hết!", Toast.LENGTH_SHORT).show();
        }
    }

    private void AddToFireBase(){
        referDatHangNgay.child("bills").child(donHang.getId() + "").setValue(donHang);
        referDatHangNgay.child("BillDetails").child(donHang.getId() + "").child(idProd).setValue(chiTietDon);
        referDatHangNgay.child("products").child(idProd).child("tonKho").setValue((Integer.parseInt(prod.getTonKho()) - soLuong) + "");
        Toast.makeText(getActivity(), "Hoàn Tất Đặt Hàng, Kiểm Tra Đơn Tại 'Đơn Hàng'", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void DaDuocDuyet(DuyetCallback callback) {
        referDatHangNgay.child("duyetkhachhang").child(prod.getIdChu()).child(Customer_HomeActivity.info.getID()).child("trangthai").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tempCheck = snapshot.getValue(String.class);
                boolean isDuyet = tempCheck != null && tempCheck.equals("1");
                callback.onCallback(isDuyet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(false); // Hoặc xử lý lỗi theo cách của bạn
            }
        });
    }

    public interface DuyetCallback {
        void onCallback(boolean isDuyet);
    }

    private void ShowWar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông Báo!").setMessage("Bạn Chưa Được Cửa Hàng Cho Phép Trả Góp!");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Drawable drawableIcon = getResources().getDrawable(android.R.drawable.ic_dialog_alert);
        drawableIcon.setTint(Color.RED);
        builder.setIcon(drawableIcon);
        Drawable drawableBg = getResources().getDrawable(R.drawable.bg_item_lg);
        drawableBg.setTint(Color.rgb(100, 220, 255));
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(drawableBg);
        alertDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}