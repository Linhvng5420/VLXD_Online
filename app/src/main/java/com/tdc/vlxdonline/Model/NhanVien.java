package com.tdc.vlxdonline.Model;

import java.io.Serializable;

// Lớp NhanVien implement giao diện Serializable để dùng Bundle truyền dữ liệu giữa các Activity hoặc Fragment
public class NhanVien {
    private String idnv;
    private String cccd;
    private String chucvu;
    private String emailnv;
    private String emailchu;
    private String sdt;
    private String tennv;

    // Constructor rỗng (cần thiết cho Firebase)
    public NhanVien() {}

    public NhanVien(String idnv, String cccd, String chucvu, String emailnv, String emailchu, String sdt, String tennv) {
        this.idnv = idnv;
        this.cccd = cccd;
        this.chucvu = chucvu;
        this.emailnv = emailnv;
        this.emailchu = emailchu;
        this.sdt = sdt;
        this.tennv = tennv;
    }

    public String getIdnv() {
        return idnv;
    }

    public void setIdnv(String idnv) {
        this.idnv = idnv;
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

    public String getEmailnv() {
        return emailnv;
    }

    public void setEmailnv(String emailnv) {
        this.emailnv = emailnv;
    }

    public String getEmailchu() {
        return emailchu;
    }

    public void setEmailchu(String emailchu) {
        this.emailchu = emailchu;
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

    @Override
    public String toString() {
        return "NhanVien{" +
                "idnv='" + idnv + '\'' +
                ", cccd='" + cccd + '\'' +
                ", chucvu='" + chucvu + '\'' +
                ", emailnv='" + emailnv + '\'' +
                ", emailchu='" + emailchu + '\'' +
                ", sdt='" + sdt + '\'' +
                ", tennv='" + tennv + '\'' +
                '}';
    }
}
