package com.wayne.www.waynelib.fdc.model;


import com.wayne.www.waynelib.Util;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.wayne.www.waynelib.fdc.message.DeviceClass;

/**
 * Created by Think on 3/9/2016.
 */
public class FuelSaleTransaction implements Serializable {
    public float Amount;
    public float Volume;
    public float UnitPrice;
    public int ProductNo;
    public int TransactionSeqNo;
    public Calendar TransactionTimeStamp;
    public FuelSaleTransactionState State;
    public int ReleaseToken;

    public int PumpNo;
    public int NozzleNo;

    // the related nozzle authed by which app sender(fdc client)
    public String AuthorisationApplicationSender;

    // the current trx locked by which fdc client, always null in current EMSG FC.
    public String LockingApplicationSender;

    // the current trx locked by which fdc client, like 110,109 and etc.
    public String ReservingDeviceId;

    public int getTransactionSeqNo() {
        return this.TransactionSeqNo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof FuelSaleTransaction))
            return false;

        FuelSaleTransaction target = (FuelSaleTransaction) obj;
        return target.NozzleNo == this.NozzleNo && target.PumpNo == this.PumpNo && target.Amount == this.Amount && target.Volume == this.Volume && target.UnitPrice == this.UnitPrice
                && target.ProductNo == this.ProductNo && target.TransactionSeqNo == this.TransactionSeqNo;
    }

    @Override
    public int hashCode() {
        return this.TransactionSeqNo;
    }

    public static FuelSaleTransaction convert(DeviceClass trxRelatedMsgDeviceClass) {
        FuelSaleTransaction modelTrx = new FuelSaleTransaction();
        int deviceId = Integer.parseInt(trxRelatedMsgDeviceClass.getDeviceId());
        int pumpNo = trxRelatedMsgDeviceClass.getPumpNo();
        String transactionSeqNo = trxRelatedMsgDeviceClass.getTransactionSeqNo();
        float volume = trxRelatedMsgDeviceClass.getVolume();
        float amount = trxRelatedMsgDeviceClass.getAmount();
        float unitPrice = trxRelatedMsgDeviceClass.getUnitPrice();
        int productNo = Integer.parseInt(trxRelatedMsgDeviceClass.getProductNo1());
        String productName = trxRelatedMsgDeviceClass.getProductName();
        // like android0
        String authorisedByWhichApplicationSender = trxRelatedMsgDeviceClass.getAuthorisationApplicationSender();
        String lockingApplicationSender = trxRelatedMsgDeviceClass.getLockingApplicationSender();
        String reservingDeviceId = trxRelatedMsgDeviceClass.getReservingDeviceId();
        int releaseTokenAttribute = -1;
        if (trxRelatedMsgDeviceClass.getReleaseTokenElement() != null)
            releaseTokenAttribute = Integer.parseInt(trxRelatedMsgDeviceClass.getReleaseTokenElement());

        // mapping to model
        modelTrx.TransactionSeqNo = Integer.parseInt(transactionSeqNo);
        modelTrx.Volume = volume;
        modelTrx.Amount = amount;
        modelTrx.UnitPrice = unitPrice;
        modelTrx.ProductNo = productNo;
        modelTrx.TransactionTimeStamp = Util.convertTimeStrToJavaDate(trxRelatedMsgDeviceClass.getStartTimeStamp(), "yyyy-MM-dd HH:mm:ss");
        modelTrx.ReleaseToken = releaseTokenAttribute;
        modelTrx.AuthorisationApplicationSender = authorisedByWhichApplicationSender;
        modelTrx.LockingApplicationSender = lockingApplicationSender;
        modelTrx.ReservingDeviceId = reservingDeviceId;

        modelTrx.PumpNo = deviceId;
        modelTrx.NozzleNo = trxRelatedMsgDeviceClass.getNozzleNo();
        if (trxRelatedMsgDeviceClass.getState().equals("Payable")) {
            modelTrx.State = FuelSaleTransactionState.Payable;
        } else if (trxRelatedMsgDeviceClass.getState().equals("Cleared")) {
            modelTrx.State = FuelSaleTransactionState.Cleared;
        } else if (trxRelatedMsgDeviceClass.getState().equals("Locked")) {
            modelTrx.State = FuelSaleTransactionState.Locked;
        }

        return modelTrx;
    }
}
