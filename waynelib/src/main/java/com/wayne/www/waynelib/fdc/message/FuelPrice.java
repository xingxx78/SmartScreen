package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 * Created by Think on 4/27/2016.
 */
@Root(name = "FuelPrice")
public class FuelPrice {
    @Attribute(name = "ModeNo", required = false)
    private Integer modeNo;

    public Integer getModeNo() {
        return this.modeNo;
    }

    public void setModeNo(Integer value) {
        this.modeNo = value;
    }

    @Text
    public String unitPrice;

    public float getUnitPrice() {
        return Float.parseFloat(this.unitPrice);
    }
}
