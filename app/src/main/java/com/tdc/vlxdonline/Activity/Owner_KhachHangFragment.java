package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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

    // Email mà tài khoản quản lý đang đăng nhập
    String emailLogin = LoginActivity.idUser;

    // Lưu lại danh sách khách hàng ban đầu trước khi tìm kiếm
    private List<KhachHang> dsKhachHang;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dsKhachHang = new ArrayList<>();
        Log.d("l.d", "Owner_KhachHangFragment > onCreate: emailLogin: " + emailLogin);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOwnerKhachHangBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    // TODO: HÀM XỬ LÝ CHỨC NĂNG CỦA VIEW APP
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //RecycleView: Thiết lập layout cho RecyclerView, sử dụng LinearLayoutManager để hiển thị danh sách theo chiều dọc
        binding.ownerRcvKhachHang.setLayoutManager(new LinearLayoutManager(getContext()));
        // Khởi tạo danh sách khách hàng trống và adapter
        adapter = new KhachHangAdapter(dsKhachHang);
        binding.ownerRcvKhachHang.setAdapter(adapter);

        // Firebase: lấy dữ liệu từ Firebase
        hienThiDSKhachHang();

        // Thiết lập tìm kiếm
        timKiemKhachHang();

        // Bắt sự kiện khi nhấn vào recycleview khách hàng
        nhanVaoItemKhachHang();

        // Lắng nghe sự kiện nhấn ra ngoài thanh tìm kiếm để tắt con trỏ và ẩn bàn phím
        binding.getRoot().setOnTouchListener((v, event) -> {
            hideKeyboard(v); // Ẩn bàn phím
            binding.searchView.clearFocus(); // Xóa focus để tắt con trỏ trong SearchView
            return false;
        });
    }

    private void nhanVaoItemKhachHang() {
        adapter.setOnItemClickListener(khachHang -> {
            // Tạo Bundle để truyền thông tin khách hàng được chọn qua Fragment Detail
            Bundle bundleIDKhachHang = new Bundle();
            bundleIDKhachHang.putSerializable("idKH", khachHang.getID()); // Đưa dữ liệu ID khách hàng vào Bundle

            // Tạo một instance, nó giúp chúng ta chuyển đổi dữ liệu từ Fragment này sang Fragment khác
            Owner_KhachHangDetailFragment khachHangDetailFragment = new Owner_KhachHangDetailFragment();
            // Gán Bundle (chứa thông tin id khách hàng) vào cho Fragment chi tiết
            khachHangDetailFragment.setArguments(bundleIDKhachHang);

            // Thực hiện chuyển đổi sang Fragment chi tiết, thay thế Fragment hiện tại
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_owner, khachHangDetailFragment) // Thay thế fragment_owner hiện tại bằng fragment chi tiết
                    .addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
                    .commit(); // Thực hiện chuyển đổi

            // Xóa văn bản tìm kiếm khi một khách hàng được chọn
            binding.searchView.setQuery("", false); // Xóa văn bản tìm kiếm
            binding.searchView.clearFocus(); // Ẩn con trỏ
        });
    }

    private void hienThiDSKhachHang() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("duyetkhachhang");
        List<String> dsIDKhachHang = new ArrayList<>();

        String key = emailLogin.substring(0, emailLogin.indexOf("@"));
        db.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Log.d("l.d", "Database error: " + databaseError.getMessage());
            }
        });
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
                        adapter.sortKhachHangList();  // Sắp xếp nếu cần thiết
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