package com.tdc.vlxdonline.Model;

public class NhanVien {
    private String anhcc1;
    private String anhcc2;
    private String cccd;
    private String chucvu;
    private String emailchu;
    private String emailnv;
    private String sdt;
    private String tennv;

    // Constructor rỗng (cần thiết cho Firebase)
    public NhanVien() {}

    public NhanVien(String anhcc1, String anhcc2, String cccd, String chucvu, String emailchu, String emailnv, String sdt, String tennv) {
        this.anhcc1 = anhcc1;
        this.anhcc2 = anhcc2;
        this.cccd = cccd;
        this.chucvu = chucvu;
        this.emailchu = emailchu;
        this.emailnv = emailnv;
        this.sdt = sdt;
        this.tennv = tennv;
    }

    public String getAnhcc1() {
        return anhcc1;
    }

    public void setAnhcc1(String anhcc1) {
        this.anhcc1 = anhcc1;
    }

    public String getAnhcc2() {
        return anhcc2;
    }

    public void setAnhcc2(String anhcc2) {
        this.anhcc2 = anhcc2;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getChucvu() {
        return chucvu;
    }

    public void setChucvu(String chucvu) {
        this.chucvu = chucvu;
    }

    public String getEmailchu() {
        return emailchu;
    }

    public void setEmailchu(String emailchu) {
        this.emailchu = emailchu;
    }

    public String getEmailnv() {
        return emailnv;
    }

    public void setEmailnv(String emailnv) {
        this.emailnv = emailnv;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getTennv() {
        return tennv;
    }

    public void setTennv(String tennv) {
        this.tennv = tennv;
    }
}
