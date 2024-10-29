package com.tdc.vlxdonline.Model;

public class Banner {
    private String anh;
    private String idChu;

    public Banner(String anh, String idChu) {
        this.anh = anh;
        this.idChu = idChu;
    }

    public Banner() {
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public String getIdChu() {
        return idChu;
    }

    public void setIdChu(String idChu) {
        this.idChu = idChu;
    }
}
