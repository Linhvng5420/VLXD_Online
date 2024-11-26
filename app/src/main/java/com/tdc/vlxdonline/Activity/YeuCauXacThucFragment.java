package com.tdc.vlxdonline.Activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.ThongTinChu;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentYeuCauXacThucBinding;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class YeuCauXacThucFragment extends Fragment {

    FragmentYeuCauXacThucBinding binding;
    ArrayList<ThongTinChu> dataChu = new ArrayList<>();
    ArrayList<String> dataHienThi = new ArrayList<>();
    ArrayAdapter<String> adapter;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ValueEventListener eventListener;
    String tuKhoa = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentYeuCauXacThucBinding.inflate(inflater, container, false);
        setAdapterHT();
        DocThongTin();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.svYeuCau.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Gán từ khóa tìm kiếm
                tuKhoa = query;
                // Yêu cầu đọc lại danh sách
                reference.child("thongtinchu").addListenerForSingleValueEvent(eventListener);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tuKhoa = newText;
                reference.child("thongtinchu").addListenerForSingleValueEvent(eventListener);
                return false;
            }
        });
    }

    private void setAdapterHT() {
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataHienThi);
        binding.lvDSChu.setAdapter(adapter);
        binding.lvDSChu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                XacNhanYeuCau(position);
            }
        });
    }

    private void XacNhanYeuCau(int position) {
        ThongTinChu tt = dataChu.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Xác Nhận Yêu Cầu!").setMessage("Xác Nhận Gửi Yêu Cầu Xác Thực Cho Cửa Hàng " + tt.getTen() + "?");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference.child("thongbaochu").child(tt.getId()).child(Customer_HomeActivity.info.getID()).child("xacthuc").setValue("1");
                reference.child("duyetkhachhang").child(tt.getId()).child(Customer_HomeActivity.info.getID()).child("trangthai").setValue("0");
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
        drawableBg.setTint(Color.rgb(100, 220, 255));
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(drawableBg);
        alertDialog.show();
    }

    private void DocThongTin() {
        if (eventListener == null) {
            eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Danh sách thông tin chủ cửa hàng
                    dataChu.clear();
                    // Danh sách phụ để hiển thị thông tin lên RecyclerView dạng String
                    dataHienThi.clear();
                    // Đồng bộ để không bị lỗi hiển thị khi chưa đọc xong dữ liệu
                    int itemCount = (int) dataSnapshot.getChildrenCount();
                    AtomicInteger atomicInteger = new AtomicInteger(0);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ThongTinChu tt = snapshot.getValue(ThongTinChu.class);
                        tt.setId(snapshot.getKey());
                        // Duyệt từ khóa lấy từ thanh tìm kiếm
                        if (tuKhoa.equals("") || tt.getTen().contains(tuKhoa) || tt.getEmail().contains(tuKhoa) || tt.getSdt().contains(tuKhoa)) {
                            DatabaseReference tempR = FirebaseDatabase.getInstance().getReference("duyetkhachhang").child(tt.getId()).child(Customer_HomeActivity.info.getID()).child("trangthai");
                            tempR.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String duyet = snapshot.getValue(String.class);
                                    if (duyet == null || duyet.equals("0")) {
                                        dataChu.add(tt);
                                        dataHienThi.add("Tên Chủ: " + tt.getTen() + "\n" + "Email: " + tt.getEmail() + "\n" + "Số Điện Thoại: " + tt.getSdt());
                                    }
                                    if (atomicInteger.incrementAndGet() == itemCount) adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            reference.child("thongtinchu").addValueEventListener(eventListener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        reference.child("thongtinchu").removeEventListener(eventListener);
        eventListener = null;
        reference = null;
    }
}