package com.wayne.www.waynelib.fdc.message;
import org.simpleframework.xml.Root;

/**
 * Created by Think on 4/26/2016.
 */
@Root(name = "FDCMessage")
public class FdcMessageFdcStarted extends FdcMessage {
    public FdcMessageFdcStarted() {
        super.setMessageType("FdcMessageFdcStarted");
    }
}