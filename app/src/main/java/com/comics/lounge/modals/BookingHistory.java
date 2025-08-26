package com.comics.lounge.modals;

import android.os.Parcel;
import android.os.Parcelable;

public class BookingHistory implements Parcelable {
    public static final Parcelable.Creator<BookingHistory> CREATOR = new Parcelable.Creator<BookingHistory>() {
        @Override
        public BookingHistory createFromParcel(Parcel source) {
            return new BookingHistory(source);
        }

        @Override
        public BookingHistory[] newArray(int size) {
            return new BookingHistory[size];
        }
    };
    private int id;
    private int productId;
    private int orderEventId;
    private int virtuemartOrderId;
    private String productName;
    private String productType;
    private double price;
    private String productStatus;
    private String openTime;
    private String showTime;
    private String dinnreTime;
    private String performerName;
    private String supporterName;
    private String image;
    private String eventDate;
    private String showType;

    public BookingHistory() {
    }

    protected BookingHistory(Parcel in) {
        this.id = in.readInt();
        this.productId = in.readInt();
        this.orderEventId = in.readInt();
        this.virtuemartOrderId = in.readInt();
        this.productName = in.readString();
        this.productType = in.readString();
        this.price = in.readDouble();
        this.productStatus = in.readString();
        this.openTime = in.readString();
        this.showTime = in.readString();
        this.dinnreTime = in.readString();
        this.performerName = in.readString();
        this.supporterName = in.readString();
        this.image = in.readString();
        this.eventDate = in.readString();
        this.showType = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getOrderEventId() {
        return orderEventId;
    }

    public void setOrderEventId(int orderEventId) {
        this.orderEventId = orderEventId;
    }

    public int getVirtuemartOrderId() {
        return virtuemartOrderId;
    }

    public void setVirtuemartOrderId(int virtuemartOrderId) {
        this.virtuemartOrderId = virtuemartOrderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getDinnreTime() {
        return dinnreTime;
    }

    public void setDinnreTime(String dinnreTime) {
        this.dinnreTime = dinnreTime;
    }

    public String getPerformerName() {
        return performerName;
    }

    public void setPerformerName(String performerName) {
        this.performerName = performerName;
    }

    public String getSupporterName() {
        return supporterName;
    }

    public void setSupporterName(String supporterName) {
        this.supporterName = supporterName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.productId);
        dest.writeInt(this.orderEventId);
        dest.writeInt(this.virtuemartOrderId);
        dest.writeString(this.productName);
        dest.writeString(this.productType);
        dest.writeDouble(this.price);
        dest.writeString(this.productStatus);
        dest.writeString(this.openTime);
        dest.writeString(this.showTime);
        dest.writeString(this.dinnreTime);
        dest.writeString(this.performerName);
        dest.writeString(this.supporterName);
        dest.writeString(this.image);
        dest.writeString(this.eventDate);
        dest.writeString(this.showType);
    }
}
