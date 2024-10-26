package com.tdc.vlxdonline.Model;

public class CartItem {
    private String idKhach, idSanPham;
    private int gia, soLuong;
    private String tenSP, moTa, anh;
    private boolean selected = false;

    public CartItem(String idKhach, String idSanPham, String anh, int gia, int soLuong, String tenSP, String moTa, boolean selected) {
        this.idKhach = idKhach;
        this.idSanPham = idSanPham;
        this.anh = anh;
        this.gia = gia;
        this.soLuong = soLuong;
        this.tenSP = tenSP;
        this.moTa = moTa;
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public CartItem() {
    }

    public String getIdKhach() {
        return idKhach;
    }

    public void setIdKhach(String idKhach) {
        this.idKhach = idKhach;
    }

    public String getIdSanPham() {
        return idSanPham;
    }

    public void setIdSanPham(String idSanPham) {
        this.idSanPham = idSanPham;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }
}
