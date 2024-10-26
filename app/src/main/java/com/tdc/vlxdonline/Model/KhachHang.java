package com.tdc.vlxdonline.Model;

public class KhachHang{
    private String id, ten, sdt, email;
    private String avata, cccdMatTruoc, cccdMatSau;
    private String soCCCD, diaChi;

    public KhachHang() {
    }

    public KhachHang(String id, String ten, String sdt, String email, String avata, String cccdMatTruoc, String cccdMatSau, String soCCCD, String diaChi) {
        this.id = id;
        this.ten = ten;
        this.sdt = sdt;
        this.email = email;
        this.avata = avata;
        this.cccdMatTruoc = cccdMatTruoc;
        this.cccdMatSau = cccdMatSau;
        this.soCCCD = soCCCD;
        this.diaChi = diaChi;
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

    public String getAvata() {
        return avata;
    }

    public void setAvata(String avata) {
        this.avata = avata;
    }

    public String getCccdMatTruoc() {
        return cccdMatTruoc;
    }

    public void setCccdMatTruoc(String cccdMatTruoc) {
        this.cccdMatTruoc = cccdMatTruoc;
    }

    public String getCccdMatSau() {
        return cccdMatSau;
    }

    public void setCccdMatSau(String cccdMatSau) {
        this.cccdMatSau = cccdMatSau;
    }

    public String getSoCCCD() {
        return soCCCD;
    }

    public void setSoCCCD(String soCCCD) {
        this.soCCCD = soCCCD;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
}
