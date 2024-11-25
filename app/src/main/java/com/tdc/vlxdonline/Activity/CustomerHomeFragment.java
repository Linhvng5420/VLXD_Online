package com.tdc.vlxdonline.Activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.AdapterCenterDrop;
import com.tdc.vlxdonline.Adapter.BannerAdapter;
import com.tdc.vlxdonline.Adapter.CategoryAdapter;
import com.tdc.vlxdonline.Adapter.ProductAdapter;
import com.tdc.vlxdonline.Model.Banner;
import com.tdc.vlxdonline.Model.Categorys;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentCustomerHomeBinding;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class CustomerHomeFragment extends Fragment {

    FragmentCustomerHomeBinding binding;
    // Data va adapter hien thi doc firebase
    ArrayList<Products> dataProds = new ArrayList<>();
    ProductAdapter productAdapter;
    ArrayList<Categorys> dataCategorys = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    // Data loc, sap xep
    ArrayList<String> dataLoc = new ArrayList<>();
    AdapterCenterDrop adapterLoc;
    ArrayList<String> dataSapXep = new ArrayList<>();
    AdapterCenterDrop adapterSX;
    // Banner
    ArrayList<Banner> dataBanner = new ArrayList<>();
    BannerAdapter bannerAdapter;
    Timer timer;
    LinearSnapHelper helper;

    // Item khac
    private String category = "";
    private String tuKhoa = "";
    DatabaseReference mDatabase;
    private View preView = null;
    ValueEventListener eventDocDanhSach;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCustomerHomeBinding.inflate(inflater, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Drawable draw = getActivity().getDrawable(R.drawable.bg_detail);
        draw.setTint(Color.rgb(215, 215, 215));
        binding.svCustomerHome.setBackground(draw);
        setAdapterProduct();
        setAdapterCategory();
        setBannerChange();
        KhoiTao();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Su kien search
        binding.svCustomerHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tuKhoa = query;
                mDatabase.child("products").addListenerForSingleValueEvent(eventDocDanhSach);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    tuKhoa = "";
                    mDatabase.child("products").addListenerForSingleValueEvent(eventDocDanhSach);
                }
                return false;
            }
        });
        // Su kien loc
        binding.spLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDatabase.child("products").addListenerForSingleValueEvent(eventDocDanhSach);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Su kien sap xep
        binding.spXapSep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDatabase.child("products").addListenerForSingleValueEvent(eventDocDanhSach);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void KhoiTao() {
        // Reset All Data
        dataLoc.clear();
        dataSapXep.clear();

        dataLoc.add("Lọc Theo");
        dataLoc.add("Số sao từ 1 - 3");
        dataLoc.add("Số sao từ 4 - 5");
        adapterLoc = new AdapterCenterDrop(getActivity(), R.layout.item_center_drop, dataLoc);
        binding.spLoc.setAdapter(adapterLoc);

        dataSapXep.add("Sắp Xếp Theo");
        dataSapXep.add("Số sao Tăng Dần");
        dataSapXep.add("Số sao Giảm Dần");
        dataSapXep.add("Giá Tăng Dần");
        dataSapXep.add("Giá Giảm Dần");
        adapterSX = new AdapterCenterDrop(getActivity(), R.layout.item_center_drop, dataSapXep);
        binding.spXapSep.setAdapter(adapterSX);

        readBanners();
        readcategorysFromDatabase();
        category = "";
        tuKhoa = "";
        setValueEvent();
        mDatabase.child("products").addValueEventListener(eventDocDanhSach);
    }

    private void setValueEvent() {
        eventDocDanhSach = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dataProds.clear(); // Xóa danh sách cũ trước khi cập nhật
                    // Duyệt qua từng prod trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Products product = snapshot.getValue(Products.class);
                        product.setId(snapshot.getKey());
                        if (!category.isEmpty() && !category.equals(product.getDanhMuc())) continue;
                        if (!tuKhoa.isEmpty() && !product.getTen().contains(tuKhoa) && !product.getMoTa().contains(tuKhoa))
                            continue;
                        if (binding.spLoc.getSelectedItemPosition() == 1 && Double.parseDouble(product.getSoSao()) > 3)
                            continue;
                        if (binding.spLoc.getSelectedItemPosition() == 2 && Double.parseDouble(product.getSoSao()) < 4)
                            continue;
                        dataProds.add(product); // Thêm prod vào danh sách
                    }

                    SapXepDanhSach();

                    productAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void setAdapterProduct() {
        // Event Click Product
        productAdapter = new ProductAdapter(getActivity(), dataProds, View.VISIBLE);
        productAdapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Products product = dataProds.get(position);
                ((Customer_HomeActivity) getActivity()).ReplaceFragment(new ProdDetailCustomerFragment(product.getId()));
            }

            @Override
            public void OnBtnBuyClick(View view, int position) {
                if (!dataProds.get(position).getTonKho().equals("0"))
                    ((Customer_HomeActivity) getActivity()).ReplaceFragment(new DatHangNgayFragment(dataProds.get(position).getId(), 1));
                else
                    Toast.makeText(getActivity(), "Hiện Tại Sản Phẩm Đã Bán Hết!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rcProdCustomerHome.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.rcProdCustomerHome.setAdapter(productAdapter);
    }

    private void setAdapterCategory() {
        // Category Adapter
        categoryAdapter = new CategoryAdapter(getActivity(), dataCategorys);
        categoryAdapter.setOnItemCategoryClickListener(new CategoryAdapter.OnItemCategoryClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                if (category.equals(dataCategorys.get(position).getId())) {
                    category = "";
                    view.setBackgroundColor(Color.TRANSPARENT);
                    preView = null;
                } else {
                    category = dataCategorys.get(position).getId();
                    Drawable drawable = getActivity().getDrawable(R.drawable.bg_detail);
                    view.setBackground(drawable);
                    if (preView != null) preView.setBackgroundColor(Color.TRANSPARENT);
                    preView = view;
                }
                mDatabase.child("products").addListenerForSingleValueEvent(eventDocDanhSach);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rcDanhMuc.setLayoutManager(linearLayoutManager);
        binding.rcDanhMuc.setAdapter(categoryAdapter);
    }

    private void readBanners() {
        mDatabase.child("banners").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dataBanner.clear(); // Xóa danh sách cũ trước khi cập nhật

                    // Duyệt qua từng User trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Banner banner = snapshot.getValue(Banner.class);
                        dataBanner.add(banner); // Thêm User vào danh sách
                    }
                    bannerAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBannerChange() {
        bannerAdapter = new BannerAdapter(getActivity(), dataBanner);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rcBanner.setLayoutManager(linearLayoutManager);
        binding.rcBanner.setAdapter(bannerAdapter);

        // Set tu chuyen banner
        if (helper == null) {
            helper = new LinearSnapHelper();
            helper.attachToRecyclerView(binding.rcBanner);
        }
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int position = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (position < (bannerAdapter.getItemCount() - 1)) {
                        linearLayoutManager.smoothScrollToPosition(binding.rcBanner, new RecyclerView.State(), position + 1);
                    } else {
                        linearLayoutManager.smoothScrollToPosition(binding.rcBanner, new RecyclerView.State(), 0);
                    }
                }
            }, 0, 3000);
        }
    }

    private void readcategorysFromDatabase() {
        mDatabase.child("categorys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dataCategorys.clear(); // Xóa danh sách cũ trước khi cập nhật

                    // Duyệt qua từng User trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Categorys category = snapshot.getValue(Categorys.class);
                        category.setId(snapshot.getKey());
                        dataCategorys.add(category); // Thêm User vào danh sách
                    }

                    categoryAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi Rồi Nè Má!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SapXepDanhSach() {
        Collections.sort(dataProds, new Comparator<Products>() {
            @Override
            public int compare(Products p1, Products p2) {
                int typeSort = binding.spXapSep.getSelectedItemPosition();
                int gia1 = Integer.parseInt(p1.getGiaBan());
                int gia2 = Integer.parseInt(p2.getGiaBan());
                double sao1 = Double.parseDouble(p1.getSoSao());
                double sao2 = Double.parseDouble(p2.getSoSao());
                if (typeSort == 1) {
                    return Double.compare(sao1, sao2);
                } else if (typeSort == 2) {
                    return Double.compare(sao2, sao1);
                } else if (typeSort == 3) {
                    return Integer.compare(gia1, gia2);
                } else if (typeSort == 4) {
                    return Integer.compare(gia2, gia1);
                }
                return 0;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        timer.cancel();
        timer = null;

        // Loại bỏ listener của Firebase
        if (mDatabase != null && eventDocDanhSach != null) {
            mDatabase.child("products").removeEventListener(eventDocDanhSach);
        }

        // Nullify references to help with garbage collection
        mDatabase = null;
        eventDocDanhSach = null;
    }
}