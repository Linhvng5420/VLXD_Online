package com.tdc.vlxdonline.Activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.ChiTietDonHangAdapter;
import com.tdc.vlxdonline.Model.ChiTietDon;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentDatHangGioHangBinding;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DatHangGioHangFragment extends Fragment {

    boolean daTaoDon = false;
    FragmentDatHangGioHangBinding binding;
    DatabaseReference referDatHangGio;
    KhachHang khach = Customer_HomeActivity.info;
    ValueEventListener prodEvent;
    // Chi tiet don hang va danh sach cac don hang
    ArrayList<DonHang> dataDon = new ArrayList<>();
    ArrayList<ChiTietDon> dataChiTiet = new ArrayList<>();
    ChiTietDonHangAdapter adapter;

    public DatHangGioHangFragment(ArrayList<ChiTietDon> data) {
        dataChiTiet = data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDatHangGioHangBinding.inflate(inflater, container, false);
        if (daTaoDon) getActivity().getSupportFragmentManager().popBackStack();
        binding.edtTenNguoiNhan.setText(khach.getTen());
        binding.edtSdtNguoiNhan.setText(khach.getSdt());
        binding.edtDiaChiNhan.setText(khach.getDiaChi());
        referDatHangGio = FirebaseDatabase.getInstance().getReference();
        setAdapterChiTiet();
        setProdEvent();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataDon.size() > 1) thongBaoNhieuDon();
                XacNhanTaoDonHang();
            }
        });
    }

    // Hàm tạo danh sách các đơn hàng theo chủ cửa hàng dựa theo các sản phẩm đã chọn
    private void TaoDonHangTuChiTiet() {
        // Đếm số lượng mục cần xử lý (xử lý đồng bộ firebase)
        int itemCount = dataChiTiet.size();
        AtomicInteger processedCount = new AtomicInteger(0);
        for (int i = 0; i < dataChiTiet.size(); i++) {
            boolean[] checkRemove = {false};
            final int position = i;
            ChiTietDon temp = dataChiTiet.get(i);
            referDatHangGio.child("products").child(temp.getIdSanPham()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        Products product = snapshot.getValue(Products.class);
                        if (product != null) {
                            if (Integer.parseInt(product.getTonKho()) > 0) {
                                // Bien check đã có đơn hàng chủ sở hữu của sản phẩm đang duyệt chưa
                                boolean check = false;
                                for (int j = 0; j < dataDon.size(); j++) {
                                    if (product.getIdChu().equals(dataDon.get(j).getIdChu())) {
                                        dataChiTiet.get(position).setIdDon(dataDon.get(j).getId());
                                        dataDon.get(j).setTongTien(dataDon.get(j).getTongTien() + (temp.getGia() * temp.getSoLuong()));
                                        check = true;
                                        break;
                                    }
                                }
                                if (!check) {
                                    DonHang don = new DonHang();
                                    don.setId(don.getId() + position);
                                    don.setIdChu(product.getIdChu());
                                    don.setIdKhach(khach.getID());
                                    don.setTenKhach(khach.getTen());
                                    don.setSdt(khach.getSdt());
                                    don.setDiaChi(khach.getDiaChi());
                                    don.setAnh(product.getAnh());
                                    don.setTongTien(temp.getGia() * temp.getSoLuong());
                                    dataDon.add(don);
                                    dataChiTiet.get(position).setIdDon(don.getId());
                                }
                            } else {
                                ThongBaoHetHang(temp.getTen());
                                dataChiTiet.remove(position);
                                checkRemove[0] = true;
                            }
                        } else {
                            ThongBaoXoa(temp.getTen());
                            dataChiTiet.remove(position);
                            checkRemove[0] = true;
                        }
                        if (processedCount.incrementAndGet() == itemCount) {
                            int tong = 0;
                            for (int i = 0; i < dataDon.size(); i++) {
                                tong = tong + dataDon.get(i).getTongTien();
                            }
                            binding.tvTongDatCart.setText(chuyenChuoi(tong));
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (checkRemove[0]) {
                i = i - 1;
            }
        }
    }

    // Thong bao cho nguoi dung biet se tao nhieu don hang neu mua tu nhieu nha cung cap
    private void thongBaoNhieuDon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông Báo!").setMessage("Các sản phẩm bạn đã chọn thuộc các nhà cung cấp khác nhau, chúng tôi sẽ tạo các đơn hàng dựa theo các nhà cung cấp cho bạn!");

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

    // Ham thong bao xac nhan mua hang cho khach hang
    private void XacNhanTaoDonHang() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Xác Nhận Mua Hàng!").setMessage("Hãy Chắc Chắn Rằng Bạn Sẽ Đặt Mua Các Sản Phẩm Đã Chọn!");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TaoDonHang();
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
    }

    // Ham them cac don hang va chi tiet vao firebase
    private void TaoDonHang() {
        referDatHangGio.child("products").removeEventListener(prodEvent);
        for (int i = 0; i < dataDon.size(); i++) {
            DonHang tempDon = dataDon.get(i);
            referDatHangGio.child("bills").child(tempDon.getId() + "").setValue(tempDon);
        }
        for (int i = 0; i < dataChiTiet.size(); i++) {
            ChiTietDon tempChiTiet = dataChiTiet.get(i);
            referDatHangGio.child("BillDetails").child(tempChiTiet.getIdDon() + "").child(tempChiTiet.getIdSanPham()).setValue(tempChiTiet);
            referDatHangGio.child("carts").child(khach.getID()).child(tempChiTiet.getIdSanPham()).removeValue();
            // Giam ton kho cua san pham
            referDatHangGio.child("products").child(tempChiTiet.getIdSanPham()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Products product = snapshot.getValue(Products.class);
                    DatabaseReference tempRefer = FirebaseDatabase.getInstance().getReference("products");
                    tempRefer.child(tempChiTiet.getIdSanPham()).child("tonKho").setValue((Integer.parseInt(product.getTonKho()) - tempChiTiet.getSoLuong()) + "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        ((Customer_HomeActivity) getActivity()).ReplaceFragment(new CacDonDaTaoFragment(dataDon));
        daTaoDon = true;
    }

    // Event realtime cho Products
    private void setProdEvent() {
        if (prodEvent == null) {
            prodEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        dataDon.clear();
                        TaoDonHangTuChiTiet();
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            referDatHangGio.child("products").addValueEventListener(prodEvent);
        }
    }

    private void setAdapterChiTiet() {
        adapter = new ChiTietDonHangAdapter(getActivity(), dataChiTiet);
        binding.rcChiTietDatGio.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.rcChiTietDatGio.setAdapter(adapter);
    }

    private void ThongBaoXoa(String maSP) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông Báo!").setMessage("Đã Xóa Sản Phẩm " + maSP + " Khỏi Danh Sách Vì Sản Phẩm Đã Bị Xóa!");

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

    private void ThongBaoHetHang(String maSP) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông Báo!").setMessage("Đã Xóa Sản Phẩm " + maSP + " Khỏi Danh Sách Vì Sản Phẩm Đã Hết Hàng!");

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        referDatHangGio.child("products").removeEventListener(prodEvent);

        referDatHangGio = null;
        prodEvent = null;

        binding = null;
    }
}