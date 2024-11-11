package com.tdc.vlxdonline.Activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentOwnerSettingSodiachiBinding;

import java.util.ArrayList;
import java.util.List;

public class Owner_SettingSoDiaChiFragment extends Fragment {
    FragmentOwnerSettingSodiachiBinding binding;
    DatabaseReference databaseReference;

    String idLoginChuCH = LoginActivity.idUser.substring(0, LoginActivity.idUser.indexOf("@"));

    private String selectedCH = null;
    private String selectedKho = null;
    private String selectedCSKH = null;

    // SparseBooleanArray để theo dõi mục nào đang được chọn
    private SparseBooleanArray selectedPositionsCH = new SparseBooleanArray();
    private SparseBooleanArray selectedPositionsKho = new SparseBooleanArray();
    private SparseBooleanArray selectedPositionsCSKH = new SparseBooleanArray();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("thongtinchusdc/" + idLoginChuCH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOwnerSettingSodiachiBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setupToolbar(view);

        setupButtonFunctions();
        setupListViewListeners();
        loadData();

        return view;
    }

    private void loadData() {
        // Lấy và hiển thị danh sách cửa hàng
        databaseReference.child("cuahang").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> cuahangList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue(String.class);
                    if (value != null) cuahangList.add(value);
                }
                ArrayAdapter<String> adapterCH = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, cuahangList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        // Đổi màu nền nếu mục đang được chọn
                        if (selectedPositionsCH.get(position, false)) {
                            view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                        } else {
                            view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        }
                        return view;
                    }
                };
                binding.lstCH.setAdapter(adapterCH);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // Lấy và hiển thị danh sách kho
        databaseReference.child("kho").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> khoList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue(String.class);
                    if (value != null) khoList.add(value);
                }
                ArrayAdapter<String> adapterKho = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, khoList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        if (selectedPositionsKho.get(position, false)) {
                            view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                        } else {
                            view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        }
                        return view;
                    }
                };
                binding.lstKho.setAdapter(adapterKho);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // Lấy và hiển thị danh sách CSKH
        databaseReference.child("cskh").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> cskhList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue(String.class);
                    if (value != null) cskhList.add(value);
                }
                ArrayAdapter<String> adapterCSKH = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, cskhList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        if (selectedPositionsCSKH.get(position, false)) {
                            view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                        } else {
                            view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        }
                        return view;
                    }
                };
                binding.lstCSKH.setAdapter(adapterCSKH);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void setupListViewListeners() {
        // Chọn mục trong danh sách cửa hàng
        binding.lstCH.setOnItemClickListener((parent, view, position, id) -> {
            selectedCH = (String) parent.getItemAtPosition(position);
            selectedPositionsCH.clear(); // Xóa trạng thái chọn trước đó
            selectedPositionsCH.put(position, true); // Đánh dấu mục mới được chọn
            ((ArrayAdapter) binding.lstCH.getAdapter()).notifyDataSetChanged();
        });

        // Chọn mục trong danh sách kho
        binding.lstKho.setOnItemClickListener((parent, view, position, id) -> {
            selectedKho = (String) parent.getItemAtPosition(position);
            selectedPositionsKho.clear();
            selectedPositionsKho.put(position, true);
            ((ArrayAdapter) binding.lstKho.getAdapter()).notifyDataSetChanged();
        });

        // Chọn mục trong danh sách CSKH
        binding.lstCSKH.setOnItemClickListener((parent, view, position, id) -> {
            selectedCSKH = (String) parent.getItemAtPosition(position);
            selectedPositionsCSKH.clear();
            selectedPositionsCSKH.put(position, true);
            ((ArrayAdapter) binding.lstCSKH.getAdapter()).notifyDataSetChanged();
        });
    }

    private void setupButtonFunctions() {
        // Thêm cửa hàng
        binding.btnThemCH.setOnClickListener(v -> showAddDialogAdd("cuahang"));

        // Xóa cửa hàng
        binding.btnXoaCH.setOnClickListener(v -> {
            if (selectedCH != null) {
                deleteItemFromList("cuahang", selectedCH);
                selectedCH = null; // Reset selection
            } else {
                Snackbar.make(getView(), "Vui lòng chọn địa chỉ cửa hàng để xóa.", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm kho
        binding.btnThemKho.setOnClickListener(v -> showAddDialogAdd("kho"));

        // Xóa kho
        binding.btnXoaKho.setOnClickListener(v -> {
            if (selectedKho != null) {
                deleteItemFromList("kho", selectedKho);
                selectedKho = null; // Reset selection
            } else {
                Snackbar.make(getView(), "Vui lòng chọn địa chỉ kho để xóa.", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm CSKH
        binding.btnThemCSKH.setOnClickListener(v -> showAddDialogAdd("cskh"));

        // Xóa CSKH
        binding.btnXoaCSKH.setOnClickListener(v -> {
            if (selectedCSKH != null) {
                deleteItemFromList("cskh", selectedCSKH);
                selectedCSKH = null; // Reset selection
            } else {
                Snackbar.make(getView(), "Vui lòng chọn địa chỉ CSKH để xóa.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddDialogAdd(String listKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thêm địa chỉ");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String newAddress = input.getText().toString().trim();
            if (!newAddress.isEmpty()) {
                databaseReference.child(listKey).push().setValue(newAddress)
                        .addOnSuccessListener(aVoid -> loadData());
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void deleteItemFromList(String listKey, String itemToDelete) {
        databaseReference.child(listKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String value = itemSnapshot.getValue(String.class);
                    if (value != null && value.equals(itemToDelete)) {
                        itemSnapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> loadData());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    // CUỐI: THIẾT LẬP TOOLBAR VÀ ĐIỀU HƯỚNG
    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Xử lý khi nhấn nút quay về trên Toolbar
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }
}
