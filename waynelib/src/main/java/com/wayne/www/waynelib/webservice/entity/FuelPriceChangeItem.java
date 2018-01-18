package com.wayne.www.waynelib.webservice.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Justin on 12/11/2017.
 */

public class FuelPriceChangeItem implements Parcelable {
    @SerializedName("Id")
    private String id;

    @SerializedName("Description")
    private String description;

    @SerializedName("CurrentPrice")
    private float currentPrice;

    @SerializedName("NewPrice")
    private float newPrice;

    @SerializedName("TimeOfCreation")
    private Date timeOfCreation;

    @SerializedName("TimeOfEffective")
    private Date timeOfEffective;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setCurrentPrice(float currentPrice) { this.currentPrice = currentPrice; }

    public float getCurrentPrice() {
        return currentPrice;
    }

    public void setNewPrice(float newPrice) { this.newPrice = newPrice; }

    public float getNewPrice() {
        return newPrice;
    }

    public Date getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(Date timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public Date getTimeOfEffective() {
        return timeOfEffective;
    }

    public void setTimeOfEffective(Date timeOfEffective) {
        this.timeOfEffective = timeOfEffective;
    }


    public static final Creator<FuelPriceChangeItem> CREATOR = new Creator<FuelPriceChangeItem>() {
        @Override
        public FuelPriceChangeItem createFromParcel(Parcel in) {
            return new FuelPriceChangeItem(in);
        }

        @Override
        public FuelPriceChangeItem[] newArray(int size) {
            return new FuelPriceChangeItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public FuelPriceChangeItem() {

    }

    protected FuelPriceChangeItem(Parcel source) {
        this.id = source.readString();
        this.description = source.readString();
        this.currentPrice = source.readFloat();
        this.newPrice = source.readFloat();
        this.timeOfCreation = new Date(source.readLong());
        this.timeOfEffective = new Date(source.readLong());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.description);
        parcel.writeFloat(this.currentPrice);
        parcel.writeFloat(this.newPrice);
        parcel.writeLong(timeOfCreation.getTime());
        parcel.writeLong(timeOfEffective.getTime());
    }
}
