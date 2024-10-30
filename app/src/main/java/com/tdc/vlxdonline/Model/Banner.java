package com.tdc.vlxdonline.Model;

public class Banner {
    public String id,anhBanner;

    public Banner() {
    }

    public Banner(String id, String anhBanner) {
        this.id = id;
        this.anhBanner = anhBanner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnhBanner() {
        return anhBanner;
    }

    public void setAnhBanner(String anhBanner) {
        this.anhBanner = anhBanner;
    }
}
