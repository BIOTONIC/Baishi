package com.lovejoy.baishi.utils;

public class RestaurantPos {
    private final int id;
    private final String name;
    private final String imageUrl;
    private String markerId;
    private final double posX;
    private final double posY;

    public RestaurantPos(int id, String name, String imageUrl, double posX, double posY) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.posX = posX;
        this.posY = posY;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }
}
