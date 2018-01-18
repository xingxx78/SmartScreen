package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Think on 4/21/2016.
 */
@Root(name = "DeviceClass")
@Order(elements = {"MaxTrxAmount", "MaxTrxVolume", "ReleasedProducts", "ReleaseToken", "LockFuelSaleTrx", "PayType", "ReservingDeviceId", "FuellingType"})
public class DeviceClass {
    @Attribute(name = "Type")
    private String type = "FP";


    @Attribute(name = "TransactionSeqNo", required = false)
    private String transactionSeqNo;

    @Element(name = "ErrorCode", required = false)
    private String errorCode;

    @Attribute(name = "PumpNo", required = false)
    private Integer pumpNo;

    public Integer getPumpNo() {
        return this.pumpNo;
    }

    public void setPumpNo(Integer value) {
        this.pumpNo = value;
    }

    @Attribute(name = "NozzleNo", required = false)
    private Integer nozzleNo;

    public Integer getNozzleNo() {
        return this.nozzleNo;
    }

    public void setNozzleNo(Integer value) {
        this.nozzleNo = value;
    }

    // used in FuelSaleTrx
    @Element(name = "State", required = false)
    private String state;

    public String getState() {
        return this.state;
    }

    public void setState(String value) {
        this.state = value;
    }

    @Element(name = "ReleaseToken", required = false)
    private String releaseTokenElement;

    public String getReleaseTokenElement() {
        return this.releaseTokenElement;
    }

    public void setReleaseTokenElement(String value) {
        this.releaseTokenElement = value;
    }

    @Element(name = "CompletionReason", required = false)
    private String completionReason;

    public String getCompletionReason() {
        return this.completionReason;
    }

    public void setCompletionReason(String value) {
        this.completionReason = value;
    }

    @Element(name = "Amount", required = false)
    private Float amount;

    public Float getAmount() {
        return this.amount;
    }

    public void setAmount(Float value) {
        this.amount = value;
    }


    @Element(name = "Volume", required = false)
    private Float volume;

    public Float getVolume() {
        return this.volume;
    }

    public void setVolume(Float value) {
        this.volume = value;
    }

    @Element(name = "UnitPrice", required = false)
    private Float unitPrice;

    @Element(name = "NozzleNo", required = false)
    private Integer nozzleNoInElement;

    public Integer getNozzleNoInElement() {
        return this.nozzleNoInElement;
    }

    public Float getUnitPrice() {
        return this.unitPrice;
    }

    public void setUnitPrice(Float value) {
        this.unitPrice = value;
    }

    // 2 product blend
    @Element(name = "VolumeProduct1", required = false)
    private String volumeProduct1;

    public String getVolumeProduct1() {
        return this.volumeProduct1;
    }

    public void setVolumeProduct1(String value) {
        this.volumeProduct1 = value;
    }

    @Element(name = "VolumeProduct2", required = false)
    private String volumeProduct2;

    public String getVolumeProduct2() {
        return this.volumeProduct2;
    }

    public void setVolumeProduct2(String value) {
        this.volumeProduct2 = value;
    }

    // 2 product blend, strange FDC have 2 format...
    @Element(name = "Product1No", required = false)
    private String productNo1_diff;
    @Element(name = "Product2No", required = false)
    private String productNo2_diff;

    @Element(name = "ProductNo1", required = false)
    private String productNo1;

    public String getProductNo1() {
        return this.productNo1;
    }

    public void setProductNo1(String value) {
        this.productNo1 = value;
    }

    @Element(name = "ProductNo2", required = false)
    private String productNo2;

    public String getProductNo2() {
        return this.productNo2;
    }

    public void setProductNo2(String value) {
        this.productNo2 = value;
    }

    @Element(name = "ProductUM", required = false)
    private String productUM;

    /* like: Gal*/
    public String getProductUM() {
        return this.productUM;
    }

    public void setProductUM(String value) {
        this.productUM = value;
    }

    @Element(name = "ProductName", required = false)
    private String productName;

    /* like: SYN 2000*/
    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String value) {
        this.productName = value;
    }

    @Element(name = "BlendRatio", required = false)
    private Integer blendRatio;

    public Integer getBlendRatio() {
        return this.blendRatio;
    }

    public void setBlendRatio(Integer value) {
        this.blendRatio = value;
    }

    @Element(name = "LockingApplicationSender", required = false)
    private String lockingApplicationSender;

    public String getLockingApplicationSender() {
        return this.lockingApplicationSender;
    }

    public void setLockingApplicationSender(String value) {
        this.lockingApplicationSender = value;
    }

    @Element(name = "AuthorisationApplicationSender", required = false)
    private String authorisationApplicationSender;

    public String getAuthorisationApplicationSender() {
        return this.authorisationApplicationSender;
    }

    public void setAuthorisationApplicationSender(String value) {
        this.authorisationApplicationSender = value;
    }

    @Element(name = "DSPFields", required = false)
    private String dSPFields;

    public String getDSPFields() {
        return this.dSPFields;
    }

    public void setDSPFields(String value) {
        this.dSPFields = value;
    }

    @Element(name = "CRCMode", required = false)
    private String cRCMode;

    public String getCRCMode() {
        return this.cRCMode;
    }

    public void setCRCMode(String value) {
        this.cRCMode = value;
    }

    @Element(name = "StartTimeStamp", required = false)
    private String startTimeStamp;

    public String getStartTimeStamp() {
        return this.startTimeStamp;
    }

    public void setStartTimeStamp(String value) {
        this.startTimeStamp = value;
    }

    @Element(name = "EndTimeStamp", required = false)
    private String endTimeStamp;

    public String getEndTimeStamp() {
        return this.endTimeStamp;
    }

    public void setEndTimeStamp(String value) {
        this.endTimeStamp = value;
    }

    @Element(name = "MaxTrxAmount", required = false)
    private Float maxTrxAmount;

    public Float getMaxTrxAmount() {
        return this.maxTrxAmount;
    }

    public void setMaxTrxAmount(Float value) {
        this.maxTrxAmount = value;
    }

    @Element(name = "MaxTrxVolume", required = false)
    private Float maxTrxVolume;

    public Float getMaxTrxVolume() {
        return this.maxTrxVolume;
    }

    public void setMaxTrxVolume(Float value) {
        this.maxTrxVolume = value;
    }

    //AuthoriseFuelPoint
    @Element(name = "LockFuelSaleTrx", required = false)
    private String lockFuelSaleTrx;

    public Boolean getLockFuelSaleTrx() {
        return Boolean.parseBoolean(this.lockFuelSaleTrx);
    }

    public void setLockFuelSaleTrx(Boolean value) {
        this.lockFuelSaleTrx = value ? "True" : "False";
    }

    @Attribute(name = "DeviceID")
    private String deviceId;

    public String getDeviceId() {
        return this.deviceId;
    }

    // could be a int number, or '*'
    public void setDeviceId(String value) {
        this.deviceId = value;
    }

    /*most cases it's a int, but in some message, the '*' is also acceptable, so make it String type*/
    public String getTransactionSeqNo() {
        return this.transactionSeqNo;
    }

    /*most cases it's a int, but in some message, the '*' is also acceptable, so make it String type*/
    public void setTransactionSeqNo(String value) {
        this.transactionSeqNo = value;
    }

    @Attribute(name = "ReleaseToken", required = false)
    private String releaseTokenAttribute;

    public String getReleaseTokenAttribute() {
        return this.releaseTokenAttribute;
    }

    public void setReleaseTokenAttribute(String value) {
        this.releaseTokenAttribute = value;
    }


    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String value) {
        this.errorCode = value;
    }

    @Element(name = "DeviceState", required = false)
    private String deviceState;

    public String getDeviceState() {
        return this.deviceState;
    }

    public void setDeviceState(String value) {
        this.deviceState = value;
    }

    @ElementList(inline = true, required = false)
    private List<Nozzle> nozzles;

    public List<Nozzle> getNozzles() {
        return this.nozzles;
    }

    public void setNozzle(List<Nozzle> values) {
        this.nozzles = values;
    }


    @Element(name = "ErrorID", required = false)
    private String errorID;

    public String getErrorID() {
        return this.errorID;
    }

    public void setErrorID(String value) {
        this.errorID = value;
    }

    @Element(name = "ErrorDescription", required = false)
    private String errorDescription;

    public String getErrorDescription() {
        return this.errorDescription;
    }

    public void setErrorDescription(String value) {
        this.errorDescription = value;
    }

    //FuelSaleTrx============
    @Element(name = "FuelMode", required = false)
    private FuelMode fuleMode;

    public FuelMode getFuelMode() {
        return this.fuleMode;
    }

    public void setFuelMode(FuelMode value) {
        this.fuleMode = value;
    }

    @ElementList(name = "ReleasedProducts", required = false)
    ArrayList<Product> releasedProducts;

    public ArrayList<Product> getReleasedProducts() {
        return this.releasedProducts;
    }

    public void setReleasedProducts(ArrayList<Product> values) {
        this.releasedProducts = values;
    }

    //GetDSPConfiguration
    @ElementList(name = "Product", inline = true, required = false)
    List<Product> products;

    public List<Product> getProducts() {
        return this.products;
    }

    public void setProducts(List<Product> values) {
        this.products = values;
    }

    @ElementList(name = "DeviceClass", inline = true, required = false)
    List<DeviceClass> deviceClasses;

    public List<DeviceClass> getDeviceClasses() {
        return this.deviceClasses;
    }

    public void setDeviceClasses(List<DeviceClass> values) {
        this.deviceClasses = values;
    }

    @Element(name = "ReservingDeviceId", required = false)
    private String reservingDeviceIdField;

    public String getReservingDeviceId() {
        return this.reservingDeviceIdField;
    }

    public void setReservingDeviceId(String value) {
        this.reservingDeviceIdField = value;
    }

    @Element(name = "FuellingType", required = false)
    private String fuellingType;

    public String getFuellingType() {
        return this.fuellingType;
    }

    public void setFuellingType(String value) {
        this.fuellingType = value;
    }

    @Element(name = "PayType", required = false)
    private String payTypeField;

    public String getPayType() {
        return this.payTypeField;
    }

    /* in EMSG, 'PC' is the only accept value*/
    public void setPayType(String value) {
        this.payTypeField = value;
    }

    @Element(name = "DeviceSubState", required = false)
    private String deviceSubState;

    public String getDeviceSubState() {
        return this.deviceSubState;
    }

    public void setDeviceSubState(String value) {
        this.deviceSubState = value;
    }

    @Element(name = "Type", required = false)
    private String typeElement;
}
