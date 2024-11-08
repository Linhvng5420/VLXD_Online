package com.tdc.vlxdonline.Model;

import com.tdc.vlxdonline.Activity.Warehouse_HomeActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DonNhap {
    private String  idChu, idTao;
    private int  tongTien = 0;
    private long id;
    private String ngayTao;

    public DonNhap(long id, String idChu, String idTao, int tongTien, String ngayTao) {
        this.id = id;
        this.idChu = idChu;
        this.idTao = idTao;
        this.tongTien = tongTien;
        this.ngayTao = ngayTao;
    }

    public DonNhap() {
        id = System.currentTimeMillis();

        // id chu, id tạo lấy từ nhân viên đang đăng nhập (Warehouse_HomeActivity)
        idChu = Warehouse_HomeActivity.nhanVien.getEmailchu();
        idTao = Warehouse_HomeActivity.nhanVien.getCccd();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = today.format(formatter);
        this.ngayTao = formattedDate;
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

    public String getIdTao() {
        return idTao;
    }

    public void setIdTao(String idTao) {
        this.idTao = idTao;
    }

    public int getTongTien() {
        return tongTien;
    }

    public void setTongTien(int tongTien) {
        this.tongTien = tongTien;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }
}
