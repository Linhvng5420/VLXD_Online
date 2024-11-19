package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.KhachHangAdapter;
import com.tdc.vlxdonline.Model.KhachHang;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerKhachHangBinding;

import java.util.ArrayList;
import java.util.List;

public class Owner_KhachHangFragment extends Fragment {
    FragmentOwnerKhachHangBinding binding;
    KhachHangAdapter adapter;

    // Email mà tài khoản quản lý đang đăng nhập (Đã bỏ @mail.com)
    String idLogin = LoginActivity.idUser.substring(0, LoginActivity.idUser.indexOf("@"));

    // Lưu lại danh sách khách hàng ban đầu trước khi tìm kiếm
    private List<KhachHang> dsKhachHang;

    // Thông báo nếu có khách hàng nào cần xác thực tài khoản
    private ValueEventListener thongBaoListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("l.d", "Owner_KhachHangFragment > onCreate: idLogin: " + idLogin);
        dsKhachHang = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOwnerKhachHangBinding.inflate(getLayoutInflater(), container, false);

        //RecycleView: Thiết lập layout cho RecyclerView, sử dụng LinearLayoutManager để hiển thị danh sách theo chiều dọc
        binding.ownerRcvKhachHang.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new KhachHangAdapter(dsKhachHang);
        binding.ownerRcvKhachHang.setAdapter(adapter);
        adapter.getKhachHangList().clear();
        dsKhachHang.clear();

        return binding.getRoot();
    }

    // TODO: HÀM XỬ LÝ CHỨC NĂNG CỦA VIEW APP
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Firebase: lấy dữ liệu từ Firebase
        hienThiThongBao();
        hienThiDSKhachHang();

        // Thiết lập tìm kiếm
        timKiemKhachHang();

        // Bắt sự kiện khi nhấn vào recycleview khách hàng
        setupNhanItemKhachHang();

        // Lắng nghe sự kiện nhấn ra ngoài thanh tìm kiếm để tắt con trỏ và ẩn bàn phím
        binding.getRoot().setOnTouchListener((v, event) -> {
            hideKeyboard(v); // Ẩn bàn phím
            binding.searchView.clearFocus(); // Xóa focus để tắt con trỏ trong SearchView
            return false;
        });
    }

    // HIEN THI DANH SACH KHACH HANG
    private void hienThiDSKhachHang() {
        List<String> dsIDKhachHang = new ArrayList<>();

        if (idLogin.equals("admin")) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("customers");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        KhachHang khachHang = snapshot.getValue(KhachHang.class);
                        if (khachHang != null) {
                            khachHang.setID(snapshot.getKey());
                            dsKhachHang.add(khachHang);
                        }
                    }

                    // Cập nhật dữ liệu khi hoàn thành việc lấy tất cả khách hàng
                    adapter.notifyDataSetChanged();  // Cập nhật RecyclerView
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("duyetkhachhang");
            db.child(idLogin).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String id = snapshot.getKey();
                        dsIDKhachHang.add(id);
                    }

                    Log.d("l.d", "Owner_KhachHangFragment > hienThiDSKhachHang: dsIDKhachHang: " + dsIDKhachHang);

                    // Sau khi lấy ID, tải dữ liệu khách hàng
                    layThongTinKH(dsIDKhachHang);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void layThongTinKH(List<String> customerIds) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("customers");

        // Xóa danh sách khách hàng trước khi tải mới để tránh trùng lặp
        dsKhachHang.clear();

        // Dùng biến đếm để kiểm tra khi nào hoàn thành việc lấy tất cả dữ liệu khách hàng
        final int[] counter = {0};
        for (String id : customerIds) {
            db.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    KhachHang khachHang = dataSnapshot.getValue(KhachHang.class);
                    if (khachHang != null) {
                        khachHang.setID(dataSnapshot.getKey());
                        dsKhachHang.add(khachHang);
                        Log.d("l.d", "Owner_KhachHangFragment > layThongTinKH: khachHang: " + khachHang.toString());
                    }

                    // Cập nhật dữ liệu khi hoàn thành việc lấy tất cả khách hàng
                    counter[0]++;
                    if (counter[0] == customerIds.size()) {
                        //adapter.sortKhachHangList();  // Sắp xếp nếu cần thiết
                        adapter.notifyDataSetChanged();  // Cập nhật RecyclerView
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi khi không thể truy cập dữ liệu Firebase
                }
            });
        }
    }

    // HIEN THI THONG BAO
    private void hienThiThongBao() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("thongbaochu");

        thongBaoListener = db.child(idLogin).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasNotification = false;
                int count = 0;

                for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                    String xacthuc = String.valueOf(customerSnapshot.child("xacthuc").getValue(String.class));
                    if (xacthuc != null && "1".equals(xacthuc)) {
                        hasNotification = true;
                        count++;
                    }
                }

                if (binding != null && binding.lnThongBao != null) {
                    if (hasNotification) {
                        binding.lnThongBao.setBackground(getResources().getDrawable(R.drawable.bg_img_detail));
                        binding.ivThongBao.setColorFilter(Color.parseColor("#F44336"));
                        binding.tvThongBao.setText("Bạn có thông báo mới! +" + count);

                        // Xem thông báo
                        binding.lnThongBao.setOnClickListener(v -> showThongBaoPopup());
                    }
                } else {
                    Log.d("l.d", "Binding hoặc lnThongBao bị null");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Lỗi khi truy xuất thông báo từ Firebase: " + databaseError.getMessage());
            }
        });
    }

    private void showThongBaoPopup() {
        // Tạo danh sách để lưu thông tin khách hàng có thông báo
        List<KhachHang> khachHangThongBaoList = new ArrayList<>();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("thongbaochu").child(idLogin);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                    String xacthuc = String.valueOf(customerSnapshot.child("xacthuc").getValue(String.class));
                    if (xacthuc != null && "1".equals(xacthuc)) {
                        String khachHangId = customerSnapshot.getKey();
                        layThongTinKhachHang(khachHangId, khachHangThongBaoList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Lỗi khi truy xuất thông báo từ Firebase: " + databaseError.getMessage());
            }
        });
    }

    // Hàm lấy thông tin khách hàng từ Firebase và thêm vào danh sách
    private void layThongTinKhachHang(String khachHangId, List<KhachHang> khachHangThongBaoList) {
        DatabaseReference customerDb = FirebaseDatabase.getInstance().getReference("customers").child(khachHangId);
        customerDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                KhachHang khachHang = dataSnapshot.getValue(KhachHang.class);
                if (khachHang != null) {
                    khachHang.setID(dataSnapshot.getKey());
                    khachHangThongBaoList.add(khachHang);
                    hienThiDanhSachThongBao(khachHangThongBaoList); // Gọi hiển thị khi có khách hàng mới được thêm vào
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Lỗi khi truy xuất thông tin khách hàng: " + databaseError.getMessage());
            }
        });
    }

    private void hienThiDanhSachThongBao(List<KhachHang> khachHangThongBaoList) {
        // Tạo một AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("KHÁCH HÀNG YÊU CẦU XÁC THỰC");

        // Tạo RecyclerView cho AlertDialog
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tạo adapter cho RecyclerView và thiết lập danh sách khách hàng
        KhachHangAdapter thongBaoAdapter = new KhachHangAdapter(khachHangThongBaoList);
        recyclerView.setAdapter(thongBaoAdapter);
        builder.setView(recyclerView);

        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

        // Tạo và hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Thiết lập listener cho mỗi item trong danh sách và đóng dialog khi nhấn vào item
        thongBaoAdapter.setOnItemClickListener(khachHang -> {
            dialog.dismiss(); // Đóng dialog khi nhấn vào item
            ChuyenSangFragmentDetail(khachHang.getID()); // Chuyển đến trang chi tiết
        });
    }

    private void setupNhanItemKhachHang() {
        adapter.setOnItemClickListener(khachHang -> {
            ChuyenSangFragmentDetail(khachHang.getID());
        });
    }

    private void ChuyenSangFragmentDetail(String idKH) {
        // Tạo Bundle để truyền thông tin khách hàng được chọn qua Fragment Detail
        Bundle bundleIDKhachHang = new Bundle();
        bundleIDKhachHang.putSerializable("idKH", idKH); // Đưa dữ liệu ID khách hàng vào Bundle

        // Tạo một instance, nó giúp chúng ta chuyển đổi dữ liệu từ Fragment này sang Fragment khác
        Owner_KhachHangDetailFragment khachHangDetailFragment = new Owner_KhachHangDetailFragment();
        // Gán Bundle (chứa thông tin id khách hàng) vào cho Fragment chi tiết
        khachHangDetailFragment.setArguments(bundleIDKhachHang);

        // Thực hiện chuyển đổi sang Fragment chi tiết, thay thế Fragment hiện tại
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, khachHangDetailFragment) // Thay thế fragment_container hiện tại bằng fragment chi tiết
                .addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
                .commit(); // Thực hiện chuyển đổi

        // Xóa văn bản tìm kiếm khi một khách hàng được chọn
        binding.searchView.setQuery("", false); // Xóa văn bản tìm kiếm
        binding.searchView.clearFocus(); // Ẩn con trỏ
    }

    // CHỨC NĂNG TÌM KIẾM KHACH HANG
    private void timKiemKhachHang() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Không cần xử lý khi submit, chỉ thực hiện tìm kiếm ngay khi người dùng nhập
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Kiểm tra nếu từ khóa rỗng, trả lại danh sách ban đầu
                if (newText.isEmpty()) {
                    adapter.updateList(new ArrayList<>(dsKhachHang)); // Cập nhật lại danh sách ban đầu
                } else {
                    // Gọi hàm filter để tìm kiếm khách hàng
                    filterKhachHang(newText);
                }
                return true;
            }
        });

        // Tắt con trỏ khi SearchView bị mất focus
        binding.searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.searchView.clearFocus(); // Xóa focus khi mất focus
            }
        });
    }

    private void filterKhachHang(String query) {
        List<KhachHang> filteredList = new ArrayList<>();

        for (KhachHang khachHang : dsKhachHang) {
            // Kiểm tra nếu tên hoặc ID của khách hàng chứa từ khóa tìm kiếm (không phân biệt chữ hoa/chữ thường)
            if (khachHang.getTen().toLowerCase().contains(query.toLowerCase()) ||
                    khachHang.getID().toLowerCase().contains(query.toLowerCase()) ||
                    khachHang.getSdt().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(khachHang);
            }
        }

        // Cập nhật danh sách đã lọc vào adapter
        adapter.updateList(filteredList);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Giải phóng tài nguyên của binding để tránh việc rò rỉ bộ nhớ khi Fragment bị hủy
        binding = null;
    }
}