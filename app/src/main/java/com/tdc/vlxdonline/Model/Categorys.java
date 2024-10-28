package com.tdc.vlxdonline.Model;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Categorys {
    @Override
    public boolean equals(Object o) {
        Categorys category = (Categorys) o;
        return category.getId().equals(id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private String ten, anh;
    private String id;

    public Categorys() {
    }

    public Categorys(String ten, String id, String anh) {
        this.ten = ten;
        this.id = id;
        this.anh = anh;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    @Override
    public String toString() {
        return ten;
    }

}
