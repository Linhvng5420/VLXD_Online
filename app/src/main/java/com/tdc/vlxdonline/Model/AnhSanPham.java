package com.tdc.vlxdonline.Model;

public class AnhSanPham {
    private String anh, idSanPham;
    private long id = 0;

    public AnhSanPham() {
        id = System.currentTimeMillis();
    }

    public AnhSanPham(String anh, long id, String idSanPham) {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdSanPham() {
        return idSanPham;
    }

    public void setIdSanPham(String idSanPham) {
        this.idSanPham = idSanPham;
    }
}
