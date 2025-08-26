package com.comics.lounge.modals;

import android.os.Parcel;
import android.os.Parcelable;

public class Date implements Parcelable {
    private String date;
    private int qty;

    protected Date(Parcel in) {
        date = in.readString();
        qty = in.readInt();
    }

    public Date() {
    }

    public static final Creator<Date> CREATOR = new Creator<Date>() {
        @Override
        public Date createFromParcel(Parcel in) {
            return new Date(in);
        }

        @Override
        public Date[] newArray(int size) {
            return new Date[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeInt(qty);
    }
}
