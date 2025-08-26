package com.comics.lounge.modals;

public class NavMenuItem {
    private int id;
    private String navMenuName;
    private int navMenuImg;

    public NavMenuItem(int id, String navMenuName, int navMenuImg) {
        this.id = id;
        this.navMenuName = navMenuName;
        this.navMenuImg = navMenuImg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNavMenuName() {
        return navMenuName;
    }

    public void setNavMenuName(String navMenuName) {
        this.navMenuName = navMenuName;
    }

    public int getNavMenuImg() {
        return navMenuImg;
    }

    public void setNavMenuImg(int navMenuImg) {
        this.navMenuImg = navMenuImg;
    }
}
