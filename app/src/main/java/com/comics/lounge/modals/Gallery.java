package com.comics.lounge.modals;

public class Gallery {
    String id, img, name, url, isFav, type;

    public Gallery(String id, String img, String name, String url, String isFav, String type) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.url = url;
        this.isFav = isFav;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setIsFav(String isFav) {
        this.isFav = isFav;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsFav() {
        return isFav;
    }

    public String getId() {
        return id;
    }

    public String getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
