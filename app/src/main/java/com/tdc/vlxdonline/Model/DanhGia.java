package com.tdc.vlxdonline.Model;

public class DanhGia {
    private String idKhach, idSp, anh, ten, moTa;
    private int soSao = 0;
    private long idDon;

    public DanhGia() {
    }

    public DanhGia(String idKhach, String idSp, String anh, String ten, String moTa, int soSao, long idDon) {
        this.idKhach = idKhach;
        this.idSp = idSp;
        this.anh = anh;
        this.ten = ten;
        this.moTa = moTa;
        this.soSao = soSao;
        this.idDon = idDon;
    }

    public long getIdDon() {
        return idDon;
    }

    public void setIdDon(long idDon) {
        this.idDon = idDon;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getIdKhach() {
        return idKhach;
    }

    public void setIdKhach(String idKhach) {
        this.idKhach = idKhach;
    }

    public String getIdSp() {
        return idSp;
    }

    public void setIdSp(String idSp) {
        this.idSp = idSp;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getSoSao() {
        return soSao;
    }

    public void setSoSao(int soSao) {
        this.soSao = soSao;
    }
}
