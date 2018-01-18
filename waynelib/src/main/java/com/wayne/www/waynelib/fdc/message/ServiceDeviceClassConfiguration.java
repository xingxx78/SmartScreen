package com.wayne.www.waynelib.fdc.message;

/**
 * Created by Think on 4/19/2016.
 */
public class ServiceDeviceClassConfiguration {
    private String TypeField;
    private String DeviceIdField;
    //FP
    private String FPPumpNoField;
    private ServiceNozzleFPDeviceClassConfiguration[] FPNozzleField;
    private ServiceDeviceClassConfiguration[] DeviceClassField;

    public String getType() {
        return this.TypeField;
    }

    public void setType(String value) {
        this.TypeField = value;
    }

    public String getDeviceID() {
        return this.DeviceIdField;
    }

    public void setDeviceID(String value) {
        this.DeviceIdField = value;
    }

    public String getFPPumpNo() {
        return this.FPPumpNoField;
    }

    public void setFPPumpNo(String value) {
        this.FPPumpNoField = value;
    }

    public ServiceNozzleFPDeviceClassConfiguration[] getFPNozzle() {
        return this.FPNozzleField;
    }

    public void setFPNozzle(ServiceNozzleFPDeviceClassConfiguration[] value) {
        this.FPNozzleField = value;
    }
}
