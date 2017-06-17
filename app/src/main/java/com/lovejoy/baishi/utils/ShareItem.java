package com.lovejoy.baishi.utils;

public class ShareItem {
    private int id;
    private String name;
    private String image;
    private String time;
    private String words;
    private String photo;

    public ShareItem(int id,String name, String image, String time, String words, String photo){
        this.id = id;
        this.name = name;
        this.image = image;
        this.time = time;
        this.words = words;
        this.photo = photo;
    }
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWords() {
        return this.words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
