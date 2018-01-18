package com.wayne.www.waynelib.webservice.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Shawn on 6/8/2017.
 */

public class PosTrx {
    @SerializedName("Id")
    private String id;

    //public enum PosTrxSource { Indoor, Outdoor }
    @SerializedName("TransactionSource")
    private int transactionSource;

    @SerializedName("TransactionType")
    private int transactionType;

    @SerializedName("Items")
    private List<PosTrxItem> items = new ArrayList<PosTrxItem>();

    @SerializedName("Discounts")
    private List<PosTrxDiscount> discounts = new ArrayList<PosTrxDiscount>();

    @SerializedName("Payments")
    private List<PosTrxMop> payments = new ArrayList<PosTrxMop>();


    @SerializedName("ReceiptId")
    private String receiptId;

//    @SerializedName("TerminalId")
//    private int terminalId;

    @SerializedName("ShiftId")
    private int shiftId;

    @SerializedName("TransactionInitTimeInPos")
    private String transactionInitTimeInPos;

    @SerializedName("TransactionArrivedAtServerTime")
    private String transactionArrivedAtServerTime;

    @SerializedName("NetAmount")
    private float netAmount;

    @SerializedName("GrossAmount")
    private float grossAmount;

    @SerializedName("CurrencyId")
    private String currencyId;

    @SerializedName("TransactionStatus")
    private int transactionStatus;

    @SerializedName("ServiceIdentityUserId")
    private String serviceIdentityUserId;

    @SerializedName("ByUser")
    private ServiceUser byUser;

    @SerializedName("CreatedFromSiteDevice")
    private SiteDevice createdFromSiteDevice;

    @SerializedName("TrxComment")
    private String trxComment;

    public String getId() {
        return this.id;
    }

    /**
     * @return like 2017-05-30 16:22:00
     */
    public String getTransactionInitTimeInPos() {
        return this.transactionInitTimeInPos;
    }

    public String getTransactionArrivedAtServerTime() {
        return this.transactionArrivedAtServerTime;
    }

    public List<PosTrxItem> getItems() {
        return this.items;
    }

    public List<PosTrxMop> getPayments() {
        return this.payments;
    }

    public String getReceiptId() {
        return this.receiptId;
    }

    public float getNetAmount() {
        return this.netAmount;
    }

    public float getGrossAmount() {
        return this.getNetAmount();
    }

    public ServiceUser getByUser() {
        return this.byUser;
    }

    public void addPosTrxItem(PosTrxItem posTrxItem) {
        this.items.add(posTrxItem);
    }

    public String getTransactionStatus() {
        //Open, Cancelled, Paid
        if (this.transactionStatus == 0)
            return "Open";
        else if (this.transactionStatus == 1)
            return "Cancelled";
        else if (this.transactionStatus == 2)
            return "Paid";
        else if (this.transactionStatus == 3)
            return "Refunded";
        return "Unknown(" + this.transactionStatus + ")";
    }

    public int getTransactionType() {
        return this.transactionType;
    }

    /**
     * public enum PosTrxSource { Indoor, Outdoor }
     *
     * @return
     */
    public int getTransactionSource() {
        return this.transactionSource;
    }

    public SiteDevice getCreatedFromSiteDevice() {
        return this.createdFromSiteDevice;
    }

    public String getTrxComment() {
        return this.trxComment;
    }

    public List<ClientRingUpPosItem> extractTo() {
        ArrayList<ClientRingUpPosItem> result = new ArrayList<>();
        for (int i = 0; i < this.getItems().size(); i++) {
            PosTrxItem pti = this.getItems().get(i);
            if (pti.getVoided()) {
                ClientRingUpPosItem voidingItem = new ClientRingUpPosItem();
                voidingItem.setVoidOperationForWhichLineNum(pti.getLineNum());
                result.add(voidingItem);
            } else {
                ClientRingUpPosItem clientSaleItem = new ClientRingUpPosItem();
                clientSaleItem.setPosItemUniqueId(pti.getPosItem().getId());
                clientSaleItem.setQty(pti.getQty());
                clientSaleItem.setFuelItemFdcReleaseTokenAttribute(pti.getFuelItemFdcReleaseTokenAttribute());
                clientSaleItem.setFuelItemFdcTransactionSeqNo(pti.getFuelItemFdcTransactionSeqNo());
                clientSaleItem.setFuelItemSoldOnPumpId(pti.getFuelItemSoldOnPumpId());
                clientSaleItem.setFuelItemSoldOnPumpNozzleId(pti.getFuelItemSoldOnPumpNozzleId());
                result.add(clientSaleItem);
            }
        }

        return result;
    }
}
