package com.wayne.www.waynelib.webservice;

/**
 * Created by Shawn on 8/16/2017.
 */

public interface OnProxyOnlineOfflineSwitchListener {
    public static final String State_Online  = "Online";
    public static final String State_Offline  = "Offline";
    void switchTo(String toState);
}
