package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Root;

/**
 * Created by Think on 4/21/2016.
 */
@Root(name = "ServiceRequest")
public class ServiceRequestGetFuelSalesTrxDetails extends ServiceRequest {
    public ServiceRequestGetFuelSalesTrxDetails() {
        super.setRequestType("GetFuelSaleTrxDetails");
    }
}
