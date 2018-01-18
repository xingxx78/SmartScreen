package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

/**
 * Created by Think on 4/21/2016.
 */
@Root(name = "FuelMode")
@Order(elements = {"PriceNew", "EffectiveDateTime"})
public class FuelMode {
    @Element(name = "PriceNew", required = false)
    private String priceNewForSendingToFdcServer;

    public String getPriceNewForSendingToFdcServer() {
        return this.priceNewForSendingToFdcServer;
    }

    public void setPriceNewForSendingToFdcServer(String value) {
        this.priceNewForSendingToFdcServer = value;
    }

    @Element(name = "NewPrice", required = false)
    private String newPriceAppliedInFdcServer;

    public String getNewPriceAppliedInFdcServer() {
        return this.newPriceAppliedInFdcServer;
    }

    public void setNewPriceAppliedInFdcServer(String value) {
        this.newPriceAppliedInFdcServer = value;
    }

    @Element(name = "OldPrice", required = false)
    private String oldPrice;

    public String getOldPrice() {
        return this.oldPrice;
    }

    public void setOldPrice(String value) {
        this.oldPrice = value;
    }

    @Element(name = "EffectiveDateTime", required = false)
    private String effectiveDateTime;

    public String getEffectiveDateTime() {
        return this.effectiveDateTime;
    }

//    public void setEffectiveDateTime(String value) {
//        this.effectiveDateTime = value;
//    }

    @Attribute(name = "ModeNo", required = true)
    private int modeNo;

    public int getModeNo() {
        return this.modeNo;
    }

    public void setModeNo(Integer value) {
        this.modeNo = value;
    }

}
