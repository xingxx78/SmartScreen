package com.wayne.www.waynelib.fdc;


import com.wayne.www.waynelib.fdc.message.ServiceResponse;

/**
 * Created by Think on 4/25/2016.
 */
public interface OnFdcServiceResponseReceivedListener {
    void onServiceResponseReceived(FdcClient sender, ServiceResponse serviceResponse);
}
