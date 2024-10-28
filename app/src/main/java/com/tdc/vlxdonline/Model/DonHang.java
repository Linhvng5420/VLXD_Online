package com.tdc.vlxdonline.Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DonHang {
    private String idChu, idKhach, idTao, idGiao;
    private String anh, tenKhach, sdt, diaChi, ngayTao;
    private int tongTien, trangThai, trangThaiTT, phiTraGop;
    private long id;

    public DonHang(String anh, long id, String idChu, String idKhach, int tongTien, int trangThai, int trangThaiTT, String idTao, String idGiao, int phiTraGop, String tenKhach, String sdt, String diaChi, String ngayTao) {
        this.id = id;
        this.idChu = idChu;
        this.idKhach = idKhach;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.trangThaiTT = trangThaiTT;
        this.idTao = idTao;
        this.idGiao = idGiao;
        this.phiTraGop = phiTraGop;
        this.tenKhach = tenKhach;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.ngayTao = ngayTao;
        this.anh = anh;
    }

    public DonHang() {
        id = System.currentTimeMillis();
        this.tongTien = 0;
        this.trangThai = 1;
        this.trangThaiTT = 0;
        this.idTao = " ";
        this.idGiao = " ";
        this.phiTraGop = 0;
        this.tenKhach = "";
        this.sdt = "";
        this.diaChi = "";
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = today.format(formatter);
        this.ngayTao = formattedDate;
        this.anh = "";
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdChu() {
        return idChu;
    }

    public void setIdChu(String idChu) {
        this.idChu = idChu;
    }

    public String getIdKhach() {
        return idKhach;
    }

    public void setIdKhach(String idKhach) {
        this.idKhach = idKhach;
    }

    public int getTongTien() {
        return tongTien;
    }

    public void setTongTien(int tongTien) {
        this.tongTien = tongTien;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public int getTrangThaiTT() {
        return trangThaiTT;
    }

    public void setTrangThaiTT(int trangThaiTT) {
        this.trangThaiTT = trangThaiTT;
    }

    public String getIdTao() {
        return idTao;
    }

    public void setIdTao(String idTao) {
        this.idTao = idTao;
    }

    public String getIdGiao() {
        return idGiao;
    }

    public void setIdGiao(String idGiao) {
        this.idGiao = idGiao;
    }

    public int getPhiTraGop() {
        return phiTraGop;
    }

    public void setPhiTraGop(int phiTraGop) {
        this.phiTraGop = phiTraGop;
    }

    public String getTenKhach() {
        return tenKhach;
    }

    public void setTenKhach(String tenKhach) {
        this.tenKhach = tenKhach;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }
}
