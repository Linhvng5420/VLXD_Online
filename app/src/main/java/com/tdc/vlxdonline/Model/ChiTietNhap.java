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

    private String idDon, idSanPham, soLuong, gia;
    private String ten, anh;

    public ChiTietNhap(String idDon, String idSanPham, String soLuong, String gia, String ten, String anh) {
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

    public String getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(String soLuong) {
        this.soLuong = soLuong;
    }

    public String getGia() {
        return gia;
    }

    public void setGia(String gia) {
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
