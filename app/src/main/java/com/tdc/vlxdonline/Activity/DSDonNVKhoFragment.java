package com.tdc.vlxdonline.Activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.DonHangAdapter;
import com.tdc.vlxdonline.Model.DonHang;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentDSDonNVKhoBinding;
import com.tdc.vlxdonline.databinding.FragmentDanhSachDonHangBinding;

import java.util.ArrayList;


public class DSDonNVKhoFragment extends Fragment {

    FragmentDSDonNVKhoBinding binding;
    ArrayList<DonHang> data = new ArrayList<>();
    DonHangAdapter adapter;
    // Tu khoa dung cho tim kiem
    private String tuKhoa = "";
    private DatabaseReference referDanhSachDon;
    private ValueEventListener eventDocData;
    private Drawable draw;
    private String emailchu = Warehouse_HomeActivity.nhanVien.getEmailchu();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDSDonNVKhoBinding.inflate(inflater, container, false);
        referDanhSachDon = FirebaseDatabase.getInstance().getReference();
        setAdapterDonHang();
        draw = getActivity().getDrawable(R.drawable.bg_detail);
        draw.setTint(Color.rgb(0, 100, 255));
        KhoiTao();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Event Search
        binding.svDonHang.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tuKhoa = query;
                referDanhSachDon.child("bills").addListenerForSingleValueEvent(eventDocData);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    tuKhoa = "";
                    referDanhSachDon.child("bills").addListenerForSingleValueEvent(eventDocData);
                }
                return false;
            }
        });
        binding.btnAddDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Warehouse_HomeActivity)getActivity()).ReplaceFragment(new TaoDonXuatKhoFragment());
            }
        });
    }

    private void KhoiTao() {
        setValueEventDon();
        referDanhSachDon.child("bills").addValueEventListener(eventDocData);
    }

    private void setValueEventDon() {
        eventDocData = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    data.clear(); // Xóa danh sách cũ trước khi cập nhật

                    // Duyệt qua trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DonHang don = snapshot.getValue(DonHang.class);
                        if (!tuKhoa.isEmpty() && !don.getTenKhach().contains(tuKhoa) && !don.getDiaChi().contains(tuKhoa))
                            continue;
                        if (!don.getIdChu().equals(emailchu.substring(0, emailchu.indexOf("@")))) continue;
                        if (!don.getIdTao().equals(Warehouse_HomeActivity.nhanVien.getCccd())) continue;
                        data.add(don); // Thêm User vào danh sách
                    }

                    adapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void setAdapterDonHang() {
        adapter = new DonHangAdapter(getActivity(), data);
        // Event Click Don Hang
        adapter.setOnItemDonHangClick(new DonHangAdapter.OnItemDonHangClick() {
            @Override
            public void onItemClick(int position) {
                DonHang donHang = data.get(position);
                if (donHang.getPhiTraGop() > 0) {

                } else {
                    if (LoginActivity.typeUser == 0)
                        ((Owner_HomeActivity) getActivity()).ReplaceFragment(new ChiTietDonFragment(donHang.getId()));
                    if (LoginActivity.typeUser == 1)
                        ((Customer_HomeActivity) getActivity()).ReplaceFragment(new ChiTietDonFragment(donHang.getId()));
                    if (LoginActivity.typeUser == 2)
                        ((Warehouse_HomeActivity) getActivity()).ReplaceFragment(new ChiTietDonFragment(donHang.getId()));
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcvDonhang.setLayoutManager(linearLayoutManager);
        binding.rcvDonhang.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        draw.setTint(Color.rgb(215, 215, 215));

        binding = null;

        // Loại bỏ listener của Firebase
        if (referDanhSachDon != null && eventDocData != null) {
            referDanhSachDon.child("bills").removeEventListener(eventDocData);
        }

        // Nullify references to help with garbage collection
        referDanhSachDon = null;
        eventDocData = null;

        super.onDestroyView();
    }
}