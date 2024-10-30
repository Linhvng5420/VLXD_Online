package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tdc.vlxdonline.Adapter.CategoryAdapter;
import com.tdc.vlxdonline.Adapter.SanPham_Adapter;
import com.tdc.vlxdonline.Model.Categorys;
import com.tdc.vlxdonline.Model.SanPham_Model;
import com.tdc.vlxdonline.R;

import java.util.ArrayList;
import java.util.List;

public class Warehouse_DanhMucActivity extends AppCompatActivity {
    EditText edtNhapDM;
    Button btnThemDM, btnXoaDM, btnSuaDM;
    ImageView ivAnhDM;
    Uri uri;
    String imagesUrl;
    CategoryAdapter adapter;
    Categorys category = new Categorys();
    List<Categorys> list_DM = new ArrayList<>();
    ValueEventListener listener;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.danhmuc_layout);
        setCtronl();
        getDate();
        setEvent();
    }

    private void setEvent() {
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            ivAnhDM.setImageURI(uri);
                        } else {
                            Toast.makeText(Warehouse_DanhMucActivity.this, "Khong chon anh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        ivAnhDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        btnThemDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
        btnXoaDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy tên sản phẩm từ EditText
                String id = category.getId();
                if (!id.isEmpty()) {
                    // Gọi phương thức xóa sản phẩm
                    deleteProduct(id);
                } else {
                    Toast.makeText(Warehouse_DanhMucActivity.this, "Vui lòng chọn sản phẩm để xóa", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSuaDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtNhapDM.getText().toString().isEmpty()) {
                    Toast.makeText(Warehouse_DanhMucActivity.this, "Vui lòng chọn sản phẩm để sửa!", Toast.LENGTH_SHORT).show();
                } else {
                    uploadData();  // Gọi phương thức để cập nhật dữ liệu
                }
            }
        });
        // Đảm bảo Adapter đã được khởi tạo trước khi thiết lập sự kiện click
        if (adapter != null) {
            adapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    // Xử lý sự kiện click vào sản phẩm
                    if (position != RecyclerView.NO_POSITION) {
                        if (!list_DM.get(position).getId().equals(category.getId())) {
                            btnSuaDM.setEnabled(true);
                            btnThemDM.setEnabled(false);
                            category = list_DM.get(position);

                            // Hiển thị thông tin sản phẩm lên các EditText
                            edtNhapDM.setText(category.getTen());
                            // Hiển thị hình ảnh sản phẩm
                            Glide.with(Warehouse_DanhMucActivity.this)
                                    .load(category.getAnh())
                                    .into(ivAnhDM);
                        } else {
                            category = new Categorys();
                            edtNhapDM.setText("");
                            ivAnhDM.setImageResource(R.drawable.add_a_photo_24);
                            btnSuaDM.setEnabled(false);
                            btnThemDM.setEnabled(true);
                        }

                    }
                }
            });
        } else {
            Toast.makeText(this, "Adapter chưa được khởi tạo", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDate() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new CategoryAdapter(Warehouse_DanhMucActivity.this, list_DM);
        recyclerView.setAdapter(adapter);
        reference = FirebaseDatabase.getInstance().getReference();
        listener = reference.child("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_DM.clear();
                for (DataSnapshot items : snapshot.getChildren()) {
                    Categorys categorys = items.getValue(Categorys.class);
                    list_DM.add(categorys);
                }
                // Notify adapter sau khi có dữ liệu
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }

    public void uploadData() {
        if (!edtNhapDM.getText().toString().isEmpty()) {
            if (uri != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("category Images")
                        .child(uri.getLastPathSegment());
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri urlImage = uriTask.getResult();
                        imagesUrl = urlImage.toString();
                        saveDate();
                    }
                });
            } else {
                saveDate();
            }
        }
    }

    private void saveDate() {
        if (category.getId() != null) {
            category.setTen(edtNhapDM.getText().toString());
            category.setAnh(uri != null ? imagesUrl.toString() : category.getAnh());  // Nếu bạn không cần thay đổi ảnh
            reference.child("category").child(category.getId()).setValue(category);

            Toast.makeText(this, "Đổi tên thành công", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct(String id) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("category").child(id);
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Warehouse_DanhMucActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    // Xóa dữ liệu trên giao diện người dùng
                    edtNhapDM.setText("");
                    ivAnhDM.setImageResource(0); // Xóa hình ảnh
                } else {
                    Toast.makeText(Warehouse_DanhMucActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setCtronl() {
        edtNhapDM = findViewById(R.id.edtNhapDM);
        ivAnhDM = findViewById(R.id.ivAnhDM);
        btnThemDM = findViewById(R.id.btnThemDM);
        btnXoaDM = findViewById(R.id.btnXoaDM);
        btnSuaDM = findViewById(R.id.btnSuaDM);
        recyclerView = findViewById(R.id.recycleviewDM);
    }
}