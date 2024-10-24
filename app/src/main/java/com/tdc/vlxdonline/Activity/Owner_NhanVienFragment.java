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
import com.tdc.vlxdonline.Adapter.NhanVienAdapter;
import com.tdc.vlxdonline.Model.NhanVien;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerNhanvienBinding;

import java.util.ArrayList;
import java.util.List;

public class Owner_NhanVienFragment extends Fragment {

    // Khai báo đối tượng binding để liên kết với layout của Fragment
    private FragmentOwnerNhanvienBinding ownerNhanvienBinding;
    // Adapter để hiển thị danh sách nhân viên
    private NhanVienAdapter nhanVienAdapter;

    // FIREBASE: Khai báo DatabaseReference
    private DatabaseReference databaseReference;

    // Login Data
    String emailUser = null;

    // Lưu lại danh sách nhân viên ban đầu trước khi tìm kiếm
    private List<NhanVien> originalList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Khởi tạo View Binding cho Fragment và liên kết với layout fragment_owner_nhanvien.xml
        ownerNhanvienBinding = FragmentOwnerNhanvienBinding.inflate(inflater, container, false);

        // Trả về đối tượng View được tạo từ binding, đây là root view của Fragment
        return ownerNhanvienBinding.getRoot();
    }

    // TODO: HÀM XỬ LÝ CHỨC NĂNG CỦA VIEW APP
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nhận emailUser từ Bundle
        if (getArguments() != null) {
            emailUser = getArguments().getString("emailUser");
            Log.d("l.e", "onViewCreated: emailUser = " + emailUser);
        } else Log.d("l.e", "Nhận emailUser từ Bundle: getArguments() = null");

        //RecycleView: Thiết lập layout cho RecyclerView, sử dụng LinearLayoutManager để hiển thị danh sách theo chiều dọc
        ownerNhanvienBinding.ownerRcvNhanVien.setLayoutManager(new LinearLayoutManager(getContext()));
        // Khởi tạo danh sách nhân viên trống và adapter
        List<NhanVien> nhanVienList = new ArrayList<>();
        nhanVienAdapter = new NhanVienAdapter(nhanVienList);
        ownerNhanvienBinding.ownerRcvNhanVien.setAdapter(nhanVienAdapter);

        // Firebase: lấy dữ liệu từ Firebase
        getNhanVienData();

        //nhấn vào recycleview nhân viên
        nhanVaoItemNhanVien();

        // nhấn nút thêm nhân viên
        nhanNutThemNhanVien();

        // Thiết lập tìm kiếm
        timKiemNhanVien();
        // Lắng nghe sự kiện nhấn ra ngoài thanh tìm kiếm để tắt con trỏ và ẩn bàn phím
        ownerNhanvienBinding.getRoot().setOnTouchListener((v, event) -> {
            hideKeyboard(v); // Ẩn bàn phím
            ownerNhanvienBinding.searchView.clearFocus(); // Xóa focus để tắt con trỏ trong SearchView
            return false;
        });
    }

    private void getNhanVienData() {
        // Firebase: Khởi tạo databaseReference và lấy dữ liệu từ Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("nhanvien");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Xóa danh sách cũ trước khi thêm dữ liệu mới
                nhanVienAdapter.getNhanVienList().clear();
                originalList.clear(); // Xóa danh sách gốc để thêm dữ liệu mới

                // Lặp qua tất cả các DataSnapshot con để lấy thông tin nhân viên
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy đối tượng NhanVien từ snapshot
                    NhanVien nhanVien = snapshot.getValue(NhanVien.class);
                    if (nhanVien != null) {
                        // Set idnv là document ID
                        nhanVien.setIdnv(snapshot.getKey());

                        // Lọc theo email chu
                        // Nếu emailUser là admin, thêm tất cả nhân viên mà không kiểm tra điều kiện khác
                        if ("admin@tdc.com".equals(emailUser)) {
                            nhanVienAdapter.getNhanVienList().add(nhanVien);
                            originalList.add(nhanVien); // Lưu vào danh sách gốc
                        } else if (emailUser != null && nhanVien.getEmailchu().equals(emailUser)) {
                            // Điều kiện email chủ thông thường
                            nhanVienAdapter.getNhanVienList().add(nhanVien);
                            originalList.add(nhanVien); // Lưu vào danh sách gốc
                        }
                    }
                }

                // Sắp xếp danh sách theo mã NV
                nhanVienAdapter.sortNhanVienList();

                // Thông báo cho adapter cập nhật dữ liệu
                nhanVienAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }


    // BẮT SỰ KIỆN THIẾT LẬP SỰ KIỆN KHI NHẤN VÀO MỘT ITEM TRONG DANH SÁCH NHÂN VIÊN
    private void nhanVaoItemNhanVien() {
        nhanVienAdapter.setOnItemClickListener(nhanVien -> {
            // Tạo Bundle để truyền thông tin nhân viên được chọn qua Fragment Detail
            Bundle bundleIDNhanVien = new Bundle();
            bundleIDNhanVien.putSerializable("selectedIDNhanVien", nhanVien.getIdnv()); // Đưa dữ liệu ID nhân viên vào Bundle

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
            ownerNhanvienBinding.searchView.setQuery("", false); // Xóa văn bản tìm kiếm
            ownerNhanvienBinding.searchView.clearFocus(); // Ẩn con trỏ
        });
    }

    // BẮT SỰ KIỆN NHẤN VÀO NÚT THÊM NHÂN VIÊN
    private void nhanNutThemNhanVien() {
        ownerNhanvienBinding.btnThemNhanVien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // chuyển sang fragment thêm nhân viên
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_owner, new Owner_NhanVienAddFragment())
                        .addToBackStack(null)
                        .commit();

                // Xóa văn bản tìm kiếm khi một nhân viên được chọn
                ownerNhanvienBinding.searchView.setQuery("", false); // Xóa văn bản tìm kiếm
                ownerNhanvienBinding.searchView.clearFocus(); // Ẩn con trỏ
            }
        });
    }

    // CHỨC NĂNG TÌM KIẾM NHÂN VIÊN
    private void timKiemNhanVien() {
        ownerNhanvienBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Không cần xử lý khi submit, chỉ thực hiện tìm kiếm ngay khi người dùng nhập
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Kiểm tra nếu từ khóa rỗng, trả lại danh sách ban đầu
                if (newText.isEmpty()) {
                    nhanVienAdapter.updateList(new ArrayList<>(originalList)); // Cập nhật lại danh sách ban đầu
                } else {
                    // Gọi hàm filter để tìm kiếm nhân viên
                    filterNhanVien(newText);
                }
                return true;
            }
        });

        // Tắt con trỏ khi SearchView bị mất focus
        ownerNhanvienBinding.searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                ownerNhanvienBinding.searchView.clearFocus(); // Xóa focus khi mất focus
            }
        });
    }

    private void filterNhanVien(String query) {
        List<NhanVien> filteredList = new ArrayList<>();

        for (NhanVien nhanVien : originalList) {
            // Kiểm tra nếu tên hoặc ID của nhân viên chứa từ khóa tìm kiếm (không phân biệt chữ hoa/chữ thường)
            if (nhanVien.getTennv().toLowerCase().contains(query.toLowerCase()) ||
                    nhanVien.getIdnv().toLowerCase().contains(query.toLowerCase())) {
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
        ownerNhanvienBinding = null;
    }
}


