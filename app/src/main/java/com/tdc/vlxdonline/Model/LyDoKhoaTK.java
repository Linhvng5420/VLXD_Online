package com.tdc.vlxdonline.Model;

public class LyDoKhoaTK {
    private String ngay;
    private String lyDo;

    public LyDoKhoaTK(String ngay, String lyDo) {
        this.ngay = ngay;
        this.lyDo = lyDo;
    }

    public String getNgay() {
        return ngay;
    }

    public String getLyDo() {
        return lyDo;
    }

    @Override
    public String toString() {
        return "[ " + ngay + " ] " + lyDo;
    }
}
