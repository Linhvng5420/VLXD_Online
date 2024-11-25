package com.tdc.vlxdonline.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.CuaHangAdapter;
import com.tdc.vlxdonline.Model.ThongTinChu;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentAdminCuahangBinding;

import java.util.ArrayList;
import java.util.List;

public class Admin_CuaHangFragment extends Fragment {
    FragmentAdminCuahangBinding binding;
    String LoginEmailID = LoginActivity.accountID;
    String LoginEmail = LoginActivity.idUser;
    List<ThongTinChu> listChuCuaHang;
    CuaHangAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listChuCuaHang = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminCuahangBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Adapter - Recycleview
        binding.ownerRcvNhanVien.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CuaHangAdapter(listChuCuaHang);
        binding.ownerRcvNhanVien.setAdapter(adapter);

        // Mặc định CheckRadio Show All là hiển thị tất cả NV
        binding.checkboxAll.setChecked(true);

        // Lắng nghe sự kiện thay đổi trạng thái của RadioGroup
        binding.RadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.checkboxChuaDuyet) {
                getDataCuaHangFilter("checkboxChuaDuyet");
            } else if (checkedId == R.id.checkboxOnline) {
                getDataCuaHangFilter("checkboxOnline");
            } else if (checkedId == R.id.checkboxOffline) {
                getDataCuaHangFilter("checkboxOffline");
            } else if (checkedId == R.id.checkboxLock) {
                getDataCuaHangFilter("checkboxLock");
            } else getDataCuaHang();
        });

        // Get Data Firebase listChuCuaHang
        getDataCuaHang();
        setOnClickItemRecycleView();

        return view;
    }

    private void setOnClickItemRecycleView() {
        adapter.setOnItemClickListener(new CuaHangAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ThongTinChu cuahang) {
                Bundle bundleIDKhachHang = new Bundle();
                bundleIDKhachHang.putSerializable("idCH", cuahang.getId());
                Admin_CuaHangDetailFragment adminCuaHangDetailFragment = new Admin_CuaHangDetailFragment();
                adminCuaHangDetailFragment.setArguments(bundleIDKhachHang);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, adminCuaHangDetailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void getDataCuaHang() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("thongtinchu");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listChuCuaHang.clear();
                adapter.getCuaHangList().clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ThongTinChu thongTinChu = dataSnapshot.getValue(ThongTinChu.class);

                    if (thongTinChu != null) {
                        thongTinChu.setId(dataSnapshot.getKey());
                        listChuCuaHang.add(thongTinChu);
                    }
                }

                // Chỉ cập nhật thay đổi từng item
                for (int i = 0; i < listChuCuaHang.size(); i++) {
                    adapter.notifyItemChanged(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Đọc ds cửa hàng theo filter: online(true), offline(false), locktype: lock(vinhvien & tamthoi), chuaduyet.
    // Tương ứng với RiadioButton: checkboxChuaDuyet, checkboxOnline, checkboxOffline, checkboxLock
    private void getDataCuaHangFilter(String filter) {
        DatabaseReference dbAccount = FirebaseDatabase.getInstance().getReference("account");
        DatabaseReference dbThongTinChu = FirebaseDatabase.getInstance().getReference("thongtinchu");

        dbAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotAccount) {
                dbThongTinChu.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshotThongTinChu) {
                        listChuCuaHang.clear();
                        adapter.getCuaHangList().clear();

                        for (DataSnapshot dataSnapshotChu : snapshotThongTinChu.getChildren()) {
                            ThongTinChu thongTinChu = dataSnapshotChu.getValue(ThongTinChu.class);

                            if (thongTinChu != null) {
                                String chuId = dataSnapshotChu.getKey();
                                thongTinChu.setId(chuId);

                                // Lấy thông tin trạng thái từ bảng account
                                DataSnapshot trangThaiSnapshot = snapshotAccount.child(chuId).child("trangthai");
                                if (trangThaiSnapshot.exists()) {
                                    boolean online = trangThaiSnapshot.child("online").getValue(Boolean.class);
                                    String locktype = trangThaiSnapshot.child("locktype").getValue(String.class);

                                    // Áp dụng bộ lọc
                                    if (filter.equals("checkboxChuaDuyet") && "chuaduyet".equals(locktype)) {
                                        listChuCuaHang.add(thongTinChu);
                                    } else if (filter.equals("checkboxOnline") && online) {
                                        listChuCuaHang.add(thongTinChu);
                                    } else if (filter.equals("checkboxOffline") && !online) {
                                        listChuCuaHang.add(thongTinChu);
                                    } else if (filter.equals("checkboxLock") &&
                                            ("vinhvien".equals(locktype) || "tamthoi".equals(locktype))) {
                                        listChuCuaHang.add(thongTinChu);
                                    }
                                }
                            }
                        }

                        // Cập nhật danh sách cho adapter
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}