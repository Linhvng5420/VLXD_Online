package com.tdc.vlxdonline.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.DonVi_Adapter;

import com.tdc.vlxdonline.Adapter.SanPham_Adapter;
import com.tdc.vlxdonline.Model.Categorys;
import com.tdc.vlxdonline.Model.DonVi;

import com.tdc.vlxdonline.Model.SanPham_Model;
import com.tdc.vlxdonline.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Warehouse_DonViActivity extends AppCompatActivity {
    EditText edtNhapDV;
    Button btnThem, btnXoa;
    DonVi_Adapter adapter;
    DonVi donVi = new DonVi();
    List<DonVi> list_DV = new ArrayList<>();
    RecyclerView recyclerView;
    ValueEventListener listener;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donvi_layout);
        setCtronl();
        getDate();
        setEvent();
    }

    private void saveDate() {
        try {
            if (!edtNhapDV.getText().toString().isEmpty()){
                donVi.setId(Long.parseLong(System.currentTimeMillis() + ""));
                donVi.setTen(edtNhapDV.getText().toString());

                reference.child("DonVi").child(String.valueOf(donVi.getId())).setValue(donVi);
            }else {
                Toast.makeText(this, "Chưa Nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
    }

    private void getDate() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new DonVi_Adapter(Warehouse_DonViActivity.this, list_DV);
        recyclerView.setAdapter(adapter);
        reference = FirebaseDatabase.getInstance().getReference();
        listener = reference.child("DonVi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_DV.clear();
                for (DataSnapshot items : snapshot.getChildren()) {
                    DonVi donVi = items.getValue(DonVi.class);
                    list_DV.add(donVi);
                }
                // Notify adapter sau khi có dữ liệu
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }

    private void setEvent() {
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDate();
                edtNhapDV.setText("");
            }
        });
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy tên sản phẩm từ EditText
                String id = String.valueOf(donVi.getId());
                if (!id.isEmpty()) {
                    // Gọi phương thức xóa sản phẩm
                    deleteProduct(id);
                } else {
                    Toast.makeText(Warehouse_DonViActivity.this, "Vui lòng chọn sản phẩm để xóa", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Đảm bảo Adapter đã được khởi tạo trước khi thiết lập sự kiện click
        if (adapter != null) {
            adapter.setOnItemClickListener(new DonVi_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    // Xử lý sự kiện click vào sản phẩm
                    if (position != RecyclerView.NO_POSITION) {
                        if (!Objects.equals(list_DV.get(position).getId(), donVi.getId())) {
                            btnThem.setEnabled(false);
                            donVi = list_DV.get(position);

                            // Hiển thị thông tin sản phẩm lên các EditText
                            edtNhapDV.setText(donVi.getTen());

                        } else {
                            donVi = new DonVi();
                            edtNhapDV.setText("");
                            btnThem.setEnabled(true);
                        }

                    }
                }
            });
        } else {
            Toast.makeText(this, "Adapter chưa được khởi tạo", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct(String id) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("DonVi").child(id);
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Warehouse_DonViActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    // Xóa dữ liệu trên giao diện người dùng
                    edtNhapDV.setText("");
                } else {
                    Toast.makeText(Warehouse_DonViActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setCtronl() {
        edtNhapDV = findViewById(R.id.edtNhapDV);
        btnThem = findViewById(R.id.btnThemDV);
        btnXoa = findViewById(R.id.btnXoaDV);
        recyclerView = findViewById(R.id.recycleviewDV);
    }
}
