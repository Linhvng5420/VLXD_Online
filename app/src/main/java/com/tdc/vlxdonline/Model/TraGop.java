package com.tdc.vlxdonline.Model;

public class TraGop {
    private long idDon;
    private String hanTra;
    private int thuTu, soTien;
    private boolean trangThai = false;

    public TraGop(long idDon, String hanTra, int thuTu, int soTien) {
        this.idDon = idDon;
        this.hanTra = hanTra;
        this.thuTu = thuTu;
        this.soTien = soTien;
    }

    public TraGop() {
    }

    public int getThuTu() {
        return thuTu;
    }

    public void setThuTu(int thuTu) {
        this.thuTu = thuTu;
    }

    public long getIdDon() {
        return idDon;
    }

    public void setIdDon(long idDon) {
        this.idDon = idDon;
    }

    public String getHanTra() {
        return hanTra;
    }

    public void setHanTra(String hanTra) {
        this.hanTra = hanTra;
    }

    public int getSoTien() {
        return soTien;
    }

    public void setSoTien(int soTien) {
        this.soTien = soTien;
    }

    public boolean isDaTra() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }
}
