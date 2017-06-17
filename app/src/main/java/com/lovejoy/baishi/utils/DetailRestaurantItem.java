package com.lovejoy.baishi.utils;

public class DetailRestaurantItem {
    private int id;
    private String imgUrl;
    private String name;
    private int monetum;//热度
    private String location;
    private String phone;
    private String info;

    public DetailRestaurantItem(int id, String imgUrl, String name, int monetum, String location, String phone, String info) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.name = name;
        this.monetum = monetum;
        this.location = location;
        this.phone = phone;
        this.info = info;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMonetum(int monetum) {
        this.monetum = monetum;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getId() {

        return id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getMonetum() {
        return monetum;
    }

    public String getPhone() {
        return phone;
    }

    public String getInfo() {
        return info;
    }


}
