package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created by Think on 4/19/2016.
 */
public class ServiceResponse extends BaseFdcMessage {
    @Attribute(name = "noNamespaceSchemaLocation", required = false)
    private String unknownXmlNameSpacePlaceHolder;
    @Attribute(name = "RequestType")
    private String requestTypeField;
    @Attribute(name = "ApplicationSender")
    private String applicationSenderField;
    @Attribute(name = "WorkstationID")
    private String workstationIDField;
    @Attribute(name = "RequestID")
    private String requestIDField;
    @Attribute(name = "OverallResult")
    private String overallResultField;

    public String getRequestType() {
        return this.requestTypeField;
    }

    public void setReqeustType(String value) {
        this.requestTypeField = value;
    }

    public String getApplicationSender() {
        return this.applicationSenderField;
    }

    public void setApplicationSender(String value) {
        this.applicationSenderField = value;
    }

    public String getWorkstationID() {
        return this.workstationIDField;
    }

    public void setWorkstationID(String value) {
        this.workstationIDField = value;
    }

    public String getRequestID() {
        return this.requestIDField;
    }

    public void setRequestID(String value) {
        this.requestIDField = value;
    }

    public String getOverallResult() {
        return this.overallResultField;
    }

    public void setOverallResult(String value) {
        this.overallResultField = value;
    }


}
