package com.tdc.vlxdonline.Activity;

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

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.ImageAdapter;
import com.tdc.vlxdonline.Adapter.ProductAdapter;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.databinding.FragmentProdDetailCustomerBinding;

import java.util.ArrayList;

public class ProdDetailCustomerFragment extends Fragment {

    FragmentProdDetailCustomerBinding binding;
    // Id product duoc chon
    private String idProd = "";
    // danh sach product de cu
    ArrayList<Products> dataProds = new ArrayList<>();
    ProductAdapter productAdapter;
    // Danh sach anh mo ta cua san pham dc chon
    ArrayList<String> dataAnh = new ArrayList<>();
    ImageAdapter imageAdapter;

    private DatabaseReference referDetailProd;

    public ProdDetailCustomerFragment(String idProduct) {
        idProd = idProduct;
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpDisplay();
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
        // Su Kien Mua Ngay
        binding.btnDatHangNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Customer_HomeActivity) getActivity()).ReplaceFragment(new DatHangNgayFragment(idProd, Integer.parseInt(binding.edtSoLuong.getText().toString())));
            }
        });
        // Su Kien Tang Giam SL
        binding.btnGiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int so = Integer.parseInt(binding.edtSoLuong.getText().toString());
                if (so > 1) {
                    binding.edtSoLuong.setText(String.format("%d", so - 1));
                }
            }
        });
        binding.btnTang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int so = Integer.parseInt(binding.edtSoLuong.getText().toString());
                binding.edtSoLuong.setText(String.format("%d", so + 1));
            }
        });
        // Su kien Add Cart
        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setHienThiSanPham() {
        referDetailProd.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    dataProds.clear(); // Xóa danh sách cũ trước khi cập nhật

                    // Duyệt qua từng User trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Products product = snapshot.getValue(Products.class);
                        dataProds.add(product); // Thêm User vào danh sách
                    }

                    // Xử lý danh sách userList (ví dụ: hiển thị trong RecyclerView)
                    // Event Click Product
                    productAdapter = new ProductAdapter(getActivity(), dataProds, View.GONE);
                    productAdapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
                        @Override
                        public void OnItemClick(View view, int position) {
                            Products product = dataProds.get(position);
                            ((Customer_HomeActivity) getActivity()).ReplaceFragment(new ProdDetailCustomerFragment(product.getId()));
                        }

                        @Override
                        public void OnBtnBuyClick(View view, int position) {
                        }
                    });
                    binding.rcOfferProd.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    binding.rcOfferProd.setAdapter(productAdapter);
                }catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpDisplay() {
        referDetailProd = FirebaseDatabase.getInstance().getReference();
        readProdFromDatabase(idProd);
        setHienThiSanPham();
        // Reset All Data
        dataAnh.clear();

        dataAnh.add("https://th.bing.com/th?id=OIF.0yfM4yF7hYIDB6%2bmwxU4GQ&w=172&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7");
        dataAnh.add("https://th.bing.com/th/id/OIF.lf4QHwb4DMz5wHZ84QYJmQ?w=183&h=183&c=7&r=0&o=5&dpr=1.3&pid=1.7");
        dataAnh.add("https://th.bing.com/th/id/OIP.UWORqopZEI954B5G-Z4sbgHaHQ?w=169&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7");
        dataAnh.add("https://th.bing.com/th/id/OIP.BO1VNjeGOUGcGRWQNUVCZQHaHa?w=1024&h=1024&rs=1&pid=ImgDetMain");

        Glide.with(getActivity()).load(dataAnh.get(0)).into(binding.imgDetail);
    }

    private void readProdFromDatabase(String prodId) {
        referDetailProd.child("products").child(prodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Products product = dataSnapshot.getValue(Products.class);
                    if (product != null) {
                        Glide.with(getActivity()).load(product.getAnh()).into(binding.ivAnhChinh);
                        binding.tvTenSpDetail.setText(product.getTen());
                        binding.tvGiaSpDetail.setText(product.getGia() + " VND");
                        binding.tvTonKhoDetail.setText("Kho: " + product.getTonKho());
                        binding.tvDaBanDetail.setText("Đã Bán: " + product.getDaBan());
                        binding.tvDonViDetail.setText(product.getDonVi());
                        binding.tvMoTaDetail.setText(product.getMoTa());
                    }else{
                        Toast.makeText(getActivity(), "Sản Phẩm Đã Bị Xóa!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

        @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}