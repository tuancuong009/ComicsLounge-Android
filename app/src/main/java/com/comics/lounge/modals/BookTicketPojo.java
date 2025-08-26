package com.comics.lounge.modals;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class BookTicketPojo implements Parcelable, Serializable {
    public static final Creator<BookTicketPojo> CREATOR = new Creator<BookTicketPojo>() {
        @Override
        public BookTicketPojo createFromParcel(Parcel source) {
            return new BookTicketPojo(source);
        }

        @Override
        public BookTicketPojo[] newArray(int size) {
            return new BookTicketPojo[size];
        }
    };
    private int id;
    private String showOnlyPrice;
    private int showOnlyQty;
    private int freeCounter;
    private String withMealPrice;
    private int withMealQty;
    private int eventDetailId;
    private int totalCounter;
    private String grandTotal;
    private ArrayList<EventPriceDates> eventPriceDatesArrayList = null;
    private String walletId;
    private String walletBalance;
    private int freeTicket;
    private String showOnlyTotal;
    private String withMeaalTotal;
    private int walletFreeTicket;
    private int totalFreeEventLeft;

    public BookTicketPojo() {
    }

    protected BookTicketPojo(Parcel in) {
        this.id = in.readInt();
        this.showOnlyPrice = in.readString();
        this.showOnlyQty = in.readInt();
        this.freeCounter = in.readInt();
        this.withMealPrice = in.readString();
        this.withMealQty = in.readInt();
        this.eventDetailId = in.readInt();
        this.totalCounter = in.readInt();
        this.grandTotal = in.readString();
        this.eventPriceDatesArrayList = in.createTypedArrayList(EventPriceDates.CREATOR);
        this.walletId = in.readString();
        this.walletBalance = in.readString();
        this.freeTicket = in.readInt();
        this.showOnlyTotal = in.readString();
        this.withMeaalTotal = in.readString();
        this.walletFreeTicket = in.readInt();
        this.totalFreeEventLeft = in.readInt();
    }

    public int getWalletFreeTicket() {
        return walletFreeTicket;
    }

    public void setWalletFreeTicket(int walletFreeTicket) {
        this.walletFreeTicket = walletFreeTicket;

    }

    public String getShowOnlyTotal() {
        return showOnlyTotal;
    }

    public void setShowOnlyTotal(String showOnlyTotal) {
        this.showOnlyTotal = showOnlyTotal;
    }

    public String getWithMeaalTotal() {
        return withMeaalTotal;
    }

    public void setWithMeaalTotal(String withMeaalTotal) {
        this.withMeaalTotal = withMeaalTotal;
    }

    public int getFreeTicket() {
        return freeTicket;
    }

    public void setFreeTicket(int freeTicket) {
        this.freeTicket = freeTicket;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getWalletBalance() {
        if (walletBalance != null && !walletBalance.equals("")) {
            return walletBalance;
        } else {
            return "0";
        }

    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public float getBalance() {
        return Float.parseFloat(getWalletBalance());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShowOnlyPrice() {
        return showOnlyPrice;
    }

    public void setShowOnlyPrice(String showOnlyPrice) {
        this.showOnlyPrice = showOnlyPrice;
    }

    public int getShowOnlyQty() {
        return showOnlyQty;
    }

    public void setShowOnlyQty(int showOnlyQty) {
        this.showOnlyQty = showOnlyQty;
    }

    public int getFreeCounter() {
        return freeCounter;
    }

    public void setFreeCounter(int freeCounter) {
        this.freeCounter = freeCounter;
    }

    public String getWithMealPrice() {
        return withMealPrice;
    }

    public void setWithMealPrice(String withMealPrice) {
        this.withMealPrice = withMealPrice;
    }

    public int getWithMealQty() {
        return withMealQty;
    }

    public void setWithMealQty(int withMealQty) {
        this.withMealQty = withMealQty;
    }

    public int getEventDetailId() {
        return eventDetailId;
    }

    public void setEventDetailId(int eventDetailId) {
        this.eventDetailId = eventDetailId;
    }

    public int getTotalCounter() {
        return totalCounter;
    }

    public void setTotalCounter(int totalCounter) {
        this.totalCounter = totalCounter;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public ArrayList<EventPriceDates> getEventPriceDatesArrayList() {
        return eventPriceDatesArrayList;
    }

    public int getTotalFreeEventLeft() {
        return totalFreeEventLeft;
    }

    public void setTotalFreeEventLeft(int totalFreeEventLeft) {
        this.totalFreeEventLeft = totalFreeEventLeft;
    }

    public void setEventPriceDatesArrayList(ArrayList<EventPriceDates> eventPriceDatesArrayList) {
        this.eventPriceDatesArrayList = eventPriceDatesArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.showOnlyPrice);
        dest.writeInt(this.showOnlyQty);
        dest.writeInt(this.freeCounter);
        dest.writeString(this.withMealPrice);
        dest.writeInt(this.withMealQty);
        dest.writeInt(this.eventDetailId);
        dest.writeInt(this.totalCounter);
        dest.writeString(this.grandTotal);
        dest.writeTypedList(this.eventPriceDatesArrayList);
        dest.writeString(this.walletId);
        dest.writeString(this.walletBalance);
        dest.writeInt(this.freeTicket);
        dest.writeString(this.showOnlyTotal);
        dest.writeString(this.withMeaalTotal);
        dest.writeInt(this.walletFreeTicket);
        dest.writeInt(this.totalFreeEventLeft);
    }
}
