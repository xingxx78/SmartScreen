package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Think on 4/21/2016.
 */
@Root(name = "Nozzle")
public class Nozzle {
    public Nozzle() {
    }

    public Nozzle(Integer nozzleNo, String logicalNozzle, String logicalState, String errorCode) {
        this.nozzleNo = nozzleNo;
        this.logicalNozzle = logicalNozzle;
        this.logicalState = logicalState;
        this.errorCode = errorCode;
    }

    @Attribute(name = "NozzleNo")
    private Integer nozzleNo;

    public Integer getNozzleNo() {
        return this.nozzleNo;
    }

    @Element(name = "LogicalNozzle", required = false)
    private String logicalNozzle;

    public String getLogicalNozzle() {
        return this.logicalNozzle;
    }

    @Element(name = "LogicalState", required = false)
    private String logicalState;

    public String getLogicalState() {
        return this.logicalState;
    }

    @Element(name = "TankLogicalState", required = false)
    private String tankLogicalState;

    public String getTankLogicalState() {
        return this.tankLogicalState;
    }

    @Element(name = "ErrorCode", required = false)
    private String errorCode;

    public String getErrorCode() {
        return this.errorCode;
    }

    @Element(name = "ProductID", required = false)
    private ProductID productID;

    public ProductID getProductID() {
        return this.productID;
    }

    public void setProductID(ProductID value) {
        this.productID = value;
    }

    public Nozzle convert()
    {
        Nozzle modelNozzle = new Nozzle();
        return modelNozzle;
    }
}
