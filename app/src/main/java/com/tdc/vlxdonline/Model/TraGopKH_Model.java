package com.tdc.vlxdonline.Model;

public class TraGopKH_Model {
    public String phiTraGop,tongTien,trangThaiTT;

    @Override
    public String toString() {
        return "TraGopKH_Model{" +
                "phiTraGop='" + phiTraGop + '\'' +
                ", tongTien='" + tongTien + '\'' +
                ", trangThaiTT='" + trangThaiTT + '\'' +
                '}';
    }

    public String getPhiTraGop() {
        return phiTraGop;
    }

    public void setPhiTraGop(String phiTraGop) {
        this.phiTraGop = phiTraGop;
    }

    public String getTongTien() {
        return tongTien;
    }

    public void setTongTien(String tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThaiTT() {
        return trangThaiTT;
    }

    public void setTrangThaiTT(String trangThaiTT) {
        this.trangThaiTT = trangThaiTT;
    }

    public TraGopKH_Model() {
    }

    public TraGopKH_Model(String phiTraGop, String tongTien, String trangThaiTT) {
        this.phiTraGop = phiTraGop;
        this.tongTien = tongTien;
        this.trangThaiTT = trangThaiTT;
    }
}
