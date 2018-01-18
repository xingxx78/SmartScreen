package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

/**
 * Created by Think on 4/27/2016.
 */
@Root(name = "Product")
public class ChangeFuelPriceProduct {
    @Attribute(name = "ProductNo", required = false)
    private String productNoInAttribute;

    public String getProductNoInAttribute() {
        return this.productNoInAttribute;
    }

    public void setProductNoInAttribute(String value) {
        this.productNoInAttribute = value;
    }

    @Element(name = "ProductNo", required = false)
    private String productNo;

    public String getProductNo() {
        return this.productNo;
    }

    public void setProductNo(String value) {
        this.productNo = value;
    }

    @Element(name = "FuelMode", required = true)
    private FuelMode fuelMode;

    public FuelMode getModeNo() {
        return this.fuelMode;
    }

    public void setModeNo(FuelMode value) {
        this.fuelMode = value;
    }

    @Element(name = "EffectiveDateTime", required = false)
    private String effectiveDateTime;

    public String getEffectiveDateTime() {
        return this.effectiveDateTime;
    }
}
