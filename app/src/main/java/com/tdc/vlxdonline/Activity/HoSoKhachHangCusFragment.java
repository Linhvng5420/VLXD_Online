package com.tdc.vlxdonline.Activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentHoSoKhachHangCusBinding;

public class HoSoKhachHangCusFragment extends Fragment {

    FragmentHoSoKhachHangCusBinding binding;
    boolean checkSua = false; // Biến kiểm tra có đang trong trạng thái sửa thông tin không
    KhachHang infoKhach = new KhachHang();
    DatabaseReference reference;
    ValueEventListener event;
    String uriAvata, uriCCCDMatTruoc, uriCCCDMatSau;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHoSoKhachHangCusBinding.inflate(inflater, container, false);
        reference = FirebaseDatabase.getInstance().getReference();
        docThongTin();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnCloseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkSua) {
                    binding.edtTenTk.setEnabled(true);
                    binding.edtSdtTk.setEnabled(true);
                    binding.edtEmailTk.setEnabled(true);
                    binding.edtDiachiTk.setEnabled(true);
                    binding.btnCloseInfo.setText("Hủy Thay Đổi");
                    checkSua = true;
                } else {
                    resetInfo();
                    checkSua = false;
                }
            }
        });
        binding.btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInformation();
            }
        });
    }

    private void saveInformation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông Báo!").setMessage("Xác Nhận Thay Đổi Thông Tin?");
        builder.setPositiveButton("Xác Nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference.child("customers").child(Customer_HomeActivity.info.getID()).removeEventListener(event);
                reference.child("customers").child(Customer_HomeActivity.info.getID()).child("ten").setValue(binding.edtTenTk.getText().toString());
                reference.child("customers").child(Customer_HomeActivity.info.getID()).child("sdt").setValue(binding.edtSdtTk.getText().toString());
                reference.child("customers").child(Customer_HomeActivity.info.getID()).child("email").setValue(binding.edtEmailTk.getText().toString());
                reference.child("customers").child(Customer_HomeActivity.info.getID()).child("diaChi").setValue(binding.edtDiachiTk.getText().toString());
                reference.child("customers").child(Customer_HomeActivity.info.getID()).child("avata").setValue(uriAvata);
                reference.child("customers").child(Customer_HomeActivity.info.getID()).child("cccdMatTruoc").setValue(uriCCCDMatTruoc);
                reference.child("customers").child(Customer_HomeActivity.info.getID()).child("cccdMatSau").setValue(uriCCCDMatSau);
                reference.child("customers").child(Customer_HomeActivity.info.getID()).addValueEventListener(event);
                resetInfo();
            }
        });
        builder.setNegativeButton(R.string.quay_lai, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Drawable drawableIcon = getResources().getDrawable(android.R.drawable.ic_dialog_alert);
        drawableIcon.setTint(Color.RED);
        builder.setIcon(drawableIcon);
        Drawable drawableBg = getResources().getDrawable(R.drawable.bg_item_lg);
        drawableBg.setTint(Color.rgb(100,220,255));
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(drawableBg);
        alertDialog.show();
    }

    private void resetInfo() {
        Glide.with(getActivity()).load(infoKhach.getAvata()).into(binding.ivAnhKhach);
        Glide.with(getActivity()).load(infoKhach.getCccdMatTruoc()).into(binding.ivCccdTrc);
        Glide.with(getActivity()).load(infoKhach.getCccdMatSau()).into(binding.ivCccdSau);
        binding.edtTenTk.setText(infoKhach.getTen());
        binding.edtSdtTk.setText(infoKhach.getSdt());
        binding.edtEmailTk.setText(infoKhach.getEmail());
        binding.edtDiachiTk.setText(infoKhach.getDiaChi());
        binding.edtTenTk.setEnabled(false);
        binding.edtSdtTk.setEnabled(false);
        binding.edtEmailTk.setEnabled(false);
        binding.edtDiachiTk.setEnabled(false);
        binding.btnCloseInfo.setText("Thay Đổi");
    }

    private void docThongTin() {
        if (event == null) {
            event = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        KhachHang info = snapshot.getValue(KhachHang.class);
                        if (info != null) {
                            infoKhach = info;
                            uriAvata = info.getAvata();
                            uriCCCDMatTruoc = info.getCccdMatTruoc();
                            uriCCCDMatSau = info.getCccdMatSau();
                            Glide.with(getActivity()).load(info.getAvata()).into(binding.ivAnhKhach);
                            Glide.with(getActivity()).load(info.getCccdMatTruoc()).into(binding.ivCccdTrc);
                            Glide.with(getActivity()).load(info.getCccdMatSau()).into(binding.ivCccdSau);
                            binding.edtTenTk.setText(info.getTen());
                            binding.edtSdtTk.setText(info.getSdt());
                            binding.edtEmailTk.setText(info.getEmail());
                            binding.edtDiachiTk.setText(info.getDiaChi());
                        } else {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            reference.child("customers").child(Customer_HomeActivity.info.getID()).addValueEventListener(event);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        reference.child("customers").child(Customer_HomeActivity.info.getID()).removeEventListener(event);

        reference = null;
        event = null;
    }
}