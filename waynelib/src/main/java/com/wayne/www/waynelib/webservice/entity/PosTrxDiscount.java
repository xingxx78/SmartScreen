package com.wayne.www.waynelib.webservice.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shawn on 6/8/2017.
 */

public class PosTrxDiscount {
    @SerializedName("Id")
    private String id;
    @SerializedName("PosDiscountId")
    private int posDiscountId;
    @SerializedName("DiscountAmount")
    private float discountAmount;
    @SerializedName("PosTrxId")
    private int posTrxId;
}
