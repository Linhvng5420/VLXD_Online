package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.tdc.vlxdonline.Adapter.SanPham_Adapter;
import com.tdc.vlxdonline.Model.AnhSanPham;
import com.tdc.vlxdonline.Model.Categorys;
import com.tdc.vlxdonline.Model.DonVi;
import com.tdc.vlxdonline.Model.SanPham_Model;
import com.tdc.vlxdonline.R;

import java.util.ArrayList;
import java.util.List;

public class Warehouse_AnhSPActivity extends AppCompatActivity {
    EditText edtChonASP;
    Button btnThemASP, btnXoaASP;
    ImageView ivAnhSP;
    Uri uri;
    String imagesUrl, idProduct;
    AnhSP_Adapter adapter;
    AnhSanPham anhSP = new AnhSanPham();
    List<AnhSanPham> list_ASP = new ArrayList<>();
    ValueEventListener listener;
    ArrayList<SanPham_Model> data = new ArrayList<>();
    ArrayAdapter adapterSP;
    Spinner spAnhSP;

    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


    // Hàm khởi tạo ProgressDialog
    private ProgressDialog progressDialog;

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang thêm ảnh...");
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qlanhsp_layout);
        initProgressDialog();
        setCtronl();
        setASpinner();
        DocSanPham();
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
                // TODO: Loading
                uploadData();
            }
        });
        btnXoaASP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy tên sản phẩm từ EditText
                long id = anhSP.getId();
                if (id != 0) {
                    // Gọi phương thức xóa sản phẩm
                    // deleteProduct(id);
                    showConfirmDialogXoa(id);
                    resetSelection();
                } else {
                    Toast.makeText(Warehouse_AnhSPActivity.this, "Vui lòng chọn sản phẩm để xóa", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnXoaASP.setEnabled(false);
        btnThemASP.setEnabled(true);
        // Đảm bảo Adapter đã được khởi tạo trước khi thiết lập sự kiện click
        if (adapter != null) {
            adapter.setOnItemClickListener(new AnhSP_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    // Xử lý sự kiện click vào sản phẩm
                    if (position != RecyclerView.NO_POSITION) {
                        if (list_ASP.get(position).getId() != (anhSP.getId())) {
                            btnThemASP.setEnabled(false);
                            btnXoaASP.setEnabled(true);
                            anhSP = list_ASP.get(position);
                            // Hiển thị hình ảnh sản phẩm
                            Glide.with(Warehouse_AnhSPActivity.this)
                                    .load(anhSP.getAnh())
                                    .into(ivAnhSP);
                        } else {
//                            anhSP = new AnhSanPham();
//                            ivAnhSP.setImageResource(R.drawable.add_a_photo_24);
//                            btnThemASP.setEnabled(true);
//                            btnXoaASP.setEnabled(false);
                            resetSelection();
                        }

                    }
                }
            });
        } else {
            Toast.makeText(this, "Adapter chưa được khởi tạo", Toast.LENGTH_SHORT).show();
        }

    }
    private void setASpinner() {
        adapterSP = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, data);
        spAnhSP.setAdapter(adapterSP);
        spAnhSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idProduct = data.get(i).getId();
                getDate(idProduct);
                ivAnhSP.setImageResource(R.drawable.add_a_photo_24);
                anhSP = new AnhSanPham();
                adapter.notifyDataSetChanged();
                resetSelection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new AnhSP_Adapter(Warehouse_AnhSPActivity.this, list_ASP) {
            @Override
            public void onItemClick(int position) {
            }
        };
        recyclerView.setAdapter(adapter);

    }
    private void getDate(String idSP) {
        reference = FirebaseDatabase.getInstance().getReference();
        listener = reference.child("ProdImages").child(idSP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_ASP.clear();
                for (DataSnapshot items : snapshot.getChildren()) {
                    AnhSanPham anhSP = items.getValue(AnhSanPham.class);
                    anhSP.setId(Long.parseLong(items.getKey()));
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

    private void DocSanPham() {
        reference.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for (DataSnapshot items : snapshot.getChildren()) {
                    SanPham_Model sanPhamModel = items.getValue(SanPham_Model.class);
                    sanPhamModel.setId(items.getKey());
                    
                    if (sanPhamModel.getIdChu().equals(Owner_HomeActivity.infoChu.getId())) data.add(sanPhamModel);
                }
                // Cập nhật adapter sau khi có dữ liệu
                adapterSP.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void uploadData() {
        if (uri != null) {
            progressDialog.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProdImages Images")
                    .child(uri.getLastPathSegment());
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri urlImage = uriTask.getResult();
                    imagesUrl = urlImage.toString();
                    saveDate();

                    uri = null;
                    progressDialog.dismiss();
                }
            });
        } else {
            saveDate();
        }
    }

    private void saveDate() {
        anhSP.setAnh(uri != null ? imagesUrl.toString() : anhSP.getAnh());  // Nếu bạn không cần thay đổi ảnh
        reference.child("ProdImages").child(idProduct).child(System.currentTimeMillis() + "").child("anh").setValue(anhSP.getAnh());
        resetSelection();
        Toast.makeText(this, "Thêm ảnh thành công", Toast.LENGTH_SHORT).show();
    }

    private void deleteProduct(long id) {
        if (id > -1) {
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("ProdImages").child(idProduct).child(id + "");
            productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Warehouse_AnhSPActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        // Xóa dữ liệu trên giao diện người dùng
                        ivAnhSP.setImageResource(R.drawable.add_a_photo_24); // Xóa hình ảnh
                    } else {
                        Toast.makeText(Warehouse_AnhSPActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Khong duoc xoa anh mac dinh cua san pham", Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmDialogXoa(long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Warehouse_AnhSPActivity.this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa ảnh sản phẩm này không?");

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

    private void resetSelection() {
        anhSP = new AnhSanPham();
        ivAnhSP.setImageResource(R.drawable.add_a_photo_24);
        btnXoaASP.setEnabled(false);
        btnThemASP.setEnabled(true);
    }

    private void setCtronl() {
        ivAnhSP = findViewById(R.id.ivAnhSP);
        btnThemASP = findViewById(R.id.btnThemASP);
        btnXoaASP = findViewById(R.id.btnXoaASP);
        recyclerView = findViewById(R.id.recycleviewQLASP);
        spAnhSP = findViewById(R.id.spAnhSP);
    }
}
