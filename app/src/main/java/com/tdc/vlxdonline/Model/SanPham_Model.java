package com.tdc.vlxdonline.Model;

public class SanPham_Model {
    public String anh , ten, gia, tonKho, daBan = "0";
    public String moTa, donVi, danhMuc;

    public String id, idChu, soSao = "0";

    public String getSoSao() {
        return soSao;
    }

    public void setSoSao(String soSao) {
        this.soSao = soSao;
    }

    public SanPham_Model(String idChu, String id) {
        this.idChu = idChu;
        this.id = id;
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

    public SanPham_Model(String danhMuc, String donVi, String moTa) {
        this.danhMuc = danhMuc;
        this.donVi = donVi;
        this.moTa = moTa;
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

    public SanPham_Model() {
    }

    public SanPham_Model(String anh, String ten, String gia, String tonKho, String daBan) {
        this.anh = anh;
        this.ten = ten;
        this.gia = gia;
        this.tonKho = tonKho;
        this.daBan = daBan;
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
        return gia;
    }

    public void setGia(String gia) {
        this.gia = gia;
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
}
