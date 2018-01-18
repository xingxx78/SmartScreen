package com.wayne.www.waynelib.webservice.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shawn on 7/24/2017.
 */

public class ShiftInfo {
    @SerializedName("ShiftId")
    private int shiftId;
    @SerializedName("StartDate")
    private String startDate;
    @SerializedName("EndDate")
    private String endDate;
    @SerializedName("DurationSeconds")
    private float durationSeconds;
    @SerializedName("StartedByWho")
    private String startedByWho;
    @SerializedName("EndedByWho")
    private String endedByWho;
    @SerializedName("TrxCount")
    private int trxCount;
    @SerializedName("TotalAmount")
    private float totalAmount;
    @SerializedName("ShiftStatus")
    private String shiftStatus;

    public int getShiftId() {
        return this.shiftId;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public float getDurationSeconds() {
        return this.durationSeconds;
    }

    public String getStartedByWho() {
        return this.startedByWho;
    }

    public String getEndedByWho() {
        return this.endedByWho;
    }

    public int getTrxCount() {
        return this.trxCount;
    }

    public float getTotalAmount() {
        return this.totalAmount;
    }

    public String getShiftStatus() {
        return this.shiftStatus;
    }
}
