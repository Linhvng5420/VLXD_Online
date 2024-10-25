package com.tdc.vlxdonline.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tdc.vlxdonline.Adapter.ImageAdapter;
import com.tdc.vlxdonline.Adapter.ProductAdapter;
import com.tdc.vlxdonline.Model.Categorys;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentProdDetailCustomerBinding;

import java.util.ArrayList;

public class ProdDetailCustomerFragment extends Fragment {

    FragmentProdDetailCustomerBinding binding;
    Products product = new Products();
    ArrayList<Products> dataProds = new ArrayList<>();
    ProductAdapter productAdapter;
    ArrayList<String> dataAnh = new ArrayList<>();
    ImageAdapter imageAdapter;

    public ProdDetailCustomerFragment(Products product) {
        this.product = product;
    }
    public ProdDetailCustomerFragment(String idProduct) {

    }

    private void setUpDisplay() {
        Glide.with(getActivity()).load(product.getAnh()).into(binding.ivAnhChinh);
        binding.tvTenSpDetail.setText(product.getTen());
        binding.tvGiaSpDetail.setText(product.getGia() + " VND");
        binding.tvTonKhoDetail.setText("Kho: " + product.getTonKho());
        binding.tvDaBanDetail.setText("Đã Bán: " + product.getDaBan());
        binding.tvDonViDetail.setText(product.getDonVi());
        binding.tvMoTaDetail.setText(product.getMoTa());

        // Reset All Data
        dataAnh.clear();
        dataProds.clear();

        dataAnh.add("https://th.bing.com/th?id=OIF.0yfM4yF7hYIDB6%2bmwxU4GQ&w=172&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7");
        dataAnh.add("https://th.bing.com/th/id/OIF.lf4QHwb4DMz5wHZ84QYJmQ?w=183&h=183&c=7&r=0&o=5&dpr=1.3&pid=1.7");
        dataAnh.add("https://th.bing.com/th/id/OIP.UWORqopZEI954B5G-Z4sbgHaHQ?w=169&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7");
        dataAnh.add("https://th.bing.com/th/id/OIP.BO1VNjeGOUGcGRWQNUVCZQHaHa?w=1024&h=1024&rs=1&pid=ImgDetMain");

        dataProds.add(new Products("A", "A", "A", "1", "1", "https://th.bing.com/th/id/OIP.UWORqopZEI954B5G-Z4sbgHaHQ?w=169&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7", "100000", "0.0", "1", "1000", "580"));
        dataProds.add(new Products("B", "B", "B", "1", "2", "https://th.bing.com/th/id/OIP.BO1VNjeGOUGcGRWQNUVCZQHaHa?w=1024&h=1024&rs=1&pid=ImgDetMain", "200000", "4.0", "1", "1000", "580"));
        dataProds.add(new Products("C", "C", "C", "1", "3", "https://th.bing.com/th/id/OIP.vyMrfzra1TPcklie3-GA9gHaH9?w=180&h=183&c=7&r=0&o=5&dpr=1.3&pid=1.7", "300000", "5.0", "1", "1000", "580"));
        dataProds.add(new Products("D", "D", "D", "1", "4", "https://th.bing.com/th?id=OIF.EGFQW6dgdgP%2fL6l2yvVChg&rs=1&pid=ImgDetMain", "400000", "3.5", "1", "1000", "580"));

        Glide.with(getActivity()).load(dataAnh.get(0)).into(binding.imgDetail);
        if (imageAdapter != null) {
            imageAdapter.notifyDataSetChanged();
            productAdapter.notifyDataSetChanged();
        }
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
        // Adapter San Pham
        productAdapter = new ProductAdapter(getActivity(), dataProds, View.VISIBLE);
        productAdapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Products product = dataProds.get(position);
                ((Customer_HomeActivity) getActivity()).ReplaceFragment(new ProdDetailCustomerFragment(product));
            }

            @Override
            public void OnBtnBuyClick(View view, int position) {
                setUpDisplay();
                Toast.makeText(getActivity(), "Co Ton Tai", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rcOfferProd.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binding.rcOfferProd.setAdapter(productAdapter);
        // Su Kien Mua Ngay
        binding.btnDatHangNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Customer_HomeActivity) getActivity()).ReplaceFragment(new DatHangNgayFragment(product, Integer.parseInt(binding.edtSoLuong.getText().toString())));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}