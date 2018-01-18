package com.wayne.www.waynelib.webservice;

import com.wayne.www.waynelib.Util;

/**
 * Created by Shawn on 6/1/2017.
 */

public class CloudServiceAddressBook {
    private static final CloudServiceAddressBook ourInstance = new CloudServiceAddressBook();

    public static CloudServiceAddressBook getInstance() {
        return ourInstance;
    }

    private CloudServiceAddressBook() {
    }

    static {
//        if (Util.isEmulator()) {
//            AuthServiceAddress = "http://10.0.2.2:1983";
//            ProductServiceAddress = "http://10.0.2.2:8900";
//            TransactionServiceAddress = "http://10.0.2.2:8123";
//            ReceiptServiceAddress = "http://10.0.2.2:8123";
//        } else {
//            AuthServiceAddress = "http://192.168.199.166:1983";
//            ProductServiceAddress = "http://192.168.199.166:8900";
//            TransactionServiceAddress = "http://192.168.199.166:8123";
//            ReceiptServiceAddress = "http://10.196.30.230:8899";
//        }
    }

    public static String AuthServiceAddress;
    public static String ProductServiceAddress;
    public static String TransactionServiceAddress;
    public static String ReceiptServiceAddress;
    public static String FusionServiceAddress;
    public static String ReportServiceAddress;
}
