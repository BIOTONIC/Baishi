package com.lovejoy.baishi.utils;

public class MyItem {
    private int id;
    private String name;
    private String imgUrl;
    private String info;

    public MyItem(int id, String name, String imgUrl, String info) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.info = info;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public String getInfo() {
        return info;
    }
}
