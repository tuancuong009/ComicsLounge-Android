package com.comics.lounge.modals;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class PriceDates implements Parcelable {
    String date, openTime;
    double showPrice, mealPrice;
    int qty;

    public PriceDates(String date, String openTime, double showPrice, double mealPrice, int qty) {
        this.date = date;
        this.openTime = openTime;
        this.showPrice = showPrice;
        this.mealPrice = mealPrice;
        this.qty = qty;
    }

    protected PriceDates(Parcel in) {
        date = in.readString();
        openTime = in.readString();
        showPrice = in.readDouble();
        mealPrice = in.readDouble();
        qty = in.readInt();
    }

    public static final Creator<PriceDates> CREATOR = new Creator<PriceDates>() {
        @Override
        public PriceDates createFromParcel(Parcel in) {
            return new PriceDates(in);
        }

        @Override
        public PriceDates[] newArray(int size) {
            return new PriceDates[size];
        }
    };

    public String getDate() {
        return date;
    }

    public String getOpenTime() {
        return openTime;
    }

    public double getShowPrice() {
        return showPrice;
    }

    public double getMealPrice() {
        return mealPrice;
    }

    public int getQty() {
        return qty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(openTime);
        dest.writeDouble(showPrice);
        dest.writeDouble(mealPrice);
        dest.writeInt(qty);
    }
}
