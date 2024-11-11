package com.tdc.vlxdonline.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.tdc.vlxdonline.databinding.FragmentOwnerSettingStkBinding;

import java.util.ArrayList;

public class Owner_SettingSTKFragment extends Fragment {

    FragmentOwnerSettingStkBinding binding;
    DatabaseReference mDatabase;
    ArrayAdapter<String> mAdapter;
    ArrayList<String> listData;

    String idLoginChuCH = LoginActivity.idUser.substring(0, LoginActivity.idUser.indexOf("@"));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOwnerSettingStkBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupToolbar(root);

        listData = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listData);
        binding.lstCH.setAdapter(mAdapter);

        mDatabase.child("thongtinchustk").child(idLoginChuCH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String data = snapshot.getValue(String.class);
                    listData.add(data);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        binding.lstCH.setOnItemClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= listData.size()) {
                return;  // Nếu vị trí không hợp lệ, thoát khỏi hàm
            }

            // Đặt màu nền cho mục đã chọn
            for (int i = 0; i < parent.getChildCount(); i++) {
                if (i == position) {
                    parent.getChildAt(i).setBackgroundColor(Color.GREEN);  // Mục đã chọn
                } else {
                    parent.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);  // Mục chưa chọn
                }
            }

            binding.btnXoa.setOnClickListener(v -> {
                if (position < 0 || position >= listData.size()) {
                    return;  // Kiểm tra lại lần nữa trước khi xóa
                }

                String itemToDelete = listData.get(position);

                // Xóa mục khỏi Firebase
                mDatabase.child("thongtinchustk").child(idLoginChuCH)
                        .orderByValue().equalTo(itemToDelete).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().removeValue()
                                            .addOnSuccessListener(aVoid -> {
                                                if (position >= 0 && position < listData.size()) {
                                                    listData.remove(position);
                                                    Snackbar.make(getView(), "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                                                    mAdapter.notifyDataSetChanged();
                                                }
                                            })
                                            .addOnFailureListener(e -> Snackbar.make(getView(), "Lỗi khi xóa!", Toast.LENGTH_SHORT).show());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        });
            });
        });

        binding.btnThem.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Thêm Tài Khoản");

            // Create an input view with 3 TextViews
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);

            EditText edtBankName = new EditText(getContext());
            edtBankName.setHint("Tên Ngân Hàng");
            EditText edtAccountNumber = new EditText(getContext());
            edtAccountNumber.setHint("Số Tài Khoản");
            edtAccountNumber.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            EditText edtOwnerName = new EditText(getContext());
            edtOwnerName.setHint("Tên Chủ Tài Khoản");

            layout.addView(edtBankName);
            layout.addView(edtAccountNumber);
            layout.addView(edtOwnerName);
            builder.setView(layout);

            builder.setPositiveButton("Thêm", (dialog, which) -> {
                String bankName = edtBankName.getText().toString().toUpperCase();
                String accountNumber = edtAccountNumber.getText().toString().toUpperCase();
                String ownerName = edtOwnerName.getText().toString().toUpperCase();

                if (!bankName.isEmpty() && !accountNumber.isEmpty() && !ownerName.isEmpty()) {
                    String data = bankName + "-" + accountNumber + "-" + ownerName;

                    // Add the new data to Firebase under "chu1"
                    mDatabase.child("thongtinchustk").child(idLoginChuCH).push().setValue(data)
                            .addOnSuccessListener(aVoid -> {
                                Snackbar.make(getView(), "Thêm thành công!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Snackbar.make(getView(), "Thêm thất bại!", Toast.LENGTH_SHORT).show());
                } else {
                    Snackbar.make(getView(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        return root;
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