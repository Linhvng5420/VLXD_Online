package com.tdc.vlxdonline.Model;

public class DonVi {
    private String ten;
    private long id;

    public DonVi() {
    }

    public DonVi(String ten, long id) {
        this.ten = ten;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }
}
