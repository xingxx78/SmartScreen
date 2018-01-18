package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Think on 4/19/2016.
 */
@Root(name = "POSdata")
@Order(elements = {"POSTimeStamp", "POSName", "ResponsePort", "UnsolicitedPort", "interfaceVersion"})
public class PosData {
    @Element(name = "POSTimeStamp")
    private String posTimeStamp;

    public String getPosTimeStamp() {
        return this.posTimeStamp;
    }

    public void setPosTimeStamp(String value) {
        this.posTimeStamp = value;
    }

    //Logon========================================
    @Element(name = "POSName", required = false)
    private String POSNameField;
    @Element(name = "ResponsePort", required = false)
    private Integer responsePortField;
    @Element(name = "UnsolicitedPort", required = false)
    private Integer unsolicitedPortField;
    private byte[][] validationInfoField;
    //private String interfaceVersionField;

    @Element(name = "interfaceVersion", required = false)
    private String InterfaceVersionField;
    private String posInfoField;
    private String posValidationField;

    public String getPOSName() {
        return this.POSNameField;
    }

    public void setPosName(String value) {
        this.POSNameField = value;
    }

    public Integer getResponsePort() {
        return this.responsePortField;
    }

    public void setResponsePort(Integer value) {
        this.responsePortField = value;
    }

    public Integer getUnsolicitedPort() {
        return this.unsolicitedPortField;
    }

    public void setUnsolicitedPort(Integer value) {
        this.unsolicitedPortField = value;
    }

    public byte[][] getValidationInfo() {
        return this.validationInfoField;
    }

    public void setValidationInfo(byte[][] value) {
        this.validationInfoField = value;
    }

    public String getInterfaceVersion() {
        return this.InterfaceVersionField;
    }

    public void setInterfaceVersion(String value) {
        this.InterfaceVersionField = value;
    }

//    public String getinterfaceVersion() {
//        return this.interfaceVersionField;
//    }
//
//    public void setinterfaceVersion(String value) {
//        this.interfaceVersionField = value;
//    }

    public String getPosInfo() {
        return this.posInfoField;
    }

    public void setPosInfo(String value) {
        this.posInfoField = value;
    }

    public String getPosValidation() {
        return this.posValidationField;
    }

    public void setPosValidation(String value) {
        this.posValidationField = value;
    }

    //LockFuelSaleTrx==============================
    @Element(name = "DeviceClass", required = false)
    private DeviceClass deviceClass;

    public DeviceClass getDeviceClass() {
        return this.deviceClass;
    }

    public void setDeviceClass(DeviceClass value) {
        this.deviceClass = value;
    }


    @Element(name = "Product",  required = false)
    ChangeFuelPriceProduct fuelPrice;

    /* as a client(msg sender), we have no reason to getPosData, but leave here for unit testing purpose */
    public ChangeFuelPriceProduct getFuelPrice() {
        return this.fuelPrice;
    }

    public void setFuelPrice(ChangeFuelPriceProduct value) {
        this.fuelPrice = value;
    }
}
