package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Think on 4/27/2016.
 */
@Root(name = "ProductID")
public class ProductID {
    // 2 product blend
    @Attribute(name = "ProductNo1", required = false)
    private Integer productNo1;

    public Integer getProductNo1() {
        return this.productNo1;
    }

    public void setProductNo1(Integer value) {
        this.productNo1 = value;
    }

    @Attribute(name = "ProductNo2", required = false)
    private String productNo2;

    public String getProductNo2() {
        return this.productNo2;
    }

    public void setProductNo2(String value) {
        this.productNo2 = value;
    }

    @Attribute(name = "BlendRatio", required = false)
    private Integer blendRatio;

    public Integer getBlendRatio() {
        return this.blendRatio;
    }

    public void setBlendRatio(Integer value) {
        this.blendRatio = value;
    }

    @Attribute(name = "BlendProductNo", required = false)
    private Integer blendProductNo;

    public Integer getBlendProductNo() {
        return this.blendProductNo;
    }

    public void setBlendProductNo(Integer value) {
        this.blendProductNo = value;
    }
}
