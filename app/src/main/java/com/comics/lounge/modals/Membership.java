package com.comics.lounge.modals;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Membership implements Serializable {
    private int id;
    private String name;
    private String description;
    private String price;
    private String otherPrice;
    private String image;
    private String date;
    private String status;
    private String events;
    private String permonthallowed;

    public Membership() {
    }

    public Membership(int id, String name, String description, String price, String otherPrice, String image, String date, String status, String permonthallowed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.otherPrice = otherPrice;
        this.image = image;
        this.date = date;
        this.status = status;
        this.permonthallowed = permonthallowed;
    }

    protected Membership(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readString();
        otherPrice = in.readString();
        image = in.readString();
        date = in.readString();
        status = in.readString();
        events = in.readString();
        permonthallowed = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOtherPrice() {
        return otherPrice;
    }

    public void setOtherPrice(String otherPrice) {
        this.otherPrice = otherPrice;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public String getPermonthallowed() {
        return permonthallowed;
    }

    public void setPermonthallowed(String permonthallowed) {
        this.permonthallowed = permonthallowed;
    }
}
