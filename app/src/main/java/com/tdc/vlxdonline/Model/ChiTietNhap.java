package com.tdc.vlxdonline.Model;

public class ChiTietNhap {
    @Override
    public String toString() {
        return "ChiTietNhap{" +
                "idDon='" + idDon + '\'' +
                ", idSanPham='" + idSanPham + '\'' +
                ", soLuong='" + soLuong + '\'' +
                ", gia='" + gia + '\'' +
                ", ten='" + ten + '\'' +
                ", anh='" + anh + '\'' +
                '}';
    }

    private String idDon, idSanPham;
    private int soLuong, gia;
    private String ten, anh;

    public ChiTietNhap(String idDon, String idSanPham, int soLuong, int gia, String ten, String anh) {
        this.idDon = idDon;
        this.idSanPham = idSanPham;
        this.soLuong = soLuong;
        this.gia = gia;
        this.ten = ten;
        this.anh = anh;
    }

    public ChiTietNhap() {
    }

    public String getIdDon() {
        return idDon;
    }

    public void setIdDon(String idDon) {
        this.idDon = idDon;
    }

    public String getIdSanPham() {
        return idSanPham;
    }

    public void setIdSanPham(String idSanPham) {
        this.idSanPham = idSanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }
}
