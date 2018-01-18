package com.wayne.www.waynelib.fdc.message;

/**
 * Created by Think on 4/19/2016.
 */
public class ServiceNozzleFPDeviceClassConfiguration {
    private String FPNozzleNoField;
    private ServiceProductIdFPDeviceClassConfiguration[] FPProductIdField;

    public String getFPNozzleNo() {
        return this.FPNozzleNoField;
    }

    public void setFPNozzleNo(String value) {
        this.FPNozzleNoField = value;
    }

    public ServiceProductIdFPDeviceClassConfiguration[] getFPProductId() {
        return this.FPProductIdField;
    }

    public void setFPProductId(ServiceProductIdFPDeviceClassConfiguration[] value) {
        this.FPProductIdField = value;
    }
}
