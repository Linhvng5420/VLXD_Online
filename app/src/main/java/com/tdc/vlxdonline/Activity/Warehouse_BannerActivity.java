package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.tdc.vlxdonline.Model.Banner;
import com.tdc.vlxdonline.R;

import java.util.ArrayList;
import java.util.List;

public class Warehouse_BannerActivity extends AppCompatActivity {

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private Button btnThemBanner, btnXoaBanner;
    private ImageView ivAnhBanner;
    private ListView lvDanhSach;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qlbanner_layout);
        setCtronl();
        getDate();
        initImagePickerLauncher();
        setEvent();

    }
    private void initImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            ivAnhBanner.setImageURI(selectedImageUri);
                            uploadImageToFirebase(selectedImageUri);
                        }
                    }
                }
        );
    }

    private void setEvent() {

        btnThemBanner.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnXoaBanner.setOnClickListener(v -> {
            deleteBanner();
        });
    }

    private void getDate() {

    }
    public void uploadData() {

    }
    private void saveData() {

    }
    private void deleteBanner() {

    }
    private void setCtronl() {

        btnThemBanner = findViewById(R.id.btnThemBanner);
        btnXoaBanner = findViewById(R.id.btnXoaBanner);
        ivAnhBanner = findViewById(R.id.ivAnhBanner);
        lvDanhSach = findViewById(R.id.lvDanhSach);

        // Initialize Firebase
        reference = FirebaseDatabase.getInstance().getReference("Banners");

    }
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("banners/" + System.currentTimeMillis() + ".jpg");

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Toast.makeText(this, "Tải ảnh lên thành công!", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}