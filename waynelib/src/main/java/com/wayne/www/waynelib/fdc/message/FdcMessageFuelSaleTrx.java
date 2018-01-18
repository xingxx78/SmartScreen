package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Root;

/**
 * Created by Think on 4/21/2016.
 */
@Root(name = "FDCMessage")
public class FdcMessageFuelSaleTrx extends FdcMessage {
    public FdcMessageFuelSaleTrx() {
        super.setMessageType("FuelSaleTrx");
    }
}
