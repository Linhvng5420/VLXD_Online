package com.tdc.vlxdonline.Model;

public class TempCart {
    String idSp;
    boolean selected;

    public TempCart() {
    }

    public TempCart(String idSp, boolean selected) {
        this.idSp = idSp;
        this.selected = selected;
    }

    public String getIdSp() {
        return idSp;
    }

    public void setIdSp(String idSp) {
        this.idSp = idSp;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
