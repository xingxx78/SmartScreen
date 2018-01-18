package com.wayne.www.waynelib.webservice.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shawn on 7/29/2017.
 */

public class SiteDevice {
    @SerializedName("Id")
    private String id;
    @SerializedName("DeviceType")
    private int deviceType;

    @SerializedName("Model")
    private String model;
    @SerializedName("Description")
    private String description;

    @SerializedName("PosDeviceFdcClientId")
    private String posDeviceFdcClientId;

    @SerializedName("AsSubjectOfBusinessUnit")
    private BusinessUnit asSubjectOfBusinessUnit;

    public String getPosDeviceFdcClientId() {
        return this.posDeviceFdcClientId;
    }

    public String getModel() {
        return this.model;
    }

    public String getDeviceType() {
//        Pos = 0,
//        Fusion = 1,
        if (this.deviceType == 0) return "POS";
        else if (this.deviceType == 1) return "Fusion";
        return null;
    }
}
