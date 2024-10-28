package com.tdc.vlxdonline.Model;

public class AnhSanPham {
    private String anh, idSanPham;
    private int id;

    public AnhSanPham() {
    }

    public AnhSanPham(String anh, int id, String idSanPham) {
        this.idSanPham = idSanPham;
        this.id = id;
        this.anh = anh;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdSanPham() {
        return idSanPham;
    }

    public void setIdSanPham(String idSanPham) {
        this.idSanPham = idSanPham;
    }
}
