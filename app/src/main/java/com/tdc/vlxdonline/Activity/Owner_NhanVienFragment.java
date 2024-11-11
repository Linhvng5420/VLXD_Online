package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.NhanVienAdapter;
import com.tdc.vlxdonline.Model.NhanVien;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerNhanvienBinding;

import java.util.ArrayList;
import java.util.List;

public class Owner_NhanVienFragment extends Fragment {
    private FragmentOwnerNhanvienBinding binding;

    // Email mà tài khoản quản lý đang đăng nhập
    String emailLogin = LoginActivity.idUser;

    // Lưu lại danh sách nhân viên ban đầu trước khi tìm kiếm
    private List<NhanVien> dsNhanVien = new ArrayList<>();
    private NhanVienAdapter nhanVienAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOwnerNhanvienBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // TODO: HÀM XỬ LÝ CHỨC NĂNG CỦA VIEW APP
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //RecycleView: Thiết lập layout cho RecyclerView, sử dụng LinearLayoutManager để hiển thị danh sách theo chiều dọc
        binding.ownerRcvNhanVien.setLayoutManager(new LinearLayoutManager(getContext()));
        // Khởi tạo danh sách nhân viên trống và adapter
        List<NhanVien> nhanVienList = new ArrayList<>();
        nhanVienAdapter = new NhanVienAdapter(nhanVienList);
        nhanVienAdapter.sortNhanVienList();
        binding.ownerRcvNhanVien.setAdapter(nhanVienAdapter);

        // Firebase: lấy dữ liệu từ Firebase
        getDataNhanVien();

        // Bắt sự kiện khi nhấn vào recycleview nhân viên
        setupNhanItemRecycleView();

        // Bắt sự kiện khi nhấn nút thêm nhân viên
        setupThemNhanVien();

        // Thiết lập tìm kiếm
        setupTimKiemNhanVien();

        // Lắng nghe sự kiện nhấn ra ngoài thanh tìm kiếm để tắt con trỏ và ẩn bàn phím
        binding.getRoot().setOnTouchListener((v, event) -> {
            hideKeyboard(v); // Ẩn bàn phím
            binding.searchView.clearFocus(); // Xóa focus để tắt con trỏ trong SearchView
            return false;
        });
    }

    private void getDataNhanVien() {
        // TODO: Thoát ứng dụng khi chưa đăng nhập mà vào được trang quản lý
        if (emailLogin == null) {
            Snackbar.make(getView(), "Bạn không có quyền truy cập \nQuản lý Nhân Viên", Toast.LENGTH_SHORT).show();
            requireActivity().finishAffinity();
        }

        // Firebase: Khởi tạo databaseReference và lấy dữ liệu từ Firebase
        DatabaseReference dbNhanVien = FirebaseDatabase.getInstance().getReference("nhanvien");
        dbNhanVien.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Xóa danh sách cũ trước khi thêm dữ liệu mới
                nhanVienAdapter.getNhanVienList().clear();
                dsNhanVien.clear();

                // Lặp qua tất cả các DataSnapshot con để lấy thông tin nhân viên
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy đối tượng NhanVien từ snapshot
                    NhanVien nhanVien = new NhanVien();
                    nhanVien = snapshot.getValue(NhanVien.class);
                    if (nhanVien != null) {
                        // Set ID NV là snapshot key
                        nhanVien.setCccd(snapshot.getKey());

                        // Lọc theo nhân viên của Chủ CH theo email
                        // Nếu emailUser là admin, hiển thị tất cả nhân viên có trong Firebase
                        if (emailLogin != null && "admin@tdc.com".equals(emailLogin)) {
                            nhanVienAdapter.getNhanVienList().add(nhanVien);
                            dsNhanVien.add(nhanVien); // Lưu vào danh sách gốc
                        } else
                            // Chủ cửa hàng đăng nhập, hiển thị nhân viên của chủ cửa hàng
                            if (emailLogin != null && nhanVien.getEmailchu().equals(emailLogin)) {
                                nhanVienAdapter.getNhanVienList().add(nhanVien);
                                dsNhanVien.add(nhanVien); // Lưu vào danh sách gốc
                            }
                    } else
                        Snackbar.make(getView(), "Danh Sách Nhân Viên Rỗng", Toast.LENGTH_SHORT).show();
                }

                // Sắp xếp danh sách theo mã NV
                nhanVienAdapter.sortNhanVienList();
                // Thông báo cho adapter cập nhật dữ liệu
                nhanVienAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // BẮT SỰ KIỆN THIẾT LẬP SỰ KIỆN KHI NHẤN VÀO MỘT ITEM TRONG DANH SÁCH NHÂN VIÊN
    private void setupNhanItemRecycleView() {
        nhanVienAdapter.setOnItemClickListener(nhanVien -> {
            // Tạo Bundle để truyền thông tin nhân viên được chọn qua Fragment Detail
            Bundle bundleIDNhanVien = new Bundle();
            bundleIDNhanVien.putSerializable("idNhanVien", nhanVien.getCccd()); // Đưa dữ liệu ID nhân viên vào Bundle

            // Tạo một instance, nó giúp chúng ta chuyển đổi dữ liệu từ Fragment này sang Fragment khác
            Owner_NhanVienDetailFragment nhanVienDetailFragment = new Owner_NhanVienDetailFragment();
            // Gán Bundle (chứa thông tin id nhân viên) vào cho Fragment chi tiết
            nhanVienDetailFragment.setArguments(bundleIDNhanVien);

            // Thực hiện chuyển đổi sang Fragment chi tiết, thay thế Fragment hiện tại
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_owner, nhanVienDetailFragment) // Thay thế fragment_owner hiện tại bằng fragment chi tiết
                    .addToBackStack(null) // Cho phép quay lại màn hình trước khi nhấn nút Back
                    .commit(); // Thực hiện chuyển đổi

            // Xóa văn bản tìm kiếm khi một nhân viên được chọn
            binding.searchView.setQuery("", false); // Xóa văn bản tìm kiếm
            binding.searchView.clearFocus(); // Ẩn con trỏ
        });
    }

    // BẮT SỰ KIỆN NHẤN VÀO NÚT THÊM NHÂN VIÊN
    private void setupThemNhanVien() {
        binding.btnThemNhanVien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // chuyển sang fragment thêm nhân viên
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_owner, new Owner_NhanVienAddFragment())
                        .addToBackStack(null)
                        .commit();

                // Xóa văn bản tìm kiếm khi một sự kiện khác xảy ra
                binding.searchView.setQuery("", false); // Xóa văn bản tìm kiếm
                binding.searchView.clearFocus(); // Ẩn con trỏ
            }
        });
    }

    // CHỨC NĂNG TÌM KIẾM NHÂN VIÊN
    private void setupTimKiemNhanVien() {
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
                    nhanVienAdapter.updateList(new ArrayList<>(dsNhanVien)); // Cập nhật lại danh sách ban đầu
                } else {
                    // Gọi hàm filter để tìm kiếm nhân viên
                    filterNhanVien(newText);
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

    private void filterNhanVien(String query) {
        List<NhanVien> filteredList = new ArrayList<>();

        for (NhanVien nhanVien : dsNhanVien) {
            // Kiểm tra nếu tên hoặc ID của nhân viên chứa từ khóa tìm kiếm (không phân biệt chữ hoa/chữ thường)
            if (nhanVien.getTennv().toLowerCase().contains(query.toLowerCase()) ||
                    nhanVien.getCccd().toLowerCase().contains(query.toLowerCase()) ||
                    nhanVien.getSdt().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(nhanVien);
            }
        }

        // Cập nhật danh sách đã lọc vào adapter
        nhanVienAdapter.updateList(filteredList);
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


