package com.tdc.vlxdonline.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.R;

import java.util.ArrayList;

public class KhachHangTraGopFragment extends Fragment {

    private DatabaseReference referDatHangGio;
    private TextView tvLaigop, tvTongTienGop;
    private Spinner spChonVay;
    private ArrayList<String> lstKhoanVay = new ArrayList<>();
    private int giaSanPham; // Sử dụng giaSanPham khởi tạo là 0
    private int laiSuat = 5; // Lãi suất mặc định là 5%

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_khach_hang_tra_gop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các thành phần giao diện
        tvLaigop = view.findViewById(R.id.tvLaigop);
        tvTongTienGop = view.findViewById(R.id.tvTongTienGop);
        spChonVay = view.findViewById(R.id.spChonVay);

        // Lấy tham chiếu Firebase
        referDatHangGio = FirebaseDatabase.getInstance().getReference("products");

        // Lấy tổng tiền từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            giaSanPham = bundle.getInt("tongTien", 0);// Lấy tổng tiền
        }

        // Tính lãi suất và tổng tiền trả góp
        int tienLai = (giaSanPham * laiSuat) / 100;
        int tongTienTraGop = giaSanPham + tienLai;

        // Hiển thị lên giao diện
        tvLaigop.setText(laiSuat + "%");
        tvTongTienGop.setText(chuyenChuoi(tongTienTraGop));

        // Xử lý sự kiện khi người dùng chọn khoản vay
        spChonVay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(), "Bạn đã chọn: " + selectedOption, Toast.LENGTH_SHORT).show();

                // Cập nhật lãi suất dựa trên lựa chọn của người dùng
                if (selectedOption.equals("6 tháng")) {
                    laiSuat = 5;
                } else if (selectedOption.equals("12 tháng")) {
                    laiSuat = 10;
                } else if (selectedOption.equals("24 tháng")) {
                    laiSuat = 15;
                }

                // Tính toán lại tổng tiền trả góp sau khi chọn thời gian vay
                int tienLai = (giaSanPham * laiSuat) / 100;
                int tongTienTraGop = giaSanPham + tienLai;
                tvLaigop.setText(laiSuat + "%");
                tvTongTienGop.setText(chuyenChuoi(tongTienTraGop));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Hàm định dạng số tiền (thêm dấu chấm vào các nhóm ba chữ số)
    private String chuyenChuoi(int soTien) {
        StringBuilder chuoi = new StringBuilder(soTien + "");
        if (chuoi.length() > 3) {
            int dem = 0;
            int doDai = chuoi.length() - 1;
            for (int i = doDai; i > 0; i--) {
                dem++;
                if (dem == 3) {
                    chuoi.insert(i, '.');
                    dem = 0;
                }
            }
        }
        return chuoi.toString();
    }
}
