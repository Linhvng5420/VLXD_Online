package com.tdc.vlxdonline.Model;

public class DaDuyetKhach {
    private String idKhach, iDChu, trangThai;

    public DaDuyetKhach(String idKhach, String iDChu, String trangThai) {
        this.idKhach = idKhach;
        this.iDChu = iDChu;
        this.trangThai = trangThai;
    }

    public DaDuyetKhach() {
    }

    public String getIdKhach() {
        return idKhach;
    }

    public void setIdKhach(String idKhach) {
        this.idKhach = idKhach;
    }

    public String getiDChu() {
        return iDChu;
    }

    public void setiDChu(String iDChu) {
        this.iDChu = iDChu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
