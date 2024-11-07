package com.tdc.vlxdonline.Activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdc.vlxdonline.Adapter.CartItemAdapter;
import com.tdc.vlxdonline.Model.CartItem;
import com.tdc.vlxdonline.Model.ChiTietDon;
import com.tdc.vlxdonline.Model.Products;
import com.tdc.vlxdonline.Model.TempCart;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.FragmentCartBinding;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CartFragment extends Fragment {

    // Danh sach san pham trong gio cua KH
    ArrayList<CartItem> dataCart = new ArrayList<>();
    CartItemAdapter cartItemAdapter;
    // Khac
    boolean checkFirst, addPE;
    FragmentCartBinding binding;
    DatabaseReference referCart;
    String idKhach;
    ValueEventListener eventCart, eventProd;
    ArrayList<TempCart> tempCarts = new ArrayList<>();
    // Tong tien cac san pham da chon
    private int tongTien = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false);
        referCart = FirebaseDatabase.getInstance().getReference();
        idKhach = Customer_HomeActivity.info.getID();
        checkFirst = true;
        addPE = false;
        tempCarts.clear();
        setAdapterCart();
        docDanhSach();
        setEventChangeProd();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.cbAllCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean select = binding.cbAllCart.isChecked();
                // Set tong tien ve 0 (nếu select bị hủy thì giữ nguyên, nếu chọn thì sẽ cộng lại tổng trong for)
                tongTien = 0;
                for (int i = 0; i < dataCart.size(); i++) {
                    dataCart.get(i).setSelected(select);
                    if (select) tongTien = tongTien + (dataCart.get(i).getSoLuong() * dataCart.get(i).getGia());
                }
                for (int i = 0; i < tempCarts.size(); i++) {
                    tempCarts.get(i).setSelected(select);
                }
                cartItemAdapter.notifyDataSetChanged();
                binding.tvTongCart.setText(chuyenChuoi(tongTien));
            }
        });
        binding.btnDatCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChuyenSangDatHang();
            }
        });
    }
    // Ham doc cac san pham trong gio cua khach
    private void docDanhSach() {
        if (eventCart == null) {
            eventCart = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshotCart) {
                    try {
                        tongTien = 0;
                        dataCart.clear();
                        // Đếm số lượng mục cần xử lý
                        int itemCount = (int) dataSnapshotCart.getChildrenCount();
                        AtomicInteger processedCount = new AtomicInteger(0);
                        boolean[] check = {true, checkFirst};

                        if (itemCount > 0){
                            for (DataSnapshot snapshot : dataSnapshotCart.getChildren()) {
                                CartItem cartItem = snapshot.getValue(CartItem.class);
                                cartItem.idSanPham = snapshot.getKey();

                                referCart.child("products").child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try {
                                            Products product = dataSnapshot.getValue(Products.class);
                                            if (product != null) {
                                                cartItem.gia = Integer.parseInt(product.getGiaBan());
                                                cartItem.tenSP = product.getTen();
                                                cartItem.moTa = product.getMoTa();
                                                cartItem.anh = product.getAnh();
                                                if (check[1]) {
                                                    TempCart t = new TempCart(cartItem.idSanPham, cartItem.isSelected());
                                                    tempCarts.add(t);
                                                } else {
                                                    for (int i = 0; i < tempCarts.size(); i++) {
                                                        TempCart t = tempCarts.get(i);
                                                        if (cartItem.getIdSanPham().equals(t.getIdSp())) {
                                                            cartItem.selected = t.isSelected();
                                                            if (cartItem.isSelected())
                                                                tongTien += (cartItem.soLuong * cartItem.gia);
                                                        }
                                                    }
                                                }
                                                dataCart.add(cartItem);
                                                int tonKho = Integer.parseInt(product.getTonKho());
                                                if (tonKho < cartItem.getSoLuong()) {
                                                    if (check[1]) tempCarts.clear();
                                                    referCart.child("carts").child(idKhach).child(cartItem.getIdSanPham()).child("soLuong").setValue(tonKho);
                                                    check[0] = false;
                                                }
                                                if (processedCount.incrementAndGet() == itemCount && check[0]) {
                                                    cartItemAdapter.notifyDataSetChanged();
                                                    binding.tvTongCart.setText(chuyenChuoi(tongTien));
                                                    checkFirst = false;
                                                }
                                            } else {
                                                ThongBaoXoa(snapshot.getKey());
                                                if (check[1]) tempCarts.clear();
                                                referCart.child("carts").child(idKhach).child(cartItem.getIdSanPham()).removeValue();
                                                check[0] = false;
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }else {
                            cartItemAdapter.notifyDataSetChanged();
                            binding.tvTongCart.setText(chuyenChuoi(tongTien));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            referCart.child("carts").child(idKhach).addValueEventListener(eventCart);
        } else {
            referCart.child("carts").child(idKhach).addListenerForSingleValueEvent(eventCart);
        }
    }

    // Ham doc lai thong tin moi khi sua san pham
    private void setEventChangeProd(){
        if (eventProd != null) {
            referCart.child("products").removeEventListener(eventProd);
            eventProd = null;
        }
        eventProd = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (addPE) docDanhSach();
                else addPE = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        referCart.child("products").addValueEventListener(eventProd);
    }

    private void setAdapterCart() {
        cartItemAdapter = new CartItemAdapter(getActivity(), dataCart);
        cartItemAdapter.setOnItemCartClickListener(new CartItemAdapter.OnItemCartClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                ((Customer_HomeActivity) getActivity()).ReplaceFragment(new ProdDetailCustomerFragment(dataCart.get(position).getIdSanPham()));
            }

            @Override
            public void OnCheckBoxClick(View view, int position, boolean selected) {
                dataCart.get(position).setSelected(selected);
                CartItem temp = dataCart.get(position);
                for (int i = 0; i < tempCarts.size(); i++) {
                    if (tempCarts.get(i).getIdSp().equals(temp.getIdSanPham())) {
                        tempCarts.get(i).setSelected(selected);
                    }
                }
                if (binding.cbAllCart.isChecked()) {
                    binding.cbAllCart.setChecked(false);
                }
                if (selected) {
                    tongTien = tongTien + (temp.getSoLuong() * temp.getGia());
                }else {
                    tongTien = tongTien - (temp.getSoLuong() * temp.getGia());
                }
                binding.tvTongCart.setText(chuyenChuoi(tongTien));
            }

            @Override
            public void OnDeleteClick(View view, int position) {
                CartItem temp = dataCart.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Xác Nhận Xóa!").setMessage("Xác Nhận Xóa Sản Phẩm " + temp.getTenSP() + " Khỏi Giỏ Hàng?");

                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        referCart.child("carts").child(idKhach).child(temp.getIdSanPham()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Đã xóa thành công
                                    Toast.makeText(getActivity(), "Đã xóa sản phẩm khỏi giỏ hàng!", Toast.LENGTH_LONG).show();
                                } else {
                                    // Xảy ra lỗi
                                    Toast.makeText(getActivity(), "Đã xảy ra lỗi khi xóa!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton(R.string.quay_lai, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                Drawable drawableIcon = getResources().getDrawable(android.R.drawable.ic_dialog_alert);
                drawableIcon.setTint(Color.RED);
                builder.setIcon(drawableIcon);
                Drawable drawableBg = getResources().getDrawable(R.drawable.bg_item_lg);
                drawableBg.setTint(Color.rgb(100, 220, 255));
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(drawableBg);
                alertDialog.show();
            }

            @Override
            public void OnIncreaseClick(View view, int position) {
                CartItem temp = dataCart.get(position);
                referCart.child("carts").child(idKhach).child(temp.getIdSanPham()).child("soLuong").setValue(temp.getSoLuong() + 1);
            }

            @Override
            public void OnReduceClick(View view, int position) {
                CartItem temp = dataCart.get(position);
                if (temp.getSoLuong() > 1)
                    referCart.child("carts").child(idKhach).child(temp.getIdSanPham()).child("soLuong").setValue(temp.getSoLuong() - 1);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcCart.setLayoutManager(linearLayoutManager);
        binding.rcCart.setAdapter(cartItemAdapter);
    }
    // Ham xu ly du lieu chuyen sang trang dat hang
    private void ChuyenSangDatHang(){
        ArrayList<ChiTietDon> dataDetail = new ArrayList<>();
        for (int i = 0; i < dataCart.size(); i++) {
            CartItem temp = dataCart.get(i);
            if (temp.isSelected()) {
                dataDetail.add(new ChiTietDon(0, temp.getIdSanPham(), temp.getSoLuong(), temp.getGia(), temp.getTenSP(), temp.getAnh()));
            }
        }
        if (dataDetail.size() > 0) {
            ((Customer_HomeActivity) getActivity()).ReplaceFragment(new DatHangGioHangFragment(dataDetail));
        }else {
            Toast.makeText(getActivity(), "Hãy Chọn Sản Phẩm Để Đặt Mua!", Toast.LENGTH_SHORT).show();
        }
    }
    // Ham them dau cham cho gia ban
    private StringBuilder chuyenChuoi(int soTien) {
        StringBuilder chuoi = new StringBuilder(soTien + "");
        if (chuoi.length() > 3) {
            int dem = 0;
            int doDai = chuoi.length() - 1;
            for (int i = doDai; i > 0; i--) {
                dem = dem + 1;
                if (dem == 3) {
                    chuoi.insert(i, '.');
                    dem = 0;
                }
            }
        }
        return chuoi;
    }

    private void ThongBaoXoa(String maSP){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông Báo!").setMessage("Đã Xóa Sản Phẩm Mã " + maSP + " Khỏi Giỏ Hàng Vì Sản Phẩm Đã Bị Xóa!");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Drawable drawableIcon = getResources().getDrawable(android.R.drawable.ic_dialog_alert);
        drawableIcon.setTint(Color.RED);
        builder.setIcon(drawableIcon);
        Drawable drawableBg = getResources().getDrawable(R.drawable.bg_item_lg);
        drawableBg.setTint(Color.rgb(100, 220, 255));
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(drawableBg);
        alertDialog.show();
    }

    @Override
    public void onDestroyView() {
        binding = null;

        referCart.child("carts").child(idKhach).removeEventListener(eventCart);
        referCart.child("products").removeEventListener(eventProd);

        referCart = null;
        eventCart = null;
        eventProd = null;

        super.onDestroyView();
    }
}