package com.usforever.model;

public class Timeline {
    private int id;
    private String date;
    private String title;
    private String description;
    private String imgUrl;
    private String icon;

    public Timeline() {
    }

    public Timeline(String date, String title, String description, String imgUrl, String icon) {
        this.date = date;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.icon = icon;
    }

    public Timeline(int id, String date, String title, String description, String imgUrl, String icon) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
