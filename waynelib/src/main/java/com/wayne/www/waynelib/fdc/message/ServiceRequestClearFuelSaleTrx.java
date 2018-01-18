package com.wayne.www.waynelib.fdc.message;
import org.simpleframework.xml.Root;

/**
 * Created by Think on 4/27/2016.
 */
@Root(name = "ServiceRequest")
public class ServiceRequestClearFuelSaleTrx extends ServiceRequest {
    public ServiceRequestClearFuelSaleTrx() {
        super.setRequestType("ClearFuelSaleTrx");
    }
}
