package com.wayne.www.waynelib.fdc.message;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Order;

/**
 * Created by Think on 4/19/2016.
 */
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd")
})
@Order(attributes = {"RequestType", "ApplicationSender", "WorkstationID", "RequestID"})
public class ServiceRequest extends BasePosMessage {
    @Attribute(name = "noNamespaceSchemaLocation", required = false)
    @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi")
    private String unknownXmlNameSpacePlaceHolder;// = "FDC_FuelSaleTrx_Unsolicited.xsd";

    private Object userToken;

    @Attribute(name = "RequestType")
    private String requestTypeField;

    @Attribute(name = "ApplicationSender")
    private String applicationSenderField = "";

    @Attribute(name = "WorkstationID")
    private String workstationIDField = "";

    @Attribute(name = "RequestID")
    private String requestIDField;

    public String getRequestType() {
        return this.requestTypeField;
    }

    public void setRequestType(String value) {
        this.requestTypeField = value;
    }

    public String getApplicationSender() {
        return this.applicationSenderField;
    }

    public void setApplicationSender(String value) {
        if (value.length() > 8)
            throw new IllegalArgumentException("ApplicationSender max length is 8, input value: " + value + " does not follow");
        this.applicationSenderField = value;
    }

    public String getWorkstationID() {
        return this.workstationIDField;
    }

    public void setWorkstationID(String value) {
        if (value.length() > 8)
            throw new IllegalArgumentException("WorkstationID max length is 8, input value: " + value + " does not follow");
        this.workstationIDField = value;
    }

    public String getRequestID() {
        return this.requestIDField;
    }

    public void setRequestID(String value) {
        if (value.length() > 8)
            throw new IllegalArgumentException("RequestID max length is 8");
        this.requestIDField = value;
    }
}
