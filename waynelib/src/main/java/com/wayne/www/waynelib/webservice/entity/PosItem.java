package com.wayne.www.waynelib.webservice.entity;


import com.google.gson.annotations.SerializedName;

import com.wayne.www.waynelib.NotifyPropertyChanged;

/**
 * Created by Think on 5/8/2016.
 */
public class PosItem extends NotifyPropertyChanged {
    @SerializedName("Id")
    private String id;

    @SerializedName("ItemId")
    private String itemId;

    @SerializedName("ChangesetId")
    private String changesetId;

    @SerializedName("ItemName")
    private String name;

    @SerializedName("ItemDepartmentId")
    private String itemDepartmentId;

    @SerializedName("UnitId")
    private String unitId;

    @SerializedName("Price")
    private float price;

    @SerializedName("BarCode")
    private String barCode;

    public String getBarCodeID() {
        return this.barCode;
    }

    @SerializedName("PLU")
    private String plu;

    @SerializedName("CreatedDateTime")
    private String createdDateTime;

    @SerializedName("IsFuelItem")
    private boolean isFuelItem;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return this.itemId;
    }

    public String getName() {
        return this.name;
    }

    public boolean getIsFuelItem() {
        return this.isFuelItem;
    }

    public float getPrice() {
        return this.price;
    }

    public PosItem() {
    }

    public PosItem(String name, String itemId, float price, String plu) {
        this.name = name;
        this.itemId = itemId;
        this.price = price;
        this.plu = plu;
    }

    public PosItem(String name, String itemId, String barCode, float price, boolean isFuelItem) {
        this.name = name;
        this.itemId = itemId;
        this.price = price;
        this.barCode = barCode;
        this.isFuelItem = isFuelItem;
    }

    // could multiple discount for an item.
    // tuple is: discount name : its percentage.
//    private List<Discount> discounts = new ArrayList<>();
//    private FuelSaleTransaction innerFuelSaleTransaction;

//    public void setInnerFuelSaleTransaction(FuelSaleTransaction value) {
//        this.innerFuelSaleTransaction = value;
//    }

//    public FuelSaleTransaction getInnerFuelSaleTransaction() {
//        if (this.getItemType() == ItemType.FuleItem)
//            return this.innerFuelSaleTransaction;
//        return null;
//    }

    //region Fule Item related properties
//    private Integer pumpNo;

//    public void setFuelItemPumpNo(int value) {
//        this.pumpNo = value;
//    }
//
//    public int getFuelItemPumpNo() {
//        return this.pumpNo;
//    }
//
//    private Integer transactionSeqNumber;
//
//    public void setFuelItemTransactionSeqNumber(int value) {
//        this.transactionSeqNumber = value;
//    }
//
//    public int getFuelItemTransactionSeqNumber() {
//        return this.transactionSeqNumber;
//    }
//
//    private Integer releaseToken;
//
//    public void setFuelItemReleaseToken(int value) {
//        this.releaseToken = value;
//    }
//
//    public int getFuelItemReleaseToken() {
//        return this.releaseToken;
//    }

    //endregion

//    public ItemType getItemType() {
//        return this.itemType;
//    }
//
//    public void setItemType(ItemType value) {
//        this.itemType = value;
//    }


//    public float getQty() {
//        return this.qty;
//    }
//
//    public void setQty(float value) {
//        this.qty = value;
//        super.notifyAllListeners(this, "Qty");
//    }


//
//    public void setPrice(float value) {
//        this.price = value;
//        super.notifyAllListeners(this, "Price");
//    }

    /* discount already excluded*/
//    public float getNetAmount() {
//        return this.qty * this.price - getDiscountAmount();
//    }
//
//    /* simply qty* price */
//    public float getGrossAmount() {
//        return this.qty * this.price;
//    }

//    public float getDiscountAmount() {
//        float discountAmount = 0;
//        for (int i = 0; i < this.discounts.size(); i++)
//            if (this.discounts.get(i).DiscountType == DiscountType.LineDiscount) {
//                discountAmount += this.discounts.get(i).Value;
//            } else if (this.discounts.get(i).DiscountType == DiscountType.Percentage) {
//                discountAmount += this.qty * this.price * this.discounts.get(i).Value / 100;
//            }
//
//        return discountAmount;
//    }

//    public boolean getVoided() {
//        return this.voided;
//    }
//
//    public void setVoid(boolean value) {
//        this.voided = value;
//        super.notifyAllListeners(this, "Void");
//    }

//    public PosItem(String name, float qty, float price) {
//        this.name = name;
//        this.setQty(qty);
//        this.setPrice(price);
//    }
//
//
//    public void addDiscount(Discount discount) {
//        this.discounts.add(discount);
//        super.notifyAllListeners(this, "Discount");
//    }
//
//    public List<Discount> getDiscounts() {
//        return this.discounts;
//    }
//
//    public static PosItem from(FuelSaleTransaction fuelSaleTransaction) {
//        String productName = "Unknown";
//        if (Product.getLatestProducts() != null)
//            for (int i = 0; i < Product.getLatestProducts().size(); i++)
//                if (Product.getLatestProducts().get(i).getProductNo() == fuelSaleTransaction.ProductNo) {
//                    productName = Product.getLatestProducts().get(i).getProductName();
//                    if (productName.contains("DIESEL"))
//                        productName = "柴油";
//                    else if (productName.contains("SYN 2000"))
//                        productName = "汽油93#";
//                    else if (productName.contains("SYN 5000"))
//                        productName = "汽油95#";
//                    else if (productName.contains("SYN 8000"))
//                        productName = "汽油98#";
//                    break;
//                }
//
//        //(F) indicate from FCM, just for UI.
//        PosItem posItem = new PosItem(productName + "(F)", fuelSaleTransaction.Volume, fuelSaleTransaction.UnitPrice);
//        posItem.setItemType(ItemType.FuleItem);
//        posItem.setFuelItemPumpNo(fuelSaleTransaction.PumpNo);
//        posItem.setFuelItemTransactionSeqNumber(fuelSaleTransaction.TransactionSeqNo);
//        posItem.setFuelItemReleaseToken(fuelSaleTransaction.ReleaseToken);
//        posItem.setInnerFuelSaleTransaction(fuelSaleTransaction);
//        return posItem;
//    }
}
