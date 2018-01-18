package com.wayne.www.waynelib.webservice.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shawn on 6/8/2017.
 */

public class PosMop {
    @SerializedName("Name")
    private String name;

    @SerializedName("PaymentId")
    private int paymentId;

    public PosMop() {
        this.name = "placeHolder";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getPaymentId() {
        return this.paymentId;
    }
}
