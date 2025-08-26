package com.comics.lounge.modals;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConfirmTicket implements Parcelable {
    public static final Creator<ConfirmTicket> CREATOR = new Creator<ConfirmTicket>() {
        @Override
        public ConfirmTicket createFromParcel(Parcel source) {
            return new ConfirmTicket(source);
        }

        @Override
        public ConfirmTicket[] newArray(int size) {
            return new ConfirmTicket[size];
        }
    };
    private int orderId;
    private String downloadLink;
    private List<BookingHistory> bookingHistoryList = null;

    public ConfirmTicket() {
        this.bookingHistoryList = new LinkedList<BookingHistory>();
    }

    protected ConfirmTicket(Parcel in) {
        this.orderId = in.readInt();
        this.downloadLink = in.readString();
        this.bookingHistoryList = new ArrayList<BookingHistory>();
        in.readList(this.bookingHistoryList, BookingHistory.class.getClassLoader());
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public List<BookingHistory> getBookingHistoryList() {
        return bookingHistoryList;
    }

    public void addBookingHisObj(BookingHistory bookingHistory) {
        bookingHistoryList.add(bookingHistory);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.orderId);
        dest.writeString(this.downloadLink);
        dest.writeList(this.bookingHistoryList);
    }
}
