package com.wayne.www.waynelib.ui;

/**
 * Created by Think on 5/22/2016.
 */
public class Discount {
    public Discount(String name, DiscountType type, float value) {
        this.Name = name;
        this.DiscountType = type;
        this.Value = value;
    }

    public String Name;
    public DiscountType DiscountType;
    // with 100%, like 3 indicates 3%, 15 indicates 15%
    public Float Value;
}
