package com.tdc.vlxdonline.Activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.ChiTietDonNhapAdapter;
import com.tdc.vlxdonline.Adapter.Product_Adapter;
import com.tdc.vlxdonline.Model.ChiTietNhap;
import com.tdc.vlxdonline.Model.DonNhap;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentChiTietDonNhapBinding;

import java.util.ArrayList;


public class ChiTietDonNhapFragment extends Fragment {
    FragmentChiTietDonNhapBinding binding;
    DatabaseReference reference; // Tham chiếu đến cơ sở dữ liệu Firebase
    DonNhap donNhap;
    ArrayList<ChiTietNhap> dsChiTiet = new ArrayList<>(); // Danh sách sản phẩm
    ChiTietDonNhapAdapter chiTietDonNhapAdapter; // Adapter cho danh sách sản phẩm

    public ChiTietDonNhapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHienThi();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference();
        donNhap = (DonNhap) getArguments().getSerializable("donNhap");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentChiTietDonNhapBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
    public void setHienThi(){
        if (donNhap != null) {
            binding.tvIdDonNhap.setText(donNhap.getId() + "");
            binding.tvIdNvNhap.setText(donNhap.getIdTao());
            StringBuilder chuoi = new StringBuilder(donNhap.getTongTien() + "");
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
            binding.tvTongTienDonNhap.setText(chuoi + " VND");
            binding.tvNgayNhap.setText(donNhap.getNgayTao());
        }
        reference.child("chiTietNhap").child(donNhap.getId() + "").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    dsChiTiet.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChiTietNhap chiTietNhap = snapshot.getValue(ChiTietNhap.class); // Lấy sản phẩm từ snapshot
                        dsChiTiet.add(chiTietNhap);
                    }
                    chiTietDonNhapAdapter = new ChiTietDonNhapAdapter(getActivity(), dsChiTiet); // Khởi tạo adapter cho sản phẩm
                    binding.rcvDsDonNhap.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)); // Thiết lập layout cho RecyclerView sản phẩm
                    binding.rcvDsDonNhap.setAdapter(chiTietDonNhapAdapter); // Gán adapter vào RecyclerView

                    // Sự kiện khi nhấn vào sản phẩm

                }catch (Exception e){
                    Toast.makeText(getContext(), "Lỗi hiển thị dữ liệu!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });

    }

}