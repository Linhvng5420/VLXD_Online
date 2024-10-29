package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.tdc.vlxdonline.Adapter.AnhSP_Adapter;
import com.tdc.vlxdonline.Adapter.Banner_Adapter;
import com.tdc.vlxdonline.Adapter.CategoryAdapter;
import com.tdc.vlxdonline.Model.AnhSP;
import com.tdc.vlxdonline.Model.Banner;
import com.tdc.vlxdonline.Model.Categorys;
import com.tdc.vlxdonline.R;

import java.util.ArrayList;
import java.util.List;

public class Warehouse_AnhSPActivity extends AppCompatActivity {
    EditText edtChonASP;
    Button btnThemASP, btnXoaASP;
    ImageView ivAnhSP;
    Uri uri;
    String imagesUrl;
    AnhSP_Adapter adapter;
    AnhSP anhSP = new AnhSP();
    List<AnhSP> list_ASP = new ArrayList<>();
    ValueEventListener listener;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qlanhsp_layout);
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
                            assert data != null;
                            uri = data.getData();
                            ivAnhSP.setImageURI(uri);
                        } else {
                            Toast.makeText(Warehouse_AnhSPActivity.this, "Khong chon anh", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ivAnhSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        btnThemASP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
        btnXoaASP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy tên sản phẩm từ EditText
                String id = anhSP.getId();
                if (!id.isEmpty()) {
                    // Gọi phương thức xóa sản phẩm
                    deleteProduct(id);
                } else {
                    Toast.makeText(Warehouse_AnhSPActivity.this, "Vui lòng chọn sản phẩm để xóa", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Đảm bảo Adapter đã được khởi tạo trước khi thiết lập sự kiện click
        if (adapter != null) {
            adapter.setOnItemClickListener(new AnhSP_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    // Xử lý sự kiện click vào sản phẩm
                    if (position != RecyclerView.NO_POSITION) {
                        if (!list_ASP.get(position).getId().equals(anhSP.getId())) {
                            btnThemASP.setEnabled(false);
                            anhSP = list_ASP.get(position);

                            // Hiển thị thông tin sản phẩm lên các EditText
                            // Hiển thị hình ảnh sản phẩm
                            Glide.with(Warehouse_AnhSPActivity.this)
                                    .load(anhSP.getAnhSP())
                                    .into(ivAnhSP);
                        }else {
                            anhSP = new AnhSP();
                            ivAnhSP.setImageResource(R.drawable.add_a_photo_24);
                            btnThemASP.setEnabled(true);
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
        adapter = new AnhSP_Adapter(Warehouse_AnhSPActivity.this, list_ASP) {
            @Override
            public void onItemClick(int position) {
            }
        };
        recyclerView.setAdapter(adapter);
        reference = FirebaseDatabase.getInstance().getReference();
        listener = reference.child("QLAnhSP").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_ASP.clear();
                for (DataSnapshot items : snapshot.getChildren()) {
                    AnhSP anhSP = items.getValue(AnhSP.class);
                    list_ASP.add(anhSP);
                }
                // Cập nhật adapter sau khi có dữ liệu
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void uploadData() {
        if (uri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("QLAnhSP Images")
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

    private void saveDate() {
        if (anhSP.getId() == null) anhSP.setId(System.currentTimeMillis() + "");
        anhSP.setAnhSP(uri != null ? imagesUrl.toString() : anhSP.getAnhSP());  // Nếu bạn không cần thay đổi ảnh
        reference.child("QLAnhSP").child(anhSP.getId()).setValue(anhSP);
    }
    private void deleteProduct(String id) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("QLAnhSP").child(id);
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Warehouse_AnhSPActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    // Xóa dữ liệu trên giao diện người dùng
                    ivAnhSP.setImageResource(0); // Xóa hình ảnh
                } else {
                    Toast.makeText(Warehouse_AnhSPActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setCtronl() {
        ivAnhSP = findViewById(R.id.ivAnhSP);
        btnThemASP = findViewById(R.id.btnThemASP);
        btnXoaASP = findViewById(R.id.btnXoaASP);
        recyclerView = findViewById(R.id.recycleviewQLASP);
    }
}
