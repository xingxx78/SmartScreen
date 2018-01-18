package com.wayne.www.waynelib.fdc;


import com.wayne.www.waynelib.fdc.message.FdcMessage;

/**
 * Created by Think on 4/25/2016.
 */
public interface OnFdcMessageReceivedListener {
    void onFdcMessageReceived(FdcClient sender, FdcMessage fdcMessage);
}
