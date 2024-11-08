package com.tdc.vlxdonline.Model;

public class TaoDonHang {
    private int resourceImage;
    private String name;

    public TaoDonHang(int resourceImage, String name) {
        this.resourceImage = resourceImage;
        this.name = name;
    }

    public int getResourceImage() {
        return resourceImage;
    }

    public void setResourceImage(int resourceImage) {
        this.resourceImage = resourceImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
