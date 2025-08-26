package com.comics.lounge.modals;

public class MenuItem {
    String name, price, des, img, popupImg;

    public MenuItem(String name, String price, String des, String img, String popupImg) {
        this.name = name;
        this.price = price;
        this.des = des;
        this.img = img;
        this.popupImg = popupImg;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDes() {
        return des;
    }

    public String getImg() {
        return img;
    }

    public String getPopupImg() {
        return popupImg;
    }
}
