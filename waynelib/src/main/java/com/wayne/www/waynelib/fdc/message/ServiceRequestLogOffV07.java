package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Root;

/**
 * Created by Think on 4/19/2016.
 */
@Root(name = "ServiceRequest")
public class ServiceRequestLogOffV07 extends ServiceRequest {
    public ServiceRequestLogOffV07() {
        super.setRequestType("LogOff");
    }
}
