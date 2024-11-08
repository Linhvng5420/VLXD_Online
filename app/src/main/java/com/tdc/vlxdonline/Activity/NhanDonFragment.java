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
import com.tdc.vlxdonline.Adapter.DonHangAdapter;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentDonDaGiaoBinding;
import com.tdc.vlxdonline.databinding.FragmentNhanDonBinding;

import java.util.ArrayList;


public class NhanDonFragment extends Fragment {

    FragmentNhanDonBinding binding;
    // Danh sach don hang, duoc duyet theo loai nguoi dung
    ArrayList<DonHang> dsDon = new ArrayList<>();
    DonHangAdapter adapter;
    private DatabaseReference reference;
    private ValueEventListener event;
    String idChu;

    public NhanDonFragment(String emailChu) {
        idChu = emailChu.substring(0, emailChu.indexOf("@"));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNhanDonBinding.inflate(inflater, container, false);
        reference = FirebaseDatabase.getInstance().getReference();
        KhoiTao();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void KhoiTao() {
        event = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dsDon.clear(); // Xóa danh sách cũ trước khi cập nhật

                    // Duyệt qua trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DonHang don = snapshot.getValue(DonHang.class);

                        if (don.getTrangThai() != 1 || !don.getIdChu().equals(idChu)) continue;

                        dsDon.add(don); // Thêm User vào danh sách
                    }

                    adapter = new DonHangAdapter(getActivity(), dsDon);
                    // Event Click Don Hang
                    adapter.setOnItemDonHangClick(new DonHangAdapter.OnItemDonHangClick() {
                        @Override
                        public void onItemClick(int position) {
                            DonHang donHang = dsDon.get(position);
                            ((Shipper_HomeActivity) getActivity()).ReplaceFragment(new ChiTietGiaoHangFragment(donHang.getId()));

                        }
                    });

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    binding.rcDanhSachDon.setLayoutManager(linearLayoutManager);
                    binding.rcDanhSachDon.setAdapter(adapter);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "error!", Toast.LENGTH_SHORT).show();
            }
        };
        reference.child("bills").addValueEventListener(event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        // Loại bỏ listener của Firebase
        if (reference != null && event != null) {
            reference.child("bills").removeEventListener(event);
        }

        // Nullify references to help with garbage collection
        reference = null;
        event = null;
    }
}