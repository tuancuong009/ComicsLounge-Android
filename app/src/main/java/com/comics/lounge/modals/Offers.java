package com.comics.lounge.modals;

public class Offers {
    String id, des, img;

    public Offers(String id, String des, String img) {
        this.id = id;
        this.des = des;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public String getDes() {
        return des;
    }

    public String getImg() {
        return img;
    }
}
