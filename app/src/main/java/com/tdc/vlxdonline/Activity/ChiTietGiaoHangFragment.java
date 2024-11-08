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
import com.tdc.vlxdonline.Adapter.ChiTietDonHangAdapter;
import com.tdc.vlxdonline.Model.ChiTietDon;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentChiTietDonBinding;
import com.tdc.vlxdonline.databinding.FragmentChiTietGiaoHangBinding;

import java.util.ArrayList;

public class ChiTietGiaoHangFragment extends Fragment {

    FragmentChiTietGiaoHangBinding binding;
    // Id don hang da chon
    private long idDon;
    DonHang donHang;
    // Danh sach chi tiet cac san pham da mua cua don hang
    ArrayList<ChiTietDon> dataChiTietDon = new ArrayList<>();
    ChiTietDonHangAdapter adapter;
    private DatabaseReference reference;

    public ChiTietGiaoHangFragment(long idDonHang) {
        this.idDon = idDonHang;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChiTietGiaoHangBinding.inflate(inflater, container, false);
        reference = FirebaseDatabase.getInstance().getReference();
        KhoiTao();
        docChiTietDon();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Bat su kien nut Trang Thai Van Chuyen (Nhieu loai su kien, dua vao doi tuong dang dung)
        binding.btnTrangThai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển trạng thái giao hàng sang giai đoạn tiếp theo
                ChuyenTrangThaiGiaoHang(donHang.getTrangThai());
            }
        });
    }

    private void KhoiTao() {
        reference.child("bills").child(idDon + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    DonHang don = dataSnapshot.getValue(DonHang.class);
                    if (don != null) {
                        donHang = don;
                        binding.tvTenNguoiNhan.setText(donHang.getTenKhach());
                        binding.tvSdtNguoiNhan.setText(donHang.getSdt());
                        binding.tvDiaChiNhan.setText(donHang.getDiaChi());
                        binding.btnTrangThai.setEnabled(false);
                        binding.tvTongTien.setText(getChuoiTong(donHang.getTongTien()));
                        binding.tvNgay.setText(donHang.getNgayTao());
                        // Check và hiển thị các nút dựa theo người dùng
                        int trangThaiVc = donHang.getTrangThai();
                        int trangThaiTt = donHang.getTrangThaiTT();
                        // Update điều kiện typeUser = 2 thành typeEmployee = 1 khi có nhân viên giao hàng
                        if (trangThaiVc == 0) {
                            binding.btnTrangThai.setText(R.string.cho_xac_nhan);
                        } else {
                            if (trangThaiVc == 1) {
                                binding.btnTrangThai.setEnabled(true);
                                binding.btnTrangThai.setText("Nhận Giao Đơn Hàng");
                            } else if (trangThaiVc == 2) {
                                binding.btnTrangThai.setEnabled(true);
                                binding.btnTrangThai.setText("Xác Nhận Giao Thành Công");
                            } else if (trangThaiVc == 4) {
                                binding.btnTrangThai.setText(R.string.da_hoan_thanh);
                                if (trangThaiTt == 0) {
                                    binding.btnTrangThai.setText(R.string.chua_thanh_toan);
                                }
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
        });
    }

    private void docChiTietDon() {
        reference.child("BillDetails").child(idDon + "").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dataChiTietDon.clear(); // Xóa danh sách cũ trước khi cập nhật

                    // Duyệt qua từng User trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChiTietDon detail = snapshot.getValue(ChiTietDon.class);
                        dataChiTietDon.add(detail); // Thêm User vào danh sách
                    }

                    // Adapter va Event chi tiet don
                    adapter = new ChiTietDonHangAdapter(getActivity(), dataChiTietDon);
                    adapter.setOnChiTietDonClick(new ChiTietDonHangAdapter.OnChiTietDonClick() {
                        @Override
                        public void onItemClick(int position) {
                            ((Customer_HomeActivity) getActivity()).ReplaceFragment(new ProdDetailCustomerFragment(dataChiTietDon.get(position).getIdSanPham()));
                        }
                    });
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    binding.rcChiTietDon.setLayoutManager(linearLayoutManager);
                    binding.rcChiTietDon.setAdapter(adapter);
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ChuyenTrangThaiGiaoHang(int trangThaiHienTai) {
        if (trangThaiHienTai==1) reference.child("bills").child(idDon+"").child("idGiao").setValue(Shipper_HomeActivity.nv.getCccd());
        int update = trangThaiHienTai + 1;
        reference.child("bills").child(idDon + "").child("trangThai").setValue(update);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}