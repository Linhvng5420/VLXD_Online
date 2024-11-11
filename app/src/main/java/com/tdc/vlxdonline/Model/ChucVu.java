package com.tdc.vlxdonline.Model;

public class ChucVu {
    String idChucVu;
    String tenChucVu;

    public ChucVu() {
    }

    public ChucVu(String idChucVu, String tenChucVu) {
        this.idChucVu = idChucVu;
        this.tenChucVu = tenChucVu;
    }

    public String getIdChucVu() {
        return idChucVu;
    }

    public void setIdChucVu(String idChucVu) {
        this.idChucVu = idChucVu;
    }

    public String getTenChucVu() {
        return tenChucVu;
    }

    public void setTenChucVu(String tenChucVu) {
        this.tenChucVu = tenChucVu;
    }

    @Override
    public String toString() {
        return "ChucVu{" +
                "idChucVu='" + idChucVu + '\'' +
                ", tenChucVu='" + tenChucVu + '\'' +
                '}';
    }
}
