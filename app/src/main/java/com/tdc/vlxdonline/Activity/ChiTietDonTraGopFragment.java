package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.tdc.vlxdonline.Adapter.AdapterTraGop;
import com.tdc.vlxdonline.Adapter.ChiTietDonHangAdapter;
import com.tdc.vlxdonline.Model.ChiTietDon;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.Model.ThongTinChu;
import com.tdc.vlxdonline.Model.TraGop;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentChiTietDonTraGopBinding;

import java.util.ArrayList;

public class ChiTietDonTraGopFragment extends Fragment {
    FragmentChiTietDonTraGopBinding binding;
    ArrayList<ChiTietDon> dataChiTiet = new ArrayList<>();
    ChiTietDonHangAdapter adapterChiTiet;
    ArrayList<TraGop> dataGop = new ArrayList<>();
    AdapterTraGop adapterTraGop;
    // Id don hang da chon
    private long idDon;
    DonHang donHang;
    DatabaseReference reference;
    // Kieu hien thi, chu hay khach hang | 0 chu, 1 khach
    int type;
    ValueEventListener eventDon, eventTraGop;

    public ChiTietDonTraGopFragment(long idDon, int type) {
        this.idDon = idDon;
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChiTietDonTraGopBinding.inflate(inflater, container, false);
        reference = FirebaseDatabase.getInstance().getReference();
        setAllAdapter();
        readDetailBill();
        readDataFireBase();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnTraGop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    XacNhanDaNhanTraGop();
                } else {
                    ChuyenTrangThaiGiaoHang(donHang.getTrangThai());
                }
            }
        });
    }

    private void readDataFireBase() {
        // Doc chi tiet
        reference.child("BillDetails").child(idDon + "").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dataChiTiet.clear(); // Xóa danh sách cũ trước khi cập nhật

                    // Duyệt qua từng User trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChiTietDon detail = snapshot.getValue(ChiTietDon.class);
                        dataChiTiet.add(detail); // Thêm User vào danh sách
                    }

                    adapterChiTiet.notifyDataSetChanged();
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
            }
        });
        // Doc dot tra gop
        if (eventTraGop == null) {
            eventTraGop = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataGop.clear();
                    int tempNo = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TraGop temp = snapshot.getValue(TraGop.class);
                        dataGop.add(temp);
                        if (!temp.isDaTra()) tempNo = tempNo + temp.getSoTien();
                    }
                    binding.tvTongNo.setText(getChuoiTong(tempNo) + " VND");
                    adapterTraGop.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            reference.child("tragop").child(idDon + "").addValueEventListener(eventTraGop);
        }
    }

    private void readDetailBill() {
        if (eventDon == null) {
            eventDon = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        DonHang don = dataSnapshot.getValue(DonHang.class);
                        if (don != null) {
                            donHang = don;
                            docThongTinUser(type == 0 ? don.getIdKhach() : don.getIdChu());
                            binding.tvTenNguoiNhan.setText(donHang.getTenKhach());
                            binding.tvSdtNguoiNhan.setText(donHang.getSdt());
                            binding.tvDiaChiNhan.setText(donHang.getDiaChi());
                            binding.tvNgayTao.setText("Ngày Tạo: " + donHang.getNgayTao());
                            binding.btnTraGop.setEnabled(false);
                            binding.tvTongTien.setText(getChuoiTong(donHang.getTongTien()) + " VND");
                            // Check và hiển thị các nút dựa theo người dùng
                            int trangThaiVc = donHang.getTrangThai();
                            int trangThaiTt = donHang.getTrangThaiTT();
                            // Update điều kiện typeUser = 2 thành typeEmployee = 1 khi có nhân viên giao hàng
                            if (trangThaiVc == 0) {
                                binding.btnTraGop.setText(R.string.cho_xac_nhan);
                            } else {
                                if (type == 0 && trangThaiTt < 2) {
                                    binding.btnTraGop.setEnabled(true);
                                } else if (trangThaiVc == 1) {
                                    binding.btnTraGop.setText(R.string.cho_nhan_don);
                                } else if (trangThaiVc == 2) {
                                    binding.btnTraGop.setText(R.string.dang_van_chuyen);
                                } else if (trangThaiVc == 3) {
                                    binding.btnTraGop.setText(R.string.cho_nhan_hang);
                                    if (type == 1) {
                                        binding.btnTraGop.setEnabled(true);
                                        binding.btnTraGop.setText("Xác Nhận Đã Nhận Hàng");
                                    }
                                } else if (trangThaiVc == 4) {
                                    binding.btnTraGop.setText(R.string.da_hoan_thanh);
                                    if (trangThaiTt == 1) {
                                        binding.btnTraGop.setText(R.string.tra_gop);
                                    }
                                } else if (trangThaiVc == 5) {
                                    binding.btnTraGop.setText("Chờ Lấy Hàng");
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Đơn Hàng Đã Bị Xóa!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            };
            reference.child("bills").child(idDon + "").addValueEventListener(eventDon);
        }
    }

    private void docThongTinUser(String id) {
        String bang = "customers";
        if (type == 1) {
            bang = "thongtinchu";
        }
        reference.child(bang).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (type == 0) {
                    KhachHang tempKH = snapshot.getValue(KhachHang.class);
                    binding.tvNameUser.setText("Khách Hàng: " + tempKH.getTen());
                } else {
                    ThongTinChu tempChu = snapshot.getValue(ThongTinChu.class);
                    binding.tvNameUser.setText("Shop: " + tempChu.getCuahang());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private StringBuilder getChuoiTong(int soTien) {
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

    private void setAllAdapter() {
        // Chi Tiet Don
        adapterChiTiet = new ChiTietDonHangAdapter(getActivity(), dataChiTiet);
        adapterChiTiet.setOnChiTietDonClick(new ChiTietDonHangAdapter.OnChiTietDonClick() {
            @Override
            public void onItemClick(int position) {
                if (type == 1) ((Customer_HomeActivity) getActivity()).ReplaceFragment(new ProdDetailCustomerFragment(dataChiTiet.get(position).getIdSanPham()));
            }
        });
        binding.rcChiTietDon.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.rcChiTietDon.setAdapter(adapterChiTiet);
        // Dot Tra Gop
        adapterTraGop = new AdapterTraGop(getActivity(), dataGop);
        binding.rcTraGop.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.rcTraGop.setAdapter(adapterTraGop);
    }

    private void ChuyenTrangThaiGiaoHang(int trangThaiHienTai) {
        int update = trangThaiHienTai + 1;
        if (update == 4) {
            for (int i = 0; i < dataChiTiet.size(); i++) {
                ChiTietDon tempCTD = dataChiTiet.get(i);
                DatabaseReference referChange = FirebaseDatabase.getInstance().getReference("products").child(tempCTD.getIdSanPham());
                referChange.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Products tempP = snapshot.getValue(Products.class);
                        if (tempP != null) {
                            int tempBan = Integer.parseInt(tempP.getDaBan()) + tempCTD.getSoLuong();
                            referChange.child("daBan").setValue(tempBan + "");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        reference.child("bills").child(idDon + "").child("trangThai").setValue(update);
    }

    private void XacNhanDaNhanTraGop() {
        for (int i = 0; i < dataGop.size(); i++) {
            TraGop temp = dataGop.get(i);
            if (!temp.isDaTra()) {
                reference.child("tragop").child(idDon + "").child(temp.getThuTu() + "").child("daTra").setValue(true);
                if (temp.getThuTu() == 4)
                    reference.child("bills").child(idDon + "").child("trangThaiTT").setValue(2);
                break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        reference.child("bills").child(idDon + "").removeEventListener(eventDon);
        reference.child("tragop").child(idDon + "").removeEventListener(eventTraGop);

        reference = null;
        eventDon = null;
        eventTraGop = null;
    }
}