package com.pixel.mycontact.beans;

public class DetailList {
    private String d1;
    private int imgSrc;
    private String d2;

    public DetailList(String d1, int imgSrc, String d2) {
        this.d1 = d1;
        this.imgSrc = imgSrc;
        this.d2 = d2;
    }

    public String getD1() {
        return d1;
    }

    public void setD1(String d1) {
        this.d1 = d1;
    }

    public int getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(int imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getD2() {
        return d2;
    }

    public void setD2(String d2) {
        this.d2 = d2;
    }
}
