package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.tdc.vlxdonline.Adapter.Banner_Adapter;
import com.tdc.vlxdonline.Model.Banner;
import com.tdc.vlxdonline.R;

import java.util.ArrayList;
import java.util.List;

public class Warehouse_BannerActivity extends AppCompatActivity {
    Button btnThemBanner, btnXoaBanner;
    ImageView ivAnhBanner;
    Uri uri;
    String imagesUrl;
    Banner_Adapter adapter;
    Banner banner = new Banner();
    List<Banner> list_banner = new ArrayList<>();
    ValueEventListener listener;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qlbanner_layout);
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
                            ivAnhBanner.setImageURI(uri);
                        } else {
                            Toast.makeText(Warehouse_BannerActivity.this, "Khong chon anh", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ivAnhBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        btnThemBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadData();
            }
        });
        btnXoaBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a banner is selected
                if (banner.getId() != null && !banner.getId().isEmpty()) {
                    deleteProduct(banner.getId());
                } else {
                    Toast.makeText(Warehouse_BannerActivity.this, "Vui lòng chọn banner để xóa", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Đảm bảo Adapter đã được khởi tạo trước khi thiết lập sự kiện click
        if (adapter != null) {
            adapter.setOnItemClickListener(new Banner_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (position != RecyclerView.NO_POSITION) {
                        if (!list_banner.get(position).getId().equals(banner.getId())) {
                            btnThemBanner.setEnabled(false);
                            banner = list_banner.get(position);
                            Glide.with(Warehouse_BannerActivity.this)
                                    .load(banner.getAnhBanner())
                                    .into(ivAnhBanner);
                        } else {
                            banner = new Banner();
                            ivAnhBanner.setImageResource(R.drawable.add_a_photo_24);
                            btnThemBanner.setEnabled(true);
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, "Adapter chưa được khởi tạo", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDate() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new Banner_Adapter(Warehouse_BannerActivity.this, list_banner) {
            @Override
            public void onItemClick(int position) {
            }
        };
        recyclerView.setAdapter(adapter);
        reference = FirebaseDatabase.getInstance().getReference();
        listener = reference.child("Banner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_banner.clear();
                for (DataSnapshot items : snapshot.getChildren()) {
                    Banner banner = items.getValue(Banner.class);
                    list_banner.add(banner);
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
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Banner Images")
                    .child(uri.getLastPathSegment());
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri urlImage = uriTask.getResult();
                    imagesUrl = urlImage.toString();
                    saveData();
                }
            });
        } else {
            Toast.makeText(this, "Vui lòng chọn ảnh trước khi thêm", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveData() {
        if (banner.getId() == null) banner.setId(System.currentTimeMillis() + "");
        banner.setAnhBanner(uri != null ? imagesUrl : banner.getAnhBanner());  // Update the banner image URL
        reference.child("Banner").child(banner.getId()).setValue(banner).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Warehouse_BannerActivity.this, "Thêm banner thành công", Toast.LENGTH_SHORT).show();
                    ivAnhBanner.setImageResource(R.drawable.add_a_photo_24); // Reset to default image
                    uri = null;
                } else {
                    Toast.makeText(Warehouse_BannerActivity.this, "Thêm banner thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void deleteProduct(String id) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Banner").child(id);
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Warehouse_BannerActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();

                    ivAnhBanner.setImageResource(R.drawable.add_a_photo_24); // Reset to default image
                    banner = new Banner();
                } else {
                    Toast.makeText(Warehouse_BannerActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void setCtronl() {
        ivAnhBanner = findViewById(R.id.ivAnhBanner);
        btnThemBanner = findViewById(R.id.btnThemBanner);
        btnXoaBanner = findViewById(R.id.btnXoaBanner);
        recyclerView = findViewById(R.id.recycleviewBanner);
    }
}