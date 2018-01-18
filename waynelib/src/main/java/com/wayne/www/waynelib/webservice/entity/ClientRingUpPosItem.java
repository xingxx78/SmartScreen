package com.wayne.www.waynelib.webservice.entity;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shawn on 6/16/2017.
 */

public class ClientRingUpPosItem {
    /**
     * database unique id
     */
    @SerializedName("PosItemUniqueId")
    private String posItemUniqueId;
    @SerializedName("Qty")
    private float qty;
    @SerializedName("ClientRingUpTime")
    private String clientRingUpTime;

    @SerializedName("FuelItemSoldOnPumpId")
    private Integer fuelItemSoldOnPumpId;
    @SerializedName("FuelItemSoldOnPumpNozzleId")
    private Integer fuelItemSoldOnPumpNozzleId;

    @SerializedName("FuelItemOriginalGrossAmount")
    private Float fuelItemOriginalGrossAmount;

    @SerializedName("FuelItemClientSideItemId")
    private Integer fuelItemClientSideItemId;
    @SerializedName("FuelItemClientSidePrice")
    private Float fuelItemClientSidePrice;
    @SerializedName("FuelItemFdcTransactionSeqNo")
    private String fuelItemFdcTransactionSeqNo;
    @SerializedName("FuelItemFdcReleaseTokenAttribute")
    private Integer fuelItemFdcReleaseTokenAttribute;

    @SerializedName("VoidOperationForWhichLineNum")
    private Integer voidOperationForWhichLineNum;

    @SerializedName("TransactionComment")
    private String transactionComment;

    public ClientRingUpPosItem() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        this.clientRingUpTime = currentDateandTime;
    }

    public void setPosItemUniqueId(String id) {
        this.posItemUniqueId = id;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public void setFuelItemSoldOnPumpId(Integer pumpId) {
        this.fuelItemSoldOnPumpId = pumpId;
    }

    public void setFuelItemSoldOnPumpNozzleId(Integer nozzleId) {
        this.fuelItemSoldOnPumpNozzleId = nozzleId;
    }

    public void setFuelItemClientSideItemId(int clientItemId) {
        this.fuelItemClientSideItemId = clientItemId;
    }

    public void setFuelItemClientSidePrice(float clientPrice) {
        this.fuelItemClientSidePrice = clientPrice;
    }

    /// <summary>
    /// the amount which calculated by local FC, typically Fuel item's amount should not re-cacualted in server again(by Qty*Price, may cause rounding issue),
    /// just trust the value from local.
    /// </summary>
    public void setFuelItemOriginalGrossAmount(float grossAmount) {
        this.fuelItemOriginalGrossAmount = grossAmount;
    }

    public void setVoidOperationForWhichLineNum(int lineNum) {
        this.voidOperationForWhichLineNum = lineNum;
    }

    public void setFuelItemFdcTransactionSeqNo(String seqNo) {
        this.fuelItemFdcTransactionSeqNo = seqNo;
    }

    public void setFuelItemFdcReleaseTokenAttribute(Integer releaseTokenAttribute) {
        this.fuelItemFdcReleaseTokenAttribute = releaseTokenAttribute;
    }

    public String getTransactionComment() {
        return this.transactionComment;
    }

    public void setTransactionComment(String value) {
        this.transactionComment = value;
    }
}