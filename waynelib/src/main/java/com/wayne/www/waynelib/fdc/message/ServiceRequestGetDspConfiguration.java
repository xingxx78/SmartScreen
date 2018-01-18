package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Root;

/**
 * Created by Think on 4/26/2016.
 */
@Root(name = "ServiceRequest")
public class ServiceRequestGetDspConfiguration extends ServiceRequest {
    public ServiceRequestGetDspConfiguration() {
        super.setRequestType("GetDSPConfiguration");
    }
}
