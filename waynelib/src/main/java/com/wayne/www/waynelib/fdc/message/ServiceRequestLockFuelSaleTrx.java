package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Root;

/**
 * Created by Think on 4/21/2016.
 */
@Root(name = "ServiceRequest")
public class ServiceRequestLockFuelSaleTrx extends ServiceRequest {
    public ServiceRequestLockFuelSaleTrx() {
        super.setRequestType("LockFuelSaleTrx");
    }
}
