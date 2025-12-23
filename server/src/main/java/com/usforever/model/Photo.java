package com.usforever.model;

public class Photo {
    private int id;
    private String category;
    private String imgUrl;

    public Photo() {
    }

    public Photo(int id, String category, String imgUrl) {
        this.id = id;
        this.category = category;
        this.imgUrl = imgUrl;
    }

    public Photo(String category, String imgUrl) {
        this.category = category;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
