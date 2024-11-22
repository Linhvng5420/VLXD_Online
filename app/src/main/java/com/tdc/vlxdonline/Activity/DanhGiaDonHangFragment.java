package com.tdc.vlxdonline.Activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.tdc.vlxdonline.Adapter.DanhGiaAdapter;
import com.tdc.vlxdonline.Model.DanhGia;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentDanhGiaDonHangBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DanhGiaDonHangFragment extends Fragment {
    FragmentDanhGiaDonHangBinding binding;
    ArrayList<DanhGia> data = new ArrayList<>();
    DanhGiaAdapter adapter;
    String idKH;
    long idDon;
    DatabaseReference reference;

    public DanhGiaDonHangFragment(String idKH, long idDon, ArrayList<DanhGia> data) {
        this.idKH = idKH;
        this.data = data;
        this.idDon = idDon;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDanhGiaDonHangBinding.inflate(inflater, container, false);
        setAdapterDG();
        reference = FirebaseDatabase.getInstance().getReference();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnXacNhanDG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = true;
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getSoSao() == 0 || data.get(i).getMoTa() == null || data.get(i).getMoTa().trim().isEmpty()) {
                        ShowWar(data.get(i).getTen());
                        check = false;
                        break;
                    }
                }
                if (check) {
                    ShowAlertTB();
                }
            }
        });
    }

    private void setAdapterDG(){
        adapter = new DanhGiaAdapter(getActivity(), data, 1);
        adapter.setOnItemDanhGiaClick(new DanhGiaAdapter.OnItemDanhGiaClick() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onStartClick(int position, int soSao) {
                if (soSao == data.get(position).getSoSao()) {
                    data.get(position).setSoSao(soSao - 1);
                }else {
                    data.get(position).setSoSao(soSao);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChangeDescrip(int position, String chuoi) {
                data.get(position).setMoTa(chuoi);
            }
        });
        binding.rcDanhGia.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.rcDanhGia.setAdapter(adapter);
    }

    private void ShowAlertTB(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông Báo!").setMessage("Xác Nhận Hoàn Tất Đánh Giá Đơn Hàng?");
        builder.setPositiveButton("Xác Nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < data.size(); i++) {
                    DanhGia temp = data.get(i);
                    temp.setAnh(null);
                    temp.setTen(null);
                    DatabaseReference tempRefer = FirebaseDatabase.getInstance().getReference();
                    tempRefer.child("danhgia").child(idKH).child(idDon+"").child(temp.getIdSp()).setValue(temp);
                    tempRefer.child("danhgia").child(temp.getIdSp()).child(idKH).child(idDon+"").setValue(temp);
                    final double[] tongSao = {0};
                    final int[] dem = {0};
                    tempRefer.child("danhgia").child(temp.getIdSp()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Dong bo thoi gian de khong loi tinh toan
                            int itemCount = (int) dataSnapshot.getChildrenCount();
                            AtomicInteger count = new AtomicInteger(0);

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                dem[0] = dem[0] + (int) snapshot.getChildrenCount();
                                for (DataSnapshot miniDataSnapshot : snapshot.getChildren()) {
                                    DanhGia tempDG = miniDataSnapshot.getValue(DanhGia.class);
                                    tongSao[0] = tongSao[0] + tempDG.getSoSao();
                                }
                                if (count.incrementAndGet() == itemCount) {
                                    double trungBinh = tongSao[0]/dem[0];
                                    DecimalFormat df = new DecimalFormat("#.##");
                                    String chuoi = df.format(trungBinh);
                                    DatabaseReference referAdd = FirebaseDatabase.getInstance().getReference("products");
                                    referAdd.child(temp.getIdSp()).child("soSao").setValue(chuoi);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                reference.child("bills").child(idDon+"").child("daDanhGia").setValue(true);
                getActivity().getSupportFragmentManager().popBackStack();
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

    private void ShowWar(String ten){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông Báo!").setMessage("Sản Phẩm " + ten + " Chưa Đủ Thông Tin Đánh Giá!\nPhải có ít nhất một ký tự trong mô tả và ít nhất một sao đánh giá!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}