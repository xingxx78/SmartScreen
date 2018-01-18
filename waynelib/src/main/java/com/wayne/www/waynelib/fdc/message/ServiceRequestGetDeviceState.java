package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Think on 4/19/2016.
 */
@Root(name = "ServiceRequest")
public class ServiceRequestGetDeviceState extends ServiceRequest {
    public ServiceRequestGetDeviceState() {
        super.setRequestType("GetDeviceState");
    }

//    public List<PosDataGetDeviceState> getPosData() {
//        return this.POSdata;
//    }
//
//    public void setPosData(List<PosDataGetDeviceState> value) {
//        this.POSdata = value;
//    }
}
