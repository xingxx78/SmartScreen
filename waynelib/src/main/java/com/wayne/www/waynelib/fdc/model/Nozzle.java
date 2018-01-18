package com.wayne.www.waynelib.fdc.model;


import com.wayne.www.waynelib.NotifyPropertyChanged;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Think on 3/15/2016.
 */
public class Nozzle extends NotifyPropertyChanged {
    public Product Product;
    public int NozzleNo;
    private List<FuelSaleTransaction> transactions;
    private PumpOrNozzleState currentState = PumpOrNozzleState.Idle;

    public void setState(PumpOrNozzleState newState) {
        this.currentState = newState;
        super.notifyAllListeners(this, "State");
    }

    public PumpOrNozzleState getCurrentState() {
        return this.currentState;
    }

    /* get the first stacked trx on this nozzle*/
    public FuelSaleTransaction getTransaction() {
        if (this.transactions != null && this.transactions.size() > 0)
            return this.transactions.get(0);
        return null;
    }

    /* get the all stacked trxs on this nozzle*/
    public List<FuelSaleTransaction> getTransactions() {
        return this.transactions;
    }

    /* stack a pending for processing trx to this nozzle
    *  will have a check for 'newTrx' if already in stacked list, if duplicated, return false.
    *  otherwise, return true.
    * */
    public void stackOrUpdateTransaction(FuelSaleTransaction newTrx) {
        if (this.transactions == null) this.transactions = new ArrayList<>();
        for (int i = 0; i < this.transactions.size(); i++) {
            if (this.transactions.get(i).equals(newTrx)) {
                this.transactions.get(i).State = newTrx.State;
                this.transactions.get(i).AuthorisationApplicationSender = newTrx.AuthorisationApplicationSender;
                this.transactions.get(i).LockingApplicationSender = newTrx.LockingApplicationSender;
                this.transactions.get(i).ReservingDeviceId = newTrx.ReservingDeviceId;
                super.notifyAllListeners(this, "TransactionUpdated");
                return;
            }
        }

        this.transactions.add(newTrx);
        super.notifyAllListeners(this, "Transaction");
    }

    /* remove a trx from the stacked trx list.
    *  if found and removed, return True.
    *  otherwise, false.
    * */
    public boolean finishTransaction(FuelSaleTransaction trxToFinish) {
        if (this.transactions == null) return false;
        for (int i = 0; i < this.transactions.size(); i++) {
            if (this.transactions.get(i).equals(trxToFinish)) {
                this.transactions.remove(i);
                super.notifyAllListeners(this, "Transaction");
                return true;
            }
        }

        return false;
    }

    public void clearAllTransaction() {
        if (this.transactions != null) {
            this.transactions = null;
            super.notifyAllListeners(this, "Transaction");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Nozzle))
            return false;

        Nozzle target = (Nozzle) obj;
        if ((target.Product == null && this.Product != null) || (target.Product != null && this.Product == null))
            return false;
        if (target.Product == null)
            return target.NozzleNo == this.NozzleNo;
        return target.NozzleNo == this.NozzleNo && target.Product.equals(this.Product);


    }

    @Override
    public int hashCode() {
        return this.Product.hashCode() ^ this.NozzleNo;
    }

    public static Nozzle convert(com.wayne.www.waynelib.fdc.message.Nozzle fdcNozzle, Product product) {
        Nozzle modelNozzle = new Nozzle();
        modelNozzle.NozzleNo = fdcNozzle.getNozzleNo();
        modelNozzle.Product = product;
        return modelNozzle;
    }
}
