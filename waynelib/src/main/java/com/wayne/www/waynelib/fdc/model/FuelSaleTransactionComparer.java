package com.wayne.www.waynelib.fdc.model;

import java.util.Comparator;

/**
 * Created by Think on 4/30/2016.
 */
public class FuelSaleTransactionComparer implements Comparator<FuelSaleTransaction> {
    @Override
    public int compare(FuelSaleTransaction lhs, FuelSaleTransaction rhs) {
        return lhs.TransactionTimeStamp.compareTo(rhs.TransactionTimeStamp);
    }
}
