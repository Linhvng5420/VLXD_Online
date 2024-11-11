package com.tdc.vlxdonline.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.tdc.vlxdonline.Adapter.CategoryAdapter;
import com.tdc.vlxdonline.Model.Categorys;
import com.tdc.vlxdonline.Model.DonVi;
import com.tdc.vlxdonline.R;
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
import com.tdc.vlxdonline.Adapter.SanPham_Adapter;
import com.tdc.vlxdonline.Model.SanPham_Model;

import java.util.ArrayList;
import java.util.List;

/*import SanPham_Adapter.SanPham_Adapter;
import SanPham_Model.SanPham_Model;*/

public class Warehouse_ThemSanPhamActivity extends AppCompatActivity {
    EditText edtNhapten, edtNhapgiaban,edtgiaNhap, edtNhapsoluong, edtDaban, edtMoTa;
    Button btnThem, btnXoa, btnSua;
    ImageView ivImages;
    Uri uri;
    String imagesUrl;
    SanPham_Adapter adapter;
    SanPham_Model sanPhamModel = new SanPham_Model();
    List<SanPham_Model> list_SP = new ArrayList<>();

    List<String> list_DV = new ArrayList<>();
    ArrayAdapter adapter_DV;

    ArrayList<Categorys> list_DM = new ArrayList<>();
    ArrayAdapter categoryAdapter;


    ValueEventListener listener;
    RecyclerView recyclerView;
    Spinner spdonVi, spdanhMuc;
    String donVi, danhMuc;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taosanpham_layout);
        setControl();
        getDate();
        getDonVi();
        getDanhMuc();
        setEvent();
    }

    private void getDanhMuc() {
        reference = FirebaseDatabase.getInstance().getReference();
        listener = reference.child("categorys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    list_DM.clear();
                    for (DataSnapshot items : snapshot.getChildren()) {
                        Categorys category = items.getValue(Categorys.class);
                        list_DM.add(category);
                    }
                    danhMuc = list_DM.get(0).getId();
                    categoryAdapter = new ArrayAdapter(Warehouse_ThemSanPhamActivity.this, android.R.layout.simple_spinner_dropdown_item, list_DM);
                    ;
                    spdanhMuc.setAdapter(categoryAdapter);
                    spdanhMuc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            danhMuc = list_DM.get(i).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }

    private void getDonVi() {
        reference = FirebaseDatabase.getInstance().getReference();
        listener = reference.child("DonVi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    list_DV.clear();
                    for (DataSnapshot items : snapshot.getChildren()) {
                        DonVi donviModel = items.getValue(DonVi.class);
                        list_DV.add(donviModel.getTen());
                    }
                    donVi = list_DV.get(0);
                    adapter_DV = new ArrayAdapter(Warehouse_ThemSanPhamActivity.this, android.R.layout.simple_dropdown_item_1line, list_DV);
                    spdonVi.setAdapter(adapter_DV);
                    spdonVi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            donVi = list_DV.get(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }

    private void getDate() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new SanPham_Adapter(Warehouse_ThemSanPhamActivity.this, list_SP);
        recyclerView.setAdapter(adapter);
        reference = FirebaseDatabase.getInstance().getReference();
        listener = reference.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_SP.clear();
                for (DataSnapshot items : snapshot.getChildren()) {
                    SanPham_Model sanPhamModel = items.getValue(SanPham_Model.class);
                    sanPhamModel.setId(items.getKey());
                    if (sanPhamModel.getIdChu().equals(Owner_HomeActivity.infoChu.getID())) list_SP.add(sanPhamModel);
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
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            ivImages.setImageURI(uri);
                        } else {
                            Toast.makeText(Warehouse_ThemSanPhamActivity.this, "Khong chon anh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        ivImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        uri == null ||
                                edtNhapten.getText().toString().trim().isEmpty() ||
                                edtNhapgiaban.getText().toString().trim().isEmpty() ||
                                edtgiaNhap.getText().toString().trim().isEmpty() ||
                                edtNhapsoluong.getText().toString().trim().isEmpty()) {

                    Toast.makeText(Warehouse_ThemSanPhamActivity.this,
                            "Vui lòng điền đầy đủ thông tin sản phẩm!", Toast.LENGTH_SHORT).show();
                } else {
                    uploadData();
                    hideKeyboard();
                }
            }
        });
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem có sản phẩm nào đã được chọn chưa
                if (sanPhamModel == null || sanPhamModel.getId() == null || sanPhamModel.getId().isEmpty()) {
                    Toast.makeText(Warehouse_ThemSanPhamActivity.this, "Vui lòng chọn sản phẩm để xóa", Toast.LENGTH_SHORT).show();
                } else {
                    // Thực hiện xóa sản phẩm nếu đã chọn
//                    deleteProduct(sanPhamModel.getId());
                    showConfirmDialogXoa(sanPhamModel.getId());
                    hideKeyboard();
                }
            }
        });
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtNhapten.getText().toString().isEmpty()) {
                    Toast.makeText(Warehouse_ThemSanPhamActivity.this, "Vui lòng chọn sản phẩm để sửa!", Toast.LENGTH_SHORT).show();
                } else {
                    showConfirmDialogSua();
                    //uploadData();  // Gọi phương thức để cập nhật dữ liệu
                    hideKeyboard();
                }
            }
        });
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
        btnThem.setEnabled(true);
        edtDaban.setEnabled(false);

        // Đảm bảo Adapter đã được khởi tạo trước khi thiết lập sự kiện click
        if (adapter != null) {
            adapter.setOnItemClickListener(new SanPham_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    // Xử lý sự kiện click vào sản phẩm
                    if (position != RecyclerView.NO_POSITION) {
                        if (!list_SP.get(position).getId().equals(sanPhamModel.getId())) {
                            btnSua.setEnabled(true);
                            btnXoa.setEnabled(true);
                            btnThem.setEnabled(false);
                            edtDaban.setEnabled(false);
                            sanPhamModel = list_SP.get(position);

                            // Hiển thị thông tin sản phẩm lên các EditText
                            edtNhapten.setText(sanPhamModel.getTen());
                            edtNhapgiaban.setText(sanPhamModel.getGia());
                            edtgiaNhap.setText(sanPhamModel.getGiaNhap());
                            edtNhapsoluong.setText(sanPhamModel.getTonKho());
                            edtDaban.setText(sanPhamModel.getDaBan());
                            edtMoTa.setText(sanPhamModel.getMoTa());
                            spdonVi.setSelection(adapter_DV.getPosition(sanPhamModel.getDonVi()));

                            // Tìm vị trí danh mục trong danh sách và hiển thị lên Spinner
                            Categorys temp = new Categorys();
                            for (int i = 0; i < list_DM.size(); i++) {
                                if (list_DM.get(i).getId().equals(sanPhamModel.getDanhMuc())) {
                                    temp = list_DM.get(i);
                                    break;
                                }
                            }
                            spdanhMuc.setSelection(categoryAdapter.getPosition(temp));
                            // Hiển thị hình ảnh sản phẩm
                            Glide.with(Warehouse_ThemSanPhamActivity.this)
                                    .load(sanPhamModel.getAnh())
                                    .into(ivImages);
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

    private void saveDate() {
        if (sanPhamModel.getId() == null) sanPhamModel.setId(System.currentTimeMillis() + "");
        sanPhamModel.setTen(edtNhapten.getText().toString());
        sanPhamModel.setGia(edtNhapgiaban.getText().toString());
        sanPhamModel.setGiaNhap(edtgiaNhap.getText().toString());

        sanPhamModel.setTonKho(edtNhapsoluong.getText().toString());
        sanPhamModel.setMoTa(edtMoTa.getText().toString());
        sanPhamModel.setAnh(uri != null ? imagesUrl.toString() : sanPhamModel.getAnh());
        sanPhamModel.setDonVi(donVi);
        sanPhamModel.setDanhMuc(danhMuc);
        sanPhamModel.setIdChu(Owner_HomeActivity.infoChu.getID());

        reference.child("ProdImages").child(sanPhamModel.getId()).child("-1").child("anh").setValue(sanPhamModel.getAnh());
        reference.child("products").child(sanPhamModel.getId()).setValue(sanPhamModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    }
                });
        uri = null;
    }
    public void uploadData() {
        if (uri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("SanPham Images")
                    .child(uri.getLastPathSegment());
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri urlImage = uriTask.getResult();
                    imagesUrl = urlImage.toString();
                    saveDate();
                    clearSelection();
                }
            });
        } else {
            saveDate();
            clearSelection();
        }
    }

    private void deleteProduct(String id) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(id);
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Warehouse_ThemSanPhamActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    clearSelection();
                } else {
                    Toast.makeText(Warehouse_ThemSanPhamActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
        reference.child("ProdImages").child(id).removeValue(); // Xóa ảnh trong bảng "ProdImages")
    }

    // clear trạng thái
    private void clearSelection() {
        sanPhamModel = new SanPham_Model();
        edtNhapten.setText("");
        edtNhapgiaban.setText("");
        edtgiaNhap.setText("");
        edtNhapsoluong.setText("");
        edtDaban.setText("");
        edtMoTa.setText("");
        spdanhMuc.setSelection(0);
        spdonVi.setSelection(0);
        ivImages.setImageResource(R.drawable.add_a_photo_24);
        btnXoa.setEnabled(false);
        btnSua.setEnabled(false);
        btnThem.setEnabled(true);
    }
    private void showConfirmDialogXoa(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Warehouse_ThemSanPhamActivity.this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?");

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
        AlertDialog.Builder builder = new AlertDialog.Builder(Warehouse_ThemSanPhamActivity.this);
        builder.setTitle("Xác nhận sửa");
        builder.setMessage("Bạn có chắc chắn muốn sửa sản phẩm này không?");

        builder.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveDate();
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
    private void setControl() {
        edtNhapten = findViewById(R.id.edtNhapTen);
        edtNhapgiaban = findViewById(R.id.edtNhapgiaban);
        edtgiaNhap = findViewById(R.id.edtNhapgiaNhap);
        edtNhapsoluong = findViewById(R.id.edtNhapsoluong);
        edtDaban = findViewById(R.id.edtDaban);
        edtMoTa = findViewById(R.id.edtMoTa);
        ivImages = findViewById(R.id.ivImages);
        btnThem = findViewById(R.id.btnThem);
        btnXoa = findViewById(R.id.btnXoa);
        btnSua = findViewById(R.id.btnSua);
        recyclerView = findViewById(R.id.recycleview);
        spdonVi = findViewById(R.id.spdonVi);
        spdanhMuc = findViewById(R.id.spdanhMuc);

    }
}
