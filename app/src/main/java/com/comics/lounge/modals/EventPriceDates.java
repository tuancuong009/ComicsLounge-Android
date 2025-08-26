package com.comics.lounge.modals;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class EventPriceDates implements Parcelable {
    public static final Parcelable.Creator<EventPriceDates> CREATOR = new Parcelable.Creator<EventPriceDates>() {
        public EventPriceDates createFromParcel(Parcel in) {
            return new EventPriceDates(in);
        }

        public EventPriceDates[] newArray(int size) {
            return new EventPriceDates[size];
        }
    };
    private int id;
    private String text;
    private String price;
    private String date;
    private int freeCount;
    private String display;
    private String showOnlytickets;
    private String qty;
    private JSONObject attributeJson = new JSONObject();
    private JSONObject attributeFirstJson = new JSONObject();
    private JSONObject attributeSecondJson = new JSONObject();

    public EventPriceDates() {

    }

    public EventPriceDates(Parcel in) {
        id = in.readInt();
        text = in.readString();
        price = in.readString();
        date = in.readString();
        freeCount = in.readInt();
        display = in.readString();
        showOnlytickets = in.readString();
        qty = in.readString();

        try {
            attributeJson = new JSONObject(in.readString());
            attributeFirstJson = new JSONObject(in.readString());
            attributeSecondJson = new JSONObject(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public JSONObject getAttributeJson() {
        return attributeJson;
    }

    public void setAttributeJson(JSONObject attributeJson) {
        this.attributeJson = attributeJson;
    }


    public JSONObject getAttributeFirstJson() {
        return attributeFirstJson;
    }

    public void setAttributeFirstJson(JSONObject attributeJson) {
        this.attributeFirstJson = attributeJson;
    }


    public JSONObject getAttributeSecondJson() {
        return attributeSecondJson;
    }

    public void setAttributeSecondJson(JSONObject attributeJson) {
        this.attributeSecondJson = attributeJson;
    }



    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getFreeCount() {
        return freeCount;
    }

    public void setFreeCount(int freeCount) {
        this.freeCount = freeCount;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getShowOnlytickets() {
        return showOnlytickets;
    }

    public void setShowOnlytickets(String showOnlytickets) {
        this.showOnlytickets = showOnlytickets;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof EventPriceDates) {
            return ((EventPriceDates) obj).getId() == this.getId();
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(text);
        dest.writeString(price);
        dest.writeString(date);
        dest.writeInt(freeCount);
        dest.writeString(display);
        dest.writeString(showOnlytickets);
        dest.writeString(qty);
        dest.writeString(attributeJson.toString());
        dest.writeString(attributeFirstJson.toString());
        dest.writeString(attributeSecondJson.toString());
    }

}
