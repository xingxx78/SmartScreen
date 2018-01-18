package com.wayne.www.waynelib.fdc.model;

/**
 * Created by Think on 3/15/2016.
 */
public class Product {
    public int ProductNo;
    public String ProductName;
    public float UnitPrice;

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Nozzle))
            return false;

        Product target = (Product) obj;
        if (target.ProductNo == this.ProductNo
                && target.ProductName.equals(this.ProductName)
                && target.UnitPrice == this.UnitPrice)
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return this.ProductNo ^ this.ProductName.hashCode() ^ (int) this.UnitPrice;
    }

    public static Product convert(com.wayne.www.waynelib.fdc.message.Product fdcProduct) {
        Product modelProduct = new Product();
        modelProduct.ProductNo = fdcProduct.getProductNoInAttribute();
        modelProduct.ProductName = fdcProduct.getProductName();
        modelProduct.UnitPrice = fdcProduct.getFuelPrice().getUnitPrice();
        return modelProduct;
    }
}
