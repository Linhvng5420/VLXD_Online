package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.tdc.vlxdonline.Adapter.Category_Adapter;
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
    Category_Adapter adapter;
    Categorys category = new Categorys();
    List<Categorys> list_DM = new ArrayList<>();
    ValueEventListener listener;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private ProgressDialog progressDialog;

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang thêm ảnh...");
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.danhmuc_layout);
        initProgressDialog();
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
                if (
                        uri == null ||
                                edtNhapDM.getText().toString().trim().isEmpty()) {

                    Toast.makeText(Warehouse_DanhMucActivity.this,
                            "Vui lòng điền đầy đủ thông tin sản phẩm!", Toast.LENGTH_SHORT).show();
                } else {
                    uploadData();
                    hideKeyboard();
                }
            }
        });
        btnXoaDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy tên sản phẩm từ EditText
                String id = category.getId();
                if (!id.isEmpty()) {
                    // Gọi phương thức xóa sản phẩm
                   // deleteProduct(id);
                    showConfirmDialogXoa(id);
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
                    //uploadData();  // Gọi phương thức để cập nhật dữ liệu
                    showConfirmDialogSua();
                    hideKeyboard();
                }
            }
        });
        btnSuaDM.setEnabled(false);
        btnXoaDM.setEnabled(false);
        btnThemDM.setEnabled(true);

        // Đảm bảo Adapter đã được khởi tạo trước khi thiết lập sự kiện click
        if (adapter != null) {
            adapter.setOnItemClickListener(new Category_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    // Xử lý sự kiện click vào sản phẩm
                    if (position != RecyclerView.NO_POSITION) {
                        if (!list_DM.get(position).getId().equals(category.getId())) {
                            btnSuaDM.setEnabled(true);
                            btnXoaDM.setEnabled(true);
                            btnThemDM.setEnabled(false);
                            category = list_DM.get(position);

                            // Hiển thị thông tin sản phẩm lên các EditText
                            edtNhapDM.setText(category.getTen());
                            // Hiển thị hình ảnh sản phẩm
                            Glide.with(Warehouse_DanhMucActivity.this)
                                    .load(category.getAnh())
                                    .into(ivAnhDM);
//                           view.setBackgroundColor(Color.rgb(0,255,255));
                        } else {
                            clearSelection();
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
        adapter = new Category_Adapter(Warehouse_DanhMucActivity.this, list_DM);
        recyclerView.setAdapter(adapter);
        reference = FirebaseDatabase.getInstance().getReference();
        listener = reference.child("categorys").addValueEventListener(new ValueEventListener() {
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
                progressDialog.show();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("categorys Images")
                        .child(uri.getLastPathSegment());
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri urlImage = uriTask.getResult();
                        imagesUrl = urlImage.toString();
                        saveData();
                        progressDialog.dismiss();
                    }
                });
            } else {
                saveData();
                clearSelection();
            }
        }
    }
    private void saveData() {
        if (category.getId() != null) {
            category.setTen(edtNhapDM.getText().toString());
            category.setAnh(uri != null ? imagesUrl : category.getAnh());
            reference.child("categorys").child(category.getId()).setValue(category).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    clearSelection();
                }
            });

        } else {
            String newCategoryId = reference.child("categorys").push().getKey();
            category.setId(newCategoryId);
            category.setTen(edtNhapDM.getText().toString());
            category.setAnh(uri != null ? imagesUrl : null);
            reference.child("categorys").child(newCategoryId).setValue(category).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                    clearSelection();
                }
            });
        }
        uri = null;
    }

    private void deleteProduct(String id) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("categorys").child(id);
        productRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Warehouse_DanhMucActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                clearSelection();
            } else {
                Toast.makeText(Warehouse_DanhMucActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearSelection() {
        category = new Categorys();
        edtNhapDM.setText("");
        ivAnhDM.setImageResource(R.drawable.add_a_photo_24);
        btnSuaDM.setEnabled(false);
        btnXoaDM.setEnabled(false);
        btnThemDM.setEnabled(true);
    }
    private void showConfirmDialogXoa(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Warehouse_DanhMucActivity.this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa danh mục này không?");

        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct(id);
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showConfirmDialogSua() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Warehouse_DanhMucActivity.this);
        builder.setTitle("Xác nhận sửa");
        builder.setMessage("Bạn có chắc chắn muốn sửa danh mục này không?");

        builder.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadData();
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
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