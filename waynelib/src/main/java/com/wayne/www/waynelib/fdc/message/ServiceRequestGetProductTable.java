package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Think on 4/19/2016.
 */
@Root(name = "ServiceRequest")
public class ServiceRequestGetProductTable extends ServiceRequest {
    public ServiceRequestGetProductTable() {
        super.setRequestType("GetProductTable");
    }
}
