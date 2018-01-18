package com.wayne.www.waynelib.webservice.entity;

import com.google.gson.annotations.SerializedName;
import com.wayne.www.waynelib.NotifyPropertyChanged;

/**
 * Created by Shawn on 6/8/2017.
 */

public class PosTrxItem extends NotifyPropertyChanged {
    @SerializedName("Id")
    private String id;
    @SerializedName("LineNum")
    private int lineNum;
    @SerializedName("PosItemId")
    private String posItemUniqueId;
    @SerializedName("Qty")
    private float qty;
    @SerializedName("PosTrxId")
    private String posTrxId;
    @SerializedName("Item")
    private PosItem posItem;

    @SerializedName("Voided")
    private boolean voided;

    @SerializedName("FuelItemSoldOnPumpId")
    private Integer fuelItemSoldOnPumpId;
    @SerializedName("FuelItemSoldOnPumpNozzleId")
    private Integer fuelItemSoldOnPumpNozzleId;

    @SerializedName("FuelItemOriginalGrossAmount")
    private Float fuelItemOriginalGrossAmount;

    @SerializedName("FuelItemFdcTransactionSeqNo")
    private String fuelItemFdcTransactionSeqNo;
    @SerializedName("FuelItemFdcReleaseTokenAttribute")
    private Integer fuelItemFdcReleaseTokenAttribute;

    public PosItem getPosItem() {
        return this.posItem;
    }

    public void setPosItem(PosItem posItem) {
        this.posItem = posItem;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getLineNum() {
        return this.lineNum;
    }

    public float getQty() {
        return this.qty;
    }

    public boolean getVoided() {
        return this.voided;
    }

    public Integer getFuelItemSoldOnPumpId() {
        return this.fuelItemSoldOnPumpId;
    }

    public Integer getFuelItemSoldOnPumpNozzleId() {
        return this.fuelItemSoldOnPumpNozzleId;
    }

    public Float getFuelItemOriginalGrossAmount() {
        return this.fuelItemOriginalGrossAmount;
    }

    public String getFuelItemFdcTransactionSeqNo() {
        return this.fuelItemFdcTransactionSeqNo;
    }

    public Integer getFuelItemFdcReleaseTokenAttribute() {
        return this.fuelItemFdcReleaseTokenAttribute;
    }

}
