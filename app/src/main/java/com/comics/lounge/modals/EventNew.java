package com.comics.lounge.modals;

public class EventNew {
    String id, name, img, stDate, endDate;
    boolean isFav, memberAccess;

    public EventNew(String id, String name, String img, String stDate, String endDate, boolean isFav, boolean memberAccess) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.stDate = stDate;
        this.endDate = endDate;
        this.isFav = isFav;
        this.memberAccess = memberAccess;
    }
    public EventNew(){}

    public boolean isMemberAccess() {
        return memberAccess;
    }

    public boolean isFav() {
        return isFav;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getStDate() {
        return stDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setStDate(String stDate) {
        this.stDate = stDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public void setMemberAccess(boolean memberAccess) {
        this.memberAccess = memberAccess;
    }
}
