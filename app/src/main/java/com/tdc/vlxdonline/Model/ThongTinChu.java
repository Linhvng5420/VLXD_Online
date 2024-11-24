package com.tdc.vlxdonline.Model;

public class ThongTinChu{
    private String id, ten, sdt, email;
    private String diaChi, cuahang, cccdtruoc, cccdsau;

    public ThongTinChu() {
    }

    public ThongTinChu(String id, String ten, String sdt, String email, String diaChi, String cccdtruoc, String cccdsau) {
        this.id = id;
        this.ten = ten;
        this.sdt = sdt;
        this.email = email;
        this.diaChi = diaChi;
        this.cccdtruoc = cccdtruoc;
        this.cccdsau = cccdsau;
    }

    public String getCuahang() {
        return cuahang;
    }

    public void setCuahang(String cuahang) {
        this.cuahang = cuahang;
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

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getCccdtruoc() {
        return cccdtruoc;
    }

    public void setCccdtruoc(String cccdtruoc) {
        this.cccdtruoc = cccdtruoc;
    }

    public String getCccdsau() {
        return cccdsau;
    }

    public void setCccdsau(String cccdsau) {
        this.cccdsau = cccdsau;
    }

    @Override
    public String toString() {
        return "ThongTinChu{" +
                "id='" + id + '\'' +
                ", ten='" + ten + '\'' +
                ", sdt='" + sdt + '\'' +
                ", email='" + email + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", cccdtruoc='" + cccdtruoc + '\'' +
                ", cccdsau='" + cccdsau + '\'' +
                '}';
    }
}
