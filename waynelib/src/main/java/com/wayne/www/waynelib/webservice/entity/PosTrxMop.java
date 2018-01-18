package com.wayne.www.waynelib.webservice.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayInputStream;

/**
 * Created by Shawn on 6/8/2017.
 */

public class PosTrxMop {
    @SerializedName("Id")
    private String id;

    @SerializedName("LineNum")
    private int lineNum;

    @SerializedName("Paid")
    private float paid;

    @SerializedName("PayBack")
    private float payBack;

    @SerializedName("RawResult")
    private String rawResult;

    @SerializedName("ICCardNumber")
    private String icCardNumber;

    @SerializedName("CarPlatePicture")
    private String carPlatePictureBase64EncodedString;

    @SerializedName("CarPlateNumber")
    private String carPlateNumber;

//    @SerializedName("CarPlatePicture")
//    public String carPlatePicture;

    @SerializedName("PosTrxId")
    private String posTrxId;

    @SerializedName("Mop")
    private PosMop posMop;

    // will always null since server db will not save it to db.
    @SerializedName("AuthCode")
    private String authCode;

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getAuthCode() {
        return this.authCode;
    }

    public String getIcCardNumber() {
        return this.icCardNumber;
    }

    public int getPosMopPaymentId() {
        if (this.posMop == null) return -9999;
        return this.posMop.getPaymentId();
    }

    public void setPosMopPaymentId(int posMopPaymentId) {
        if (this.posMop == null) this.posMop = new PosMop();
        this.posMop.setPaymentId(posMopPaymentId);
    }

    public float getPaid() {
        return this.paid;
    }

    public String getPosMopName() {
        if (this.posMop == null) return "";
        return this.posMop.getName();
    }

    public String getRawResult() {
        return this.rawResult;
    }

    public void setPaid(float paid) {
        this.paid = paid;
    }

    public void setPayBack(float payBack) {
        this.payBack = payBack;
    }

    public String getCarPlateNumber() {
        return this.carPlateNumber;
    }

    public String getCarPlatePictureBase64Encoded() {
        return this.carPlatePictureBase64EncodedString;
    }

    public Bitmap getCarPlatePictureBase64Decoded() {
        if (this.carPlatePictureBase64EncodedString == null) return null;
        byte[] raw = Base64.decode(this.carPlatePictureBase64EncodedString, Base64.DEFAULT);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(raw);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }
}
