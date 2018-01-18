package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Think on 4/21/2016.
 */


@Root(name = "FDCMessage")
@Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi")
public class FdcMessage extends BaseFdcMessage {
    @Attribute(name = "noNamespaceSchemaLocation", required = false)
    @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi")
    private String unknownXmlNameSpacePlaceHolder = "FDC_FuelSaleTrx_Unsolicited.xsd";
    @Attribute(name = "MessageType")
    private String messageTypeField;
    @Attribute(name = "ApplicationSender")
    private String applicationSenderField;

    @Attribute(name = "WorkstationID")
    private String workstationIDField;

    @Attribute(name = "MessageID")
    private String messageIdField;

    public String getMessageType() {
        return this.messageTypeField;
    }

    public void setMessageType(String value) {
        this.messageTypeField = value;
    }

    public String getMessageId() {
        return this.messageIdField;
    }

    public void setMessageId(String value) {
        this.messageIdField = value;
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
}
