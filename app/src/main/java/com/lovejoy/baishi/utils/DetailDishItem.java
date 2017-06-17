package com.lovejoy.baishi.utils;

public class DetailDishItem {
    private int id;
    private String imgUrl;
    private String name;
    private int heat;
    private String special;
    private String upLoader;
    private String taboo;//禁忌
    private String nurtrition;//营养价值
    private String step;


    public DetailDishItem(int id, String name, String imgUrl, int heat, String special, String upLoader, String taboo, String nurtrition, String step) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.special = special;
        this.upLoader = upLoader;
        this.taboo = taboo;
        this.nurtrition = nurtrition;
        this.heat = heat;
        this.step = step;
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

    public void setSpecial(String special) {
        this.special = special;
    }

    public void setUpLoader(String upLoader) {
        this.upLoader = upLoader;
    }

    public void setTaboo(String taboo) {
        this.taboo = taboo;
    }

    public void setNurtrition(String nurtrition) {
        this.nurtrition = nurtrition;
    }

    public void setHeat(int heat) {
        this.heat = heat;
    }

    public void setStep(String step) {
        this.step = step;
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

    public String getSpecial() {
        return special;
    }

    public String getUpLoader() {
        return upLoader;
    }

    public String getTaboo() {
        return taboo;
    }

    public String getNurtrition() {
        return nurtrition;
    }

    public int getHeat() {
        return heat;
    }

    public String getStep() {
        return step;
    }


}
