package com.tdc.vlxdonline.Model;

public class ThongTinChu{
    private String id, ten, sdt, email;
    private String diaChi, STK;

    public ThongTinChu() {
    }

    public ThongTinChu(String id, String ten, String sdt, String email, String diaChi, String STK) {
        this.id = id;
        this.ten = ten;
        this.sdt = sdt;
        this.email = email;
        this.diaChi = diaChi;
        this.STK = STK;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSTK() {
        return STK;
    }

    public void setSTK(String STK) {
        this.STK = STK;
    }
}
