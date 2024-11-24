package com.tdc.vlxdonline.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentAccountCustomerBinding;

public class AccountCustomerFragment extends Fragment {
    FragmentAccountCustomerBinding binding;
    String idKH = LoginActivity.idUser.substring(0, LoginActivity.idUser.indexOf("@"));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountCustomerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.ivChuaDg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Customer_HomeActivity) getActivity()).ReplaceFragment(new DonChuaDanhGiaFragment(idKH));
            }
        });
        binding.ivDaDg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Customer_HomeActivity) getActivity()).ReplaceFragment(new DaDanhGiaFragment(0, idKH));
            }
        });
        // Sự kiện khi nhấn nút logout
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setTitle("Đăng Xuất").setMessage("Đăng Xuất Khỏi Ứng Dụng").setPositiveButton("Có", (dialog, which) -> {
                    // Xóa thông tin đăng nhập
                    LoginActivity.idUser = null;
                    LoginActivity.typeUser = -1;

                    // Quay về màn hình LoginActivity
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    // Đóng hết các màn hình hiện có hoặc ẩn để quay lại màn hình đăng nhập
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    getActivity().finish(); // Đóng Owner_HomeActivity để quay lại màn hình đăng nhập
                }).setNegativeButton("Không", null).show();
            }
        });

        // Trang Detail
        // Thực hiện chuyển đổi sang Fragment chi tiết, thay thế Fragment hiện tại
        binding.lnHoSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo Bundle để truyền thông tin khách hàng được chọn qua Fragment Detail
                Bundle bundleIDKhachHang = new Bundle();
                bundleIDKhachHang.putSerializable("idKH", idKH); // Đưa dữ liệu ID khách hàng vào Bundle

                // Tạo một instance, nó giúp chúng ta chuyển đổi dữ liệu từ Fragment này sang Fragment khác
                Owner_KhachHangDetailFragment khachHangDetailFragment = new Owner_KhachHangDetailFragment();
                // Gán Bundle (chứa thông tin id khách hàng) vào cho Fragment chi tiết
                khachHangDetailFragment.setArguments(bundleIDKhachHang);

                // Thực hiện chuyển đổi sang Fragment chi tiết, thay thế Fragment hiện tại
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frm_customer, khachHangDetailFragment) // Thay thế fragment_container hiện tại bằng fragment chi tiết
                        .addToBackStack("detail") // Cho phép quay lại màn hình trước khi nhấn nút Back
                        .commit(); // Thực hiện chuyển đổi
            }
        });
//        binding.btnTraGop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Thực hiện chuyển đổi sang Fragment chi tiết, thay thế Fragment hiện tại
//                getParentFragmentManager().beginTransaction()
//                        .replace(R.id.frm_customer, new KhachHangTraGopFragment()) // Thay thế fragment_container hiện tại bằng fragment chi tiết
//                        .addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
//                        .commit(); // Thực hiện chuyển đổi
//            }
//        });

        binding.lnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thực hiện chuyển đổi sang Fragment chi tiết, thay thế Fragment hiện tại
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frm_customer, new Owner_SettingAboutFragment()) // Thay thế fragment_container hiện tại bằng fragment chi tiết
                        .addToBackStack("about") // Cho phép quay lại màn hình trước khi nhấn nút Back
                        .commit(); // Thực hiện chuyển đổi
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}