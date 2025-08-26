package com.comics.lounge.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wallet implements Parcelable {

    public static final Parcelable.Creator<Wallet> CREATOR = new Parcelable.Creator<Wallet>() {
        @Override
        public Wallet createFromParcel(Parcel source) {
            return new Wallet(source);
        }

        @Override
        public Wallet[] newArray(int size) {
            return new Wallet[size];
        }
    };
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("access_event")
    @Expose
    public Integer accessEvent;
    @SerializedName("event_count_allowed")
    @Expose
    public String eventCountAllowed;
    @SerializedName("event_count_left")
    @Expose
    public Integer eventCountLeft;
    @SerializedName("event_cancel_penality")
    @Expose
    public Integer eventCancelPenality;
    @SerializedName("free_event_restored")
    @Expose
    public String freeEventRestored;
    @SerializedName("balance")
    @Expose
    public String balance;

    public Wallet() {
    }

    protected Wallet(Parcel in) {
        this.status = in.readString();
        this.accessEvent = (Integer) in.readValue(Integer.class.getClassLoader());
        this.eventCountAllowed = in.readString();
        this.eventCountLeft = (Integer) in.readValue(Integer.class.getClassLoader());
        this.eventCancelPenality = (Integer) in.readValue(Integer.class.getClassLoader());
        this.balance = in.readString();
        this.freeEventRestored = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeValue(this.accessEvent);
        dest.writeString(this.eventCountAllowed);
        dest.writeValue(this.eventCountLeft);
        dest.writeValue(this.eventCancelPenality);
        dest.writeString(this.freeEventRestored);
        dest.writeString(this.balance);
    }
}
