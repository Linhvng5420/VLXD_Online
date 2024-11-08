package com.tdc.vlxdonline.Model;

public class SanPham_Model {
    public String anh , ten, giaBan,giaNhap, tonKho, daBan = "0";
    public String moTa, donVi, danhMuc;

    public String id, idChu, soSao = "0";

    @Override
    public String toString() {
        return ten;
    }

    public SanPham_Model(String anh, String ten, String giaBan, String giaNhap, String tonKho, String daBan, String moTa, String donVi, String danhMuc, String id, String idChu, String soSao) {
        this.anh = anh;
        this.ten = ten;
        this.giaBan = giaBan;
        this.giaNhap = giaNhap;
        this.tonKho = tonKho;
        this.daBan = daBan;
        this.moTa = moTa;
        this.donVi = donVi;
        this.danhMuc = danhMuc;
        this.id = id;
        this.idChu = idChu;
        this.soSao = soSao;
    }

    public SanPham_Model() {
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

    public String getGia() {
        return giaBan;
    }

    public void setGia(String giaBan) {
        this.giaBan = giaBan;
    }

    public String getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(String giaNhap) {
        this.giaNhap = giaNhap;
    }

    public String getTonKho() {
        return tonKho;
    }

    public void setTonKho(String tonKho) {
        this.tonKho = tonKho;
    }

    public String getDaBan() {
        return daBan;
    }

    public void setDaBan(String daBan) {
        this.daBan = daBan;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public String getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(String danhMuc) {
        this.danhMuc = danhMuc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdChu() {
        return idChu;
    }

    public void setIdChu(String idChu) {
        this.idChu = idChu;
    }

    public String getSoSao() {
        return soSao;
    }

    public void setSoSao(String soSao) {
        this.soSao = soSao;
    }
}
