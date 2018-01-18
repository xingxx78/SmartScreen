package com.wayne.www.waynelib.fdc.message;

import android.util.Log;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Think on 4/21/2016.
 */
@Root(name = "Product")
public class Product {
    @Attribute(name = "ProductNo")
    private Integer productNoInAttribute;
    @Attribute(name = "ProductName", required = false)
    private String productName;

    public Integer getProductNoInAttribute() {
        return this.productNoInAttribute;
    }

    public void setProductNoInAttribute(Integer value) {
        this.productNoInAttribute = value;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String value) {
        this.productName = value;
    }

    @Element(name = "ProductNo", required = false)
    private Integer productNoInElement;

    public Integer getProductNoInElement() {
        return this.productNoInElement;
    }

    public void setProductNoInElement(Integer value) {
        this.productNoInElement = value;
    }

    @Element(name = "FuelMode", required = false)
    private FuelMode fuelMode;

    public FuelMode getFuelMode() {
        return this.fuelMode;
    }

    @Element(name = "EffectiveDateTime", required = false)
    private String effectiveDateTime;

    @ElementList(inline = true, name = "FuelPrice", required = false)
    private List<FuelPrice> fuelPrices;

    public FuelPrice getFuelPrice() {
        return this.fuelPrices.get(0);
    }

    public void setFuelPrice(float fuelPrice) {
        FuelPrice price =  new FuelPrice();
        price.unitPrice = String.valueOf(fuelPrice);
        this.fuelPrices.set(0, price);
    }


    public com.wayne.www.waynelib.fdc.model.Product convert() {
        com.wayne.www.waynelib.fdc.model.Product modelProduct = new com.wayne.www.waynelib.fdc.model.Product();
        modelProduct.ProductName = this.productName;
        modelProduct.ProductNo = this.getProductNoInAttribute();
        modelProduct.UnitPrice = this.getFuelPrice().getUnitPrice();
        return modelProduct;
    }

    private static ServiceResponseGetDspConfiguration latestDSPConfigurationResponse;
    //ProductNo:Product
    private static HashMap<Integer, Product> productHashMap = new HashMap<>();

    /**
     * this is for the issue of typing chinese or other special char in fusion WebUI is not supported,
     * but may still want to show these friendly chars in FCM UI.
     * the key is the ProductNo which is the int as fusion WebUI constraint, and the value is the friendly name.
     */
    public static HashMap<Integer, String> PRODUCT_BOOK_FRIENDLY_NAME = new HashMap<>();

    public static void updateLatestProductBook(ServiceResponseGetDspConfiguration getDSPConfigurationResponse) {
        productHashMap.clear();
        latestDSPConfigurationResponse = getDSPConfigurationResponse;
        for (DeviceClass dc : latestDSPConfigurationResponse.getSingleFdcData().getDeviceClasses()
                ) {
            for (Product newPro : dc.getProducts()
                    ) {
                if (productHashMap.containsKey(newPro.getProductNoInAttribute()))
                    productHashMap.remove(newPro.getProductNoInAttribute());
                productHashMap.put(newPro.getProductNoInAttribute(), newPro);
            }
        }
    }

    public static void updateLatestProductBook(Integer productID, float fuelPrice) {
        for (Map.Entry<Integer, Product> productEntry : productHashMap.entrySet()) {
            if (productID == productEntry.getValue().getProductNoInAttribute()) {
                productEntry.getValue().setFuelPrice(fuelPrice);
            }
        }
    }

    public static List<Product> getLatestProducts() {
        List<Product> result = new ArrayList<>();
        result.addAll(productHashMap.values());
        return result;
    }

    public static Product findProductByProductNo(Integer productNo) {
        if (productHashMap.containsKey(productNo))
            return productHashMap.get(productNo);
        return null;
    }

    /**
     * get the fuel product name which configured in fusion webUI, typically it does not support chinese char and anyother special char.
     *
     * @param productNo
     * @return
     */
    public static String getLatestFusionLocalProductNameByProductNo(int productNo) {
        if (productHashMap.containsKey(productNo))
            return productHashMap.get(productNo).getProductName();
        return null;
    }
}
