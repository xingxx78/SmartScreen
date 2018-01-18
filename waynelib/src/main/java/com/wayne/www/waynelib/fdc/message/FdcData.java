package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Think on 4/19/2016.
 */
@Root(name = "FDCdata")
public class FdcData {
    @Element(name = "FDCTimeStamp")
    private String FDCTimeStampField;

    public String getFdcTimeStamp() {
        return this.FDCTimeStampField;
    }

    public void setFdcTimeStamp(String value) {
        this.FDCTimeStampField = value;
    }


    //VersionInfo ====================
    @Element(name = "InterfaceVersion", required = false)
    private String interfaceVersionField;
    @Element(name = "FDCsupplier", required = false)
    private String fdcSupplierField;
    @Element(name = "FDCrelease", required = false)
    private String fdcReleaseField;
    @Element(name = "FDCversion", required = false)
    private String fdcVersionField;
    @Element(name = "FDChotfix", required = false)
    private String fdcHotfixField;
    @Element(name = "ErrorCode", required = false)
    private String errorCodeField;

    public String getInterfaceVersion() {
        return this.interfaceVersionField;
    }

    public String getFdcSupplier() {
        return this.fdcSupplierField;
    }

    public String getFdcRelease() {
        return this.fdcReleaseField;
    }

    public String getFdcVersion() {
        return this.fdcVersionField;
    }

    public String getFdcHotfix() {
        return this.fdcHotfixField;
    }

    public String getErrorCode() {
        return this.errorCodeField;
    }


    //GetProductTable  ====================
    @ElementList(name = "FuelProducts", required = false)
    private List<Product> fuelProducts;

    public List<Product> getFuelProducts() {
        return this.fuelProducts;
    }


    //GetConfiguration=============================
    private List<ServiceDeviceClassConfiguration> DeviceClassField;

    @Element(name = "FDCStatus", required = false)
    private String FDCStatusField;

    public List<ServiceDeviceClassConfiguration> getDeviceClassConfiguration() {
        return this.DeviceClassField;
    }

    public void setDeviceClassConfiguration(List<ServiceDeviceClassConfiguration> value) {
        this.DeviceClassField = value;
    }

    public String getFdcStatus() {
        return this.FDCStatusField;
    }

    public void setFdcStatus(String value) {
        this.FDCStatusField = value;
    }

    //LockFuelSaleTrx==============================
    @ElementList(name = "DeviceClass", inline = true, required = false)
    private List<DeviceClass> deviceClasses;

    public List<DeviceClass> getDeviceClasses() {
        return this.deviceClasses;
    }

    public void setDeviceClasses(List<DeviceClass> values) {
        this.deviceClasses = values;
    }

    public void setSingleDeviceClass(DeviceClass value) {
        List<DeviceClass> p = new ArrayList<>();
        p.add(value);
        this.setDeviceClasses(p);
    }

    //GetCountrySettings==============================
    @Element(name = "VolumeUnit", required = false)
    private String volumeUnit;

    public String getVolumeUnit() {
        return this.volumeUnit;
    }

    @Element(name = "CurrencyCode", required = false)
    private String currencyCode;

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    @Element(name = "LevelUnit", required = false)
    private String levelUnit;

    public String getLevelUnit() {
        return this.levelUnit;
    }

    @Element(name = "TemperatureUnit", required = false)
    private String temperatureUnit;

    public String getTemperatureUnit() {
        return this.temperatureUnit;
    }

    @Element(name = "ThousandDelimiter", required = false)
    private String thousandDelimiter;

    public String getThousandDelimiter() {
        return this.thousandDelimiter;
    }

    @Element(name = "DecimalSign", required = false)
    private String decimalSign;

    public String getDecimalSign() {
        return this.decimalSign;
    }

    @Element(name = "LanguageCode", required = false)
    private String languageCode;

    public String getLanguageCode() {
        return this.languageCode;
    }


    @Element(name = "CountryCode", required = false)
    private String countryCode;

    public String getCountryCode() {
        return this.countryCode;
    }

    @Element(name = "Product", required = false)
    private ChangeFuelPriceProduct changeFuelPriceProduct;

    public ChangeFuelPriceProduct getChangeFuelPriceProduct() {
        return this.changeFuelPriceProduct;
    }
}
