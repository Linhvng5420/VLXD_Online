package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tdc.vlxdonline.Model.Banner;
import com.tdc.vlxdonline.R;

public class Warehouse_BannerActivity extends AppCompatActivity {

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private String idChu; // Biến để lưu idChu truyền vào
    private Banner currentBanner; // Biến để lưu thông tin Banner tìm được
    private String keyData = ""; // Biến lưu lại key của data Banner trên Firebase

    // Controls
    ImageView ivAnhBannerl;
    Button btnThem;
    Button btnXoa;

    // Dialog loadding
    private ProgressDialog progressDialog;

    private static final int REQUEST_ID_UPDATE = 1;
    private static final int ID_BASE_IMAGE_VIEW_IMAGE = R.drawable.add_a_photo_24;

    private Uri currentImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qlbanner_layout);
        getBaseData(() -> {
            setCtronl();
            setEvent();
        });
    }

    private void setCtronl() {
        this.ivAnhBannerl = findViewById(R.id.ivAnhBanner);
        this.btnThem = findViewById(R.id.btnThemBanner);
        this.btnXoa = findViewById(R.id.btnXoaBanner);

        // Chạy lần đầu, cập nhật lại Control
        updateControl();
    }

    private void updateControl() {
        // Trường hợp không user này chưa có hình ảnh nào
        if (this.keyData.isEmpty()) {
            // Chưa có hình ảnh thì show nút "Thêm" và show ảnh mặc định ra
            this.btnThem.setText("Thêm");
            this.ivAnhBannerl.setImageURI(null);
            this.ivAnhBannerl.setImageResource(this.ID_BASE_IMAGE_VIEW_IMAGE);
        } else {
            // Ngược lại nếu có hình ảnh, Show nút "Sửa" và show hình ảnh thì firebase ra
            this.btnThem.setText("Sửa");
            Glide.with(this).load(currentBanner.getAnh()).into(this.ivAnhBannerl);
        }
    }

    private void getBaseData(Runnable processAfterDone) {
        String chuoiEmail = getIntent().getStringExtra("idChu");

        // Kiểm tra idChu
        if (chuoiEmail == null || chuoiEmail.isEmpty()) {
            Toast.makeText(this, "Lỗi xác thực người dùng !", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc Activity và quay lại màn hình trước
            return;
        }

        idChu = chuoiEmail.substring(0, chuoiEmail.indexOf("@")); // Giu nguyen phan idChu nay, Dung lam Key cho firebase
        
        // Lấy dữ liệu Banner hiện tại
        this.openProgressDialog("Đang lấy dữ liệu ...");
        // Truy vấn Firebase để tìm item có idChu tương ứng
        this.reference.child("banners").orderByChild("idChu").equalTo(this.idChu).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                closeProgressDialog();
                if (dataSnapshot.exists()) {
                    // Duyệt qua kết quả và lấy item đầu tiên (vì idChu là duy nhất)
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        currentBanner = snapshot.getValue(Banner.class);

                        // Ghi lại key của node (nếu cần xóa hoặc sửa)
                        keyData = snapshot.getKey();
                        break;
                    }
                }
                if (processAfterDone != null) processAfterDone.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                closeProgressDialog();
                Toast.makeText(Warehouse_BannerActivity.this, "Lỗi khi truy vấn dữ liệu!", Toast.LENGTH_SHORT).show();
                if (processAfterDone != null) processAfterDone.run();
            }
        });
    }

    private void setEvent() {
        // Khi nhấn vào button thêm, sửa hay image view thì logic xử lý như sau
        // Btn thêm hoặc sửa
        this.btnThem.setOnClickListener(v -> {
            this.xuLyCapNhatBanner();
        });

        // Image view
        this.ivAnhBannerl.setOnClickListener(v -> {
            this.xuLyCapNhatBanner();
        });

        // Btn xóa
        this.btnXoa.setOnClickListener(v -> {
            if (keyData.isEmpty()) {
                Toast.makeText(this, "Chưa có hình ảnh Banner", Toast.LENGTH_SHORT).show();
                return;
            }

            deleteBanner();
        });
    }

    private void xuLyCapNhatBanner() {
        String tieuDe = this.keyData.isEmpty() ? "Thêm Banner" : "Cập nhật Banner";
        String message = this.keyData.isEmpty() ? "Bạn có chắc chắn muốn thêm Banner" : "Bạn có chắc chắn cập nhật hình ảnh Banner này?";

        this.moThongBao(tieuDe,message,() -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_ID_UPDATE);
        });
    }

    private void moThongBao(String title, String message, Runnable onYes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Sử dụng title và message từ tham số của hàm
        builder.setTitle(title)
                .setMessage(message) // Thông điệp bằng tiếng Việt
                .setCancelable(false)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Chạy hàm khi chọn "Có"
                        if (onYes != null) {
                            onYes.run(); // Gọi hành động khi chọn "Có"
                        }
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel(); // Đóng hộp thoại
                    }
                });

        // Cài đặt icon cho AlertDialog
        Drawable drawableIcon = getResources().getDrawable(android.R.drawable.ic_dialog_alert);
        drawableIcon.setTint(Color.RED);
        builder.setIcon(drawableIcon);

        // Cài đặt background cho AlertDialog
        Drawable drawableBg = getResources().getDrawable(R.drawable.bg_item_lg);
        drawableBg.setTint(Color.WHITE);
        AlertDialog alert = builder.create();

        // Thay đổi background cho cửa sổ của AlertDialog
        alert.getWindow().setBackgroundDrawable(drawableBg);

        // Hiển thị hộp thoại
        alert.show();
    }

    private void deleteBanner() {
        // Xóa hình ảnh trên Firebase
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("Banner Images");
        StorageReference fileRef = storageRef.child("banner_" + keyData + ".jpg");

        fileRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Nếu xóa hình ảnh thành công, tiếp tục xóa dữ liệu trong Firebase Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference bannersRef = database.getReference("banners");

                    bannersRef.child(keyData).removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Nếu xóa dữ liệu thành công, cập nhật lại các biến có liên quan
                                    Toast.makeText(this, "Banner đã được xóa thành công", Toast.LENGTH_SHORT).show();
                                    // Cập nhật các biến
                                    this.keyData = "";  // Reset keyData
                                    this.currentBanner = null;  // Reset currentBanner
                                    this.updateControl();
                                } else {
                                    Toast.makeText(this, "Xóa hình ảnh Banner thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    // Nếu xóa hình ảnh thất bại
                    Toast.makeText(this, "Đã có lỗi xẩy ra trong quá trình xóa hình ảnh Banner !", Toast.LENGTH_SHORT).show();
                    Log.e("BannerError", "Error deleting image banner: " + e.getMessage());
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_UPDATE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.currentImageUri = data.getData();
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        if (this.currentImageUri == null) return;
        this.openProgressDialog("Đang tải hình ảnh lên ...");
        // Tạo reference đến Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("Banner Images");
        String key = keyData.isEmpty() ? this.idChu : keyData;
        StorageReference fileRef = storageRef.child("banner_" + key + ".jpg");

        fileRef.putFile(this.currentImageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    this.closeProgressDialog();
                    // Lấy URL download và lưu vào Firebase
                    saveImageUrlToBanner(uri.toString(), key);
                }))
                .addOnFailureListener(e -> {
                    this.closeProgressDialog();
                    Toast.makeText(this, "Đã có lỗi xảy ra trong quá trình upload ảnh, vui lòng thử lại sau !", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveImageUrlToBanner(String downloadUrl, String key) {
        this.openProgressDialog("Đang cập nhật banner ...");
        DatabaseReference bannersRef = this.reference.child("banners");

        if (!keyData.isEmpty()) {
            // TH1: Đã có keyData, cập nhật banner hiện tại
            bannersRef.child(keyData).child("anh").setValue(downloadUrl)
                    .addOnCompleteListener(task -> {
                        this.closeProgressDialog();
                        if (task.isSuccessful()) {
                            // Cập nhật biến currentBanner với dữ liệu mới
                            currentBanner.setAnh(downloadUrl);
                            this.updateControl();
                            Toast.makeText(this, "Cập nhật banner thành công !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Đã có lỗi xảy ra !", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        this.closeProgressDialog();
                        Toast.makeText(this, "Đã có lỗi xảy ra !", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // TH2: keyData rỗng, tạo mới một banner
            Banner newBanner = new Banner(downloadUrl, idChu);
            bannersRef.child(key).setValue(newBanner)
                    .addOnCompleteListener(task -> {
                        this.closeProgressDialog();
                        if (task.isSuccessful()) {
                            // Cập nhật keyData và currentBanner với dữ liệu mới
                            keyData = key;
                            currentBanner = newBanner;
                            this.updateControl();
                            Toast.makeText(this, "Upload banner mới thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Đã có lỗi xảy ra !", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        this.closeProgressDialog();
                        Toast.makeText(this, "Đã có lỗi xảy ra !", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Hàm mở ProgressDialog
    private void openProgressDialog(String text) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(text);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    // Hàm đóng ProgressDialog
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}